import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';

final _logger = getLogger('workout_overview_screen');

class WorkoutOverviewScreen extends StatelessWidget {
  static const routeName = '/workout-overview';
  final DateTime _workoutDay;

  const WorkoutOverviewScreen(this._workoutDay, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return Scaffold(
      appBar: MainAppBar(currentlyViewedDate: _workoutDay.dateOnly()),
      drawer: AppDrawer(),
      floatingActionButton: _WorkoutFloatingActionButton(),
      body: Center(
        child: Text('Workout overview for $_workoutDay'),
      ),
    );
  }
}

class _WorkoutFloatingActionButton extends StatefulWidget {
  const _WorkoutFloatingActionButton({Key? key}) : super(key: key);

  @override
  _WorkoutFloatingActionButtonState createState() => _WorkoutFloatingActionButtonState();
}

class _WorkoutFloatingActionButtonState extends State<_WorkoutFloatingActionButton> {
  var _isOpened = false;

  set isOpened(bool isOpened) {
    setState(() => _isOpened = isOpened);
  }

  bool get isOpened {
    return _isOpened;
  }

  SpeedDialChild _buildSpeedDialChild(ThemeData theme, IconData icon, String label, void Function() onTap) {
    return SpeedDialChild(
      backgroundColor: theme.colorScheme.secondary,
      foregroundColor: theme.colorScheme.onSecondary,
      child: Icon(icon),
      label: label,
      onTap: onTap,
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return SpeedDial(
      backgroundColor: theme.colorScheme.secondary,
      foregroundColor: theme.colorScheme.onSecondary,
      renderOverlay: false,
      isOpenOnStart: false,
      onOpen: () => isOpened = true,
      onClose: () => isOpened = false,
      icon: isOpened ? Icons.close : Icons.add,
      spacing: 2,
      children: [
        _buildSpeedDialChild(theme, Icons.app_registration, "Log Exercise", () {}),
        _buildSpeedDialChild(theme, Icons.copy, "Copy Exercise", () {}),
        _buildSpeedDialChild(theme, Icons.article_outlined, "New Training program", () {}),
        _buildSpeedDialChild(theme, Icons.menu_book, "New Workout", () {}),
        _buildSpeedDialChild(theme, Icons.fitness_center, "New Exercise", () {}),
      ],
    );
  }
}
