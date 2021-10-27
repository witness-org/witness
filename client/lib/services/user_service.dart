import 'dart:convert';

import 'package:client/logging/logger_factory.dart';
import 'package:client/models/user/user.dart';
import 'package:client/services/base_service.dart';
import 'package:http/http.dart' as http;

final _logger = getLogger('user_service');

class UserService extends BaseService {
  Future<String?> createUser(final String email, final String password) async {
    final requestUri = getUri('user/register');
    _logger
      ..i('Delegating creation of user "email" to server')
      ..i('POST $requestUri');

    await Future<void>.delayed(const Duration(seconds: 1));

    final httpHeaders = getHttpHeaders(jsonContent: true);
    final payload = {
      'username': email,
      'email': email,
      'sex': 'FEMALE',
      'height': 165,
      'password': password,
    };

    final response = await http.post(
      requestUri,
      headers: httpHeaders,
      body: json.encode(payload),
    );

    if (response.statusCode == 201) {
      final responseMap = decodeResponse<Map<String, dynamic>>(response);
      // ignore: unused_local_variable, just an example on how to deserialize from response using auto-generated model
      final returnedUser = User.fromJson(responseMap);
      return null;
    } else {
      return response.body;
    }
  }
}
