import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('statistics_screen');

class StatisticsScreen extends StatelessWidget with LogMessagePreparer {
  const StatisticsScreen({final Key? key}) : super(key: key);

  static const routeName = '/statistics';

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return const Scaffold(
      appBar: MainAppBar(),
      drawer: AppDrawer(),
      body: Center(
        child: Text('Statistics'),
      ),
    );
  }
}
