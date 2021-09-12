import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('workout_overview_screen');

class WorkoutOverviewScreen extends StatelessWidget {
  static const routeName = '/workout-overview';

  const WorkoutOverviewScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return Scaffold(
      appBar: MainAppBar(),
      drawer: AppDrawer(),
      body: Center(
        child: Text('Workout overview'),
      ),
    );
  }
}
