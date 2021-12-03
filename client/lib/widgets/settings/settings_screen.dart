import 'dart:convert';

import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('settings_screen');

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({final Key? key}) : super(key: key);

  static const decoder = JsonDecoder();
  static const encoder = JsonEncoder.withIndent('  ');

  static const routeName = '/settings';

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> with LogMessagePreparer, StringLocalizer {
  bool _dataFetched = false;
  FirebaseUser? _currentUser;
  Map<String, dynamic>? _claims;

  void _fetchData() {
    final auth = Provider.of<AuthProvider>(context, listen: false);
    _currentUser = auth.currentUser;
    _currentUser?.getIdTokenResult().then((final value) {
      setState(() {
        _claims = value.claims;
        _dataFetched = true;
      });
    });
  }

  String _prettyPrintMap(final Map<String, dynamic>? map) {
    return SettingsScreen.encoder.convert(map);
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    if (_currentUser == null) {
      _fetchData();
    }

    final uiStrings = getLocalizedStrings(context);
    return Scaffold(
      appBar: const MainAppBar(),
      drawer: const AppDrawer(),
      body: Center(
        child: _currentUser == null || !_dataFetched
            ? Text(uiStrings.settingsScreen_placeholder_text)
            : Padding(
                padding: const EdgeInsets.all(20),
                child: SelectableText(
                  'Email: ${_currentUser!.email}\n'
                  'Firebase ID: ${_currentUser!.uid}\n'
                  'Is Admin: ${_claims?.containsKey('ROLE_ADMIN')}\n'
                  'Claims:\n${_prettyPrintMap(_claims)}',
                ),
              ),
      ),
    );
  }
}
