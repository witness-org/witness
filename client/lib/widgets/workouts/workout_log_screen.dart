import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workout_log_form_input.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/providers/workout_log_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/painting.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
import 'package:provider/provider.dart';
import 'package:timezone/timezone.dart';

final _logger = getLogger('training_log_screen');

class WorkoutLogScreen extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const WorkoutLogScreen(this._date, {final Key? key = _key}) : super(key: key);

  static const Key _key = Key('training_log_screen');
  static const routeName = '/training-log';
  final TZDateTime _date;

  Future<void> _fetchWorkoutLogsByDate(final BuildContext context, final TZDateTime date) async {
    _logger.v(prepare('_fetchWorkoutLogsByDate()'));
    await Provider.of<WorkoutLogProvider>(context, listen: false).fetchWorkoutLogsByDate(date);
  }

  Widget _buildHeader(final BuildContext context, final StringLocalizations uiStrings, final TZDateTime date) {
    return Container(
      margin: const EdgeInsets.only(bottom: 20.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            // TODO(raffaelfoidl): fix UI when text too long
            _date.getStringRepresentation(
              todayText: uiStrings.dateFormat_today,
              yesterdayText: uiStrings.dateFormat_yesterday,
            ),
            style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
        ],
      ),
    );
  }

  Widget _buildWorkoutLogList(final BuildContext context, final StringLocalizations uiStrings, final TZDateTime date) {
    _logger.v(prepare('_buildWorkoutLogList()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchWorkoutLogsByDate(context, date),
        builder: (final _, final snapshot) {
          if (snapshot.hasError) {
            return Center(
              child: Text(
                uiStrings.workoutLogScreen_workoutLogList_errorMessage(snapshot.error.toString()),
                textAlign: TextAlign.center,
              ),
            );
          } else {
            return snapshot.waitSwitch(
              RefreshIndicator(
                onRefresh: () => _fetchWorkoutLogsByDate(context, date),
                child: Consumer<WorkoutLogProvider>(
                  builder: (final _, final workoutLogData, final __) {
                    _logger.v(prepare('_buildWorkoutLogList.Consumer.builder()'));
                    final logs = workoutLogData.getWorkoutLogsByDate(date);
                    return logs.isEmpty
                        ? Center(
                            child: Text(uiStrings.workoutLogScreen_placeholder),
                          )
                        : Scrollbar(
                            isAlwaysShown: true,
                            child: ListView.builder(
                              itemCount: logs.length,
                              itemBuilder: (final _, final index) {
                                final log = logs[index];
                                return Column(
                                  children: [
                                    _WorkoutLogItem(index, log),
                                    const Divider(),
                                  ],
                                );
                              },
                            ),
                          );
                  },
                ),
              ),
            );
          }
        },
      ),
    );
  }

  Widget _buildScreen(final BuildContext context, final StringLocalizations uiStrings, final TZDateTime date) {
    _logger.v(prepare('_buildScreen()'));
    return Scaffold(
      appBar: MainAppBar(currentlyViewedDate: date),
      drawer: const AppDrawer(),
      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: const [
          _WorkoutFloatingActionButton(),
          SizedBox(height: 15),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildHeader(context, uiStrings, date),
            _buildWorkoutLogList(context, uiStrings, date),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return _buildScreen(context, uiStrings, _date);
  }
}

class _WorkoutFloatingActionButton extends StatefulWidget {
  const _WorkoutFloatingActionButton({final Key? key}) : super(key: key);

  @override
  _WorkoutFloatingActionButtonState createState() => _WorkoutFloatingActionButtonState();
}

class _WorkoutFloatingActionButtonState extends State<_WorkoutFloatingActionButton> with StringLocalizer {
  var _isOpened = false;

  set isOpened(final bool isOpened) {
    setState(() => _isOpened = isOpened);
  }

  bool get isOpened {
    return _isOpened;
  }

  SpeedDialChild _buildSpeedDialChild(final ThemeData theme, final IconData icon, final String label, final void Function() onTap) {
    return SpeedDialChild(
      backgroundColor: theme.colorScheme.secondary,
      foregroundColor: theme.colorScheme.onSecondary,
      child: Icon(icon),
      label: label,
      onTap: onTap,
    );
  }

  @override
  Widget build(final BuildContext context) {
    final theme = Theme.of(context);
    final uiStrings = getLocalizedStrings(context);
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
        _buildSpeedDialChild(theme, Icons.add, uiStrings.workoutLogScreen_speedDial_logNewWorkout, () {}),
        _buildSpeedDialChild(theme, Icons.content_paste, uiStrings.workoutLogScreen_speedDial_logWorkoutFromProgram, () {}),
        _buildSpeedDialChild(theme, Icons.copy, uiStrings.workoutLogScreen_speedDial_copyWorkout, () {}),
      ],
    );
  }
}

