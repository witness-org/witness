import 'dart:convert';

import 'package:client/logging/logger_factory.dart';
import 'package:client/models/greeting.dart';
import 'package:client/services/base_service.dart';
import 'package:http/http.dart' as http;

final _logger = getLogger('greeting_service');

class GreetingService extends BaseService {
  Future<String> getPublicData() async {
    final requestUri = getUri('greeting/public');
    _logger.i('GET $requestUri');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final response = await http.get(requestUri);
    return response.body;
  }

  Future<String> getRegisteredData(final String name, final String? token) async {
    final requestUri = getUri('greeting', queryParameters: {'name': name});
    _logger.i('GET $requestUri');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final httpHeaders = getHttpHeaders(authorization: token);
    final response = await http.get(requestUri, headers: httpHeaders);
    return response.body;
  }

  Future<String> getPremiumData(final String? token) async {
    final requestUri = getUri('greeting/premiumData');
    _logger.i('GET $requestUri');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final httpHeaders = getHttpHeaders(authorization: token);
    final response = await http.get(requestUri, headers: httpHeaders);
    return response.body;
  }

  Future<String> getAdminData(final String? token) async {
    final requestUri = getUri('greeting/adminData');
    _logger.i('GET $requestUri');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final httpHeaders = getHttpHeaders(authorization: token);
    final response = await http.get(requestUri, headers: httpHeaders);
    return response.body;
  }

  Future<String> getPremiumOrAdminData(final String? token) async {
    final requestUri = getUri('greeting/premiumOrAdminData');
    _logger.i('GET $requestUri');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final httpHeaders = getHttpHeaders(authorization: token);
    final response = await http.get(requestUri, headers: httpHeaders);
    return response.body;
  }

  Future<String> postData(final Greeting greeting, final String? token) async {
    final requestUri = getUri('greeting');
    _logger.i('POST $requestUri');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    final httpHeaders = getHttpHeaders(authorization: token, jsonContent: true);
    final payload = json.encode(greeting);
    final response = await http.post(requestUri, headers: httpHeaders, body: payload);
    return response.body;
  }
}
