import 'package:client/logging/logger_factory.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:firebase_auth/firebase_auth.dart';

final _logger = getLogger('firebase_service');

class FirebaseService {
  static const Map<String, String> _errorMappings = {
    'email-already-in-use': 'There already exists an account with the given email address.',
    'invalid-email': 'Given email address is not valid.',
    'operation-not-allowed': 'Requested login method is not allowed. Enable it in the Firebase Console.',
    'user-disabled': 'User corresponding to given email address has been disabled.',
    'user-not-found': 'No user found for given email.',
    'weak-password': 'Password should be at least 6 characters',
    'wrong-password': 'The provided password is invalid for the given user.'
  };

  Future<AuthenticationResult> loginEmailPassword(final FirebaseAuth auth, final String email, final String password) async {
    _logger.i('Authenticating user "$email" via "email/password" method...');

    try {
      final loginInfo = await auth.signInWithEmailAndPassword(email: email, password: password);
      return AuthenticationResult.success(loginInfo.user!);
    } on FirebaseAuthException catch (e) {
      _logger.e('Login failed: ${e.message}; Error code: ${e.code}');
      return AuthenticationResult.failure(_errorMappings[e.code] ?? '${e.message ?? 'Unspecified login error'} ("${e.code}")');
    } catch (e) {
      _logger.e('Unexpected error: $e');
      return AuthenticationResult.failure('Unexpected login error: $e');
    }
  }

  Future<void> logout(final FirebaseAuth auth) async {
    _logger.i('Logging out currently logged in user');
    auth.signOut();
  }
}

class AuthenticationResult {
  const AuthenticationResult(this.isSuccess, this.loggedInUser, this.errorMessage);

  const AuthenticationResult.success(final FirebaseUser loggedInUser) : this(true, loggedInUser, null);

  const AuthenticationResult.failure(final String errorMessage) : this(false, null, errorMessage);

  final bool isSuccess;
  final String? errorMessage;
  final FirebaseUser? loggedInUser;
}