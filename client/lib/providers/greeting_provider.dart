import 'package:client/models/greeting.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/services/greeting_service.dart';
import 'package:flutter/material.dart';

class GreetingProvider with ChangeNotifier {
  GreetingProvider(this._auth);

  GreetingProvider.empty() : this(null);

  GreetingProvider.fromProviders(final AuthProvider auth) : this(auth);

  final _greetingService = GreetingService();
  final AuthProvider? _auth; // ignore: unused_field

  Future<String> getPublicData() async {
    return _greetingService.getPublicData();
  }

  Future<String> getRegisteredData(final String name, [final bool useToken = true]) async {
    return _greetingService.getRegisteredData(name, useToken ? await _auth?.getToken() : null);
  }

  Future<String> getPremiumData() async {
    return _greetingService.getPremiumData(await _auth?.getToken());
  }

  Future<String> getAdminData() async {
    return _greetingService.getAdminData(await _auth?.getToken());
  }

  Future<String> getPremiumOrAdminData() async {
    return _greetingService.getPremiumOrAdminData(await _auth?.getToken());
  }

  Future<String> postData(final Greeting data) async {
    return _greetingService.postData(data, await _auth?.getToken());
  }
}
