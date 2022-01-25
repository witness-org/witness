import 'dart:async';

import 'package:client/logging/logger_factory.dart';
import 'package:client/services/firebase_service.dart';
import 'package:client/services/server_response.dart';
import 'package:client/services/user_service.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:injector/injector.dart';

final _logger = getLogger('auth_provider');

/// `User` should represent our own user model. In order to avoid confusion because FlutterFire's representation is also called
/// `User`, we want to refer to Firebase users only via this `FirebaseUser` type alias.
typedef FirebaseUser = User;

class AuthProvider with ChangeNotifier {
  static final Injector _injector = Injector.appInstance;
  late final FirebaseService _firebaseService = _injector.get<FirebaseService>();
  late final UserService _userService = _injector.get<UserService>();

  FirebaseAuth? _firebaseAuth;
  FirebaseUser? _loggedInUser;

  StreamSubscription<FirebaseUser?>? _userStateStream;

  FirebaseUser? get currentUser {
    return _loggedInUser;
  }

  bool get isAuthenticated {
    return _loggedInUser != null;
  }

  Future<String?> getToken() async {
    return _loggedInUser?.getIdToken();
  }

  Future<ServerResponse<void, String>> signUp(final String email, final String password) async {
    // signing up is a two-step process:
    //   - create the user via the server (which, in turn, creates a user at Firebase
    //   - sign in and receive authentication token via FlutterFire
    return _firebaseAuthAction(
      (final firebaseAuth) async {
        _logger.i('Signing up and logging in new user "$email"');
        final createResponse = await _userService.createUser(email, password);
        if (createResponse.isSuccessAndResponse) {
          return login(email, password);
        } else {
          _logger.e('Could not create user: ${createResponse.error}');
          return createResponse;
        }
      },
    );
  }

  Future<ServerResponse<FirebaseUser, String>> login(final String email, final String password) async {
    return _firebaseAuthAction(
      (final firebaseAuth) async {
        _logger.i('Trying to login user "$email"');
        return _performLogin(() => _firebaseService.loginEmailPassword(firebaseAuth, email, password));
      },
    );
  }

  Future<void> logout() async {
    return await _firebaseAuthAction((final firebaseAuth) async {
      if (_loggedInUser == null) {
        _logger.i('Aborting logout process because there is currently no signed in user.');
        return;
      }

      _logger.i('Logging out user with ID "${_loggedInUser!.uid}"');
      await _firebaseService.logout(firebaseAuth);
    });
  }

  Future<void> reloadAuthentication() async {
    await _initializeFlutterFire();
    _listenToUserChangeStream();
  }

  Future<void> _initializeFlutterFire() async {
    if (_firebaseAuth != null) {
      return;
    }

    _firebaseAuth = await _injector.get<Future<FirebaseAuth>>();
  }

  void _listenToUserChangeStream() {
    if (_userStateStream != null) {
      return;
    }

    _firebaseAuthAction(
      (final firebaseAuth) {
        _logger.i('Setting up listener to Firebase user state stream');
        _userStateStream = firebaseAuth.authStateChanges().distinct().listen(
          (final FirebaseUser? user) {
            _logger.i('User state stream: received onData event');
            if (user != null) {
              _logger.v('User ${user.email} is currently logged in');
              _loggedInUser = user;
            } else {
              _logger.v('User is or has logged out');
              _loggedInUser = null;
            }
            notifyListeners();
          },
          onError: (final Object error, final StackTrace stacktrace) {
            _logger.e('User state stream: received onError event: $error');
          },
        );
      },
    );
  }

  Future<ServerResponse<FirebaseUser, String>> _performLogin(final Future<ServerResponse<FirebaseUser, String>> Function() loginDelegate) async {
    final loginResult = await loginDelegate();
    if (loginResult.isSuccessAndResponse) {
      _loggedInUser = loginResult.success;
      notifyListeners();
    }
    return loginResult;
  }

  T _firebaseAuthAction<T>(final T Function(FirebaseAuth authenticationService) action) {
    if (_firebaseAuth == null) {
      _logger.w('Cannot perform login as authentication service has not been initialized yet');
      throw Exception('Invalid State: Authentication Service not initialized.');
    }

    return action(_firebaseAuth!);
  }

  @override
  void dispose() {
    super.dispose();
    _userStateStream?.cancel();
  }
}