class _WorkoutLogItem extends StatefulWidget with LogMessagePreparer {
  _WorkoutLogItem(this.index, this.workoutLog, {final Key? key}) : super(key: key);
  final int index;
  final WorkoutLog workoutLog;

  @override
  _WorkoutLogItemState createState() => _WorkoutLogItemState();
}

class _WorkoutLogItemState<T extends _WorkoutLogItem> extends State<T> with StringLocalizer, LogMessagePreparer {
  final _formKey = GlobalKey<FormState>();
  WorkoutLogFormInput _formInput = WorkoutLogFormInput();
  int _index = -1;

  @override
  void initState() {
    super.initState();

    _formInput = WorkoutLogFormInput.editForm(widget.workoutLog);
    _index = widget.index;
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              uiStrings.workoutLogScreen_workoutLog_heading(_index + 1),
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 16,
              ),
            ),
            TextButton.icon(
              icon: const Icon(Icons.timer),
              label: Text(
                uiStrings.workoutLogScreen_workoutLog_duration(_formInput.durationMinutes ?? 0),
                style: const TextStyle(
                  fontSize: 16,
                ),
              ),
              onPressed: () => showDialog<String>(
                context: context,
                builder: (final BuildContext context) => _WorkoutDurationDialog(_formKey, _formInput, _submitForm),
              ),
            ),
          ],
        ),
      ],
    );
  }

  Future<void> _submitForm(final BuildContext context, final StringLocalizations uiStrings, final WorkoutLogFormInput formInput) async {
    final provider = Provider.of<WorkoutLogProvider>(context, listen: false);

    if (_formKey.currentState == null || !_formKey.currentState!.validate()) {
      return;
    }

    _formKey.currentState!.save();

    await ProgressLoader().show(context);
    final response = await provider.patchWorkoutLogDuration(formInput);
    await ProgressLoader().dismiss();

    if (response.isSuccessAndResponse) {
      if (!mounted) {
        return;
      }

      setState(() {});
    }
  }
}

class _WorkoutDurationDialog extends StatelessWidget with StringLocalizer, LogMessagePreparer {
  const _WorkoutDurationDialog(this._formKey, this._formInput, this._submitFormFunction);

  final GlobalKey<FormState> _formKey;
  final WorkoutLogFormInput _formInput;
  final Future<void> Function(BuildContext, StringLocalizations, WorkoutLogFormInput) _submitFormFunction;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return AlertDialog(
      title: Text(uiStrings.workoutLogScreen_workoutDurationDialog_title),
      content: Form(
        key: _formKey,
        child: TextFormField(
          decoration: InputDecoration(
            icon: const Icon(Icons.timer),
            hintText: uiStrings.workoutLogScreen_workoutDurationDialog_durationHint,
            labelText: uiStrings.workoutLogScreen_workoutDurationDialog_durationLabel,
            border: const OutlineInputBorder(),
            suffix: Text(uiStrings.workoutLogScreen_workoutDurationDialog_durationSuffix),
          ),
          keyboardType: TextInputType.number,
          initialValue: _formInput.durationMinutes.toString(),
          validator: (final value) {
            if (value != null && int.parse(value) < 0) {
              return uiStrings.workoutLogScreen_workoutDurationDialog_durationError;
            }

            return null;
          },
          onSaved: (final value) => _formInput.durationMinutes = value != null ? int.parse(value) : null,
        ),
      ),
      actions: <Widget>[
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text(uiStrings.workoutLogScreen_workoutDurationDialog_cancel),
        ),
        TextButton(
          onPressed: () {
            _submitFormFunction(context, uiStrings, _formInput);
            Navigator.pop(context);
          },
          child: Text(uiStrings.workoutLogScreen_workoutDurationDialog_confirm),
        ),
      ],
    );
  }
}
