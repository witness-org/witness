import 'package:client/logging/logger_factory.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/services/base_service.dart';
import 'package:client/services/server_response.dart';
import 'package:firebase_auth/firebase_auth.dart';

final _logger = getLogger('firebase_service');

class FirebaseService extends BaseService {
  @override
  String get targetResource => ''; // required by contract, but its value is not used since this service does not utilize `getUri` from base

  Future<ServerResponse<FirebaseUser, String?>> loginEmailPassword(final FirebaseAuth auth, final String email, final String password) async {
    _logger.i('Authenticating user "$email" via "email/password" method...');

    try {
      final loginInfo = await auth.signInWithEmailAndPassword(email: email, password: password);
      return ServerResponse.success(loginInfo.user!);
    } on FirebaseAuthException catch (e) {
      _logger.e('Login failed: ${e.message}; Error code: ${e.code}');
      return ServerResponse.failure(e.code);
    } catch (e) {
      _logger.e('Unexpected error: $e');
      return ServerResponse.failure('Unexpected login error: $e');
    }
  }

  Future<void> logout(final FirebaseAuth auth) async {
    _logger.i('Logging out currently logged in user');
    auth.signOut();
  }
}
