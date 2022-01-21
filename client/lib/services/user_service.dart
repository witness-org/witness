import 'dart:convert';

import 'package:client/logging/logger_factory.dart';
import 'package:client/models/user/user.dart';
import 'package:client/services/base_service.dart';
import 'package:client/services/server_response.dart';
import 'package:http/http.dart' as http;

final _logger = getLogger('user_service');

class UserService extends BaseService {
  @override
  String get targetResource => "users";

  Future<ServerResponse<User, String>> createUser(final String email, final String password) async {
    final requestUri = getUri('');
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

    final response = await http.post(requestUri, headers: httpHeaders, body: json.encode(payload));
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 201) {
      return ServerResponse.success(User.fromJson(responseMap));
    } else {
      _logger.e('Could not register new user: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }
}
