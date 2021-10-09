import 'dart:convert';
import 'dart:io';

import 'package:client/logging/logger_factory.dart';
import 'package:client/models/greeting.dart';
import 'package:http/http.dart' as http;

final _logger = getLogger('greeting_service');

class GreetingService {
  Future<String> getPublicData() async {
    _logger.i('GET http://10.0.2.2:8080/greeting/public');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final url = Uri.parse('http://10.0.2.2:8080/greeting/public');
    final response = await http.get(url);
    return response.body;
  }

  Future<String> getRegisteredData(final String name, final String? token) async {
    _logger.i('GET http://10.0.2.2:8080/greeting');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final url = Uri.http('10.0.2.2:8080', '/greeting', <String, String>{'name': name});
    final response = await http.get(url, headers: _getHeaders(token));
    return response.body;
  }

  Future<String> getPremiumData(final String? token) async {
    _logger.i('GET http://10.0.2.2:8080/greeting/premiumData');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final url = Uri.parse('http://10.0.2.2:8080/greeting/premiumData');
    final response = await http.get(url, headers: _getHeaders(token));
    return response.body;
  }

  Future<String> getAdminData(final String? token) async {
    _logger.i('GET http://10.0.2.2:8080/greeting/adminData');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final url = Uri.parse('http://10.0.2.2:8080/greeting/adminData');
    final response = await http.get(url, headers: _getHeaders(token));
    return response.body;
  }

  Future<String> getPremiumOrAdminData(final String? token) async {
    _logger.i('GET http://10.0.2.2:8080/greeting/premiumOrAdminData');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final url = Uri.parse('http://10.0.2.2:8080/greeting/premiumOrAdminData');
    final response = await http.get(url, headers: _getHeaders(token));
    return response.body;
  }

  Future<String> postData(final Greeting greeting, final String? token) async {
    _logger.i('POST http://10.0.2.2:8080/greeting');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final url = Uri.parse('http://10.0.2.2:8080/greeting');
    final payload = json.encode(greeting);
    final response = await http.post(url, headers: _getHeaders(token, true), body: payload);
    return response.body;
  }

  Map<String, String> _getHeaders(final String? token, [final bool jsonPayload = false]) {
    final headers = {HttpHeaders.authorizationHeader: 'Bearer $token'};
    if (jsonPayload) {
      headers['Content-Type'] = ContentType.json.toString();
    }
    return headers;
  }
}
