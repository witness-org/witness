import 'package:client/logging/logger_factory.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('auth_provider');

class AuthProvider with ChangeNotifier {
  String? _userId;
  String? _token;
  DateTime? _expirationDate;

  String? get token {
    return _token;
  }

  String? get userId {
    return _userId;
  }

  DateTime? get expirationDate {
    return _expirationDate;
  }

  bool get isAuthenticated {
    if (_userId == null || _token == null || expirationDate == null) {
      return false;
    }

    return DateTime.now().isBefore(_expirationDate!);
  }

  Future<void> signUp(final String email, final String password) async {
    _logger.i('Signing up new user "$email"');
    return _authenticate(email, password, 'https://api.my-service.at/sign-up');
  }

  Future<void> login(final String email, final String password) async {
    _logger.i('Logging user "$email" in');
    return _authenticate(email, password, 'https://api.my-service.at/login');
  }

  Future<void> logout() async {
    _logger.i('Logging out user with id "$_userId"');
    _token = null;
    _userId = null;
    _expirationDate = null;

    notifyListeners();
  }

  Future<void> _authenticate(final String email, final String password, final String url) async {
    _logger.d('Authenticating user "$email" with password "$password"');

    await Future<void>.delayed(const Duration(seconds: 1));

    _token = 'fake-token';
    _userId = 'stub-user';
    _expirationDate = DateTime.now().add(const Duration(hours: 1));

    notifyListeners();
  }
}
