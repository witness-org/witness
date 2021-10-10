import 'dart:convert';
import 'dart:io';

import 'package:client/logging/logger_factory.dart';
import 'package:client/models/user/user.dart';
import 'package:http/http.dart' as http;

final _logger = getLogger('user_service');

class UserService {
  Future<String?> createUser(final String email, final String password) async {
    _logger
      ..i('Delegating creation of user "email" to server')
      ..i('POST http://10.0.2.2:8080/user/register');

    await Future<void>.delayed(const Duration(seconds: 1));

    final url = Uri.parse('http://10.0.2.2:8080/user/register');
    final payload = {
      'username': email,
      'email': email,
      'sex': 'FEMALE',
      'height': 165,
      'password': password,
    };

    final response = await http.post(
      url,
      headers: {'Content-Type': ContentType.json.toString()},
      body: json.encode(payload),
    );

    if (response.statusCode == 201) {
      final responseMap = json.decode(response.body) as Map<String, dynamic>;
      // ignore: unused_local_variable, just an example on how to deserialize from response using auto-generated model
      final returnedUser = User.fromJson(responseMap);
    } else {
      return response.body;
    }
  }
}
