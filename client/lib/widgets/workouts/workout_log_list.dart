import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/common/error_key_translator.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/workouts/workout_log_item.dart';
import 'package:flutter/material.dart';
import 'package:client/providers/workout_log_provider.dart';
import 'package:provider/provider.dart';
import 'package:timezone/timezone.dart';

final _logger = getLogger('workout_log_list');

class WorkoutLogList extends StatefulWidget {
  const WorkoutLogList(final this._date, final this._error, final this._resetErrorAction, final this._handleErrorAction, {final Key? key})
      : super(key: key);

  final TZDateTime _date;
  final String? _error;
  final void Function() _resetErrorAction;
  final void Function(String error) _handleErrorAction;

  @override
  State createState() => _WorkoutLogListState();
}

class _WorkoutLogListState extends State<WorkoutLogList> with StringLocalizer, LogMessagePreparer, ErrorKeyTranslator {
  Future<void>? _fetchWorkoutLogsByDayResult;

  Future<void> _fetchWorkoutLogsByDay(final WorkoutLogProvider provider, final TZDateTime date) async {
    _logger.v(prepare('_fetchWorkoutLogsByDay()'));
    provider
        .fetchWorkoutLogsByDay(date)
        .then((final _) => widget._resetErrorAction())
        .onError((final error, final stackTrace) => widget._handleErrorAction(error.toString()));
  }

  /// Wraps the [child] widget in a widget that is always scrollable without scroll glow. This is needed to display a widget that is not scrollable by
  /// default in a [RefreshIndicator] without losing the ability to refresh as the [RefreshIndicator] only works with scrollable widgets.
  Widget _buildRefreshableWidget(final double height, final Widget child) {
    return ScrollConfiguration(
      behavior: const ScrollBehavior().copyWith(overscroll: false),
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        child: SizedBox(
          height: height,
          child: child,
        ),
      ),
    );
  }

  Widget _buildFallbackWidget(final StringLocalizations uiStrings, final String? error) {
    return Padding(
      padding: const EdgeInsets.all(6),
      child: Center(
        child: Text(
          error != null ? uiStrings.workoutLogScreen_workoutLogList_errorMessage(error) : uiStrings.workoutLogScreen_placeholder,
          textAlign: TextAlign.center,
        ),
      ),
    );
  }

  Widget _buildWorkoutLogList(final StringLocalizations uiStrings, final WorkoutLogProvider provider, final TZDateTime date) {
    _logger.v(prepare('_buildWorkoutLogList()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchWorkoutLogsByDayResult,
        builder: (final _, final snapshot) {
          return snapshot.waitSwitch(
            RefreshIndicator(
              onRefresh: () => _fetchWorkoutLogsByDay(provider, date),
              child: Consumer<WorkoutLogProvider>(
                builder: (final _, final workoutLogData, final __) {
                  _logger.v(prepare('_buildWorkoutLogList.Consumer.builder()'));
                  final logs = workoutLogData.getWorkoutLogsByDay(date);
                  return logs.isEmpty || widget._error != null
                      ? _buildRefreshableWidget(
                          MediaQuery.of(context).size.height - 300,
                          _buildFallbackWidget(uiStrings, widget._error),
                        )
                      : Scrollbar(
                          child: ListView.builder(
                            itemCount: logs.length,
                            itemBuilder: (final _, final index) {
                              final log = logs[index];
                              return Padding(
                                padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
                                child: Column(
                                  children: [
                                    WorkoutLogItem(index, log),
                                    const Divider(),
                                  ],
                                ),
                              );
                            },
                          ),
                        );
                },
              ),
            ),
            errorWidget: (final error) => _buildFallbackWidget(uiStrings, translate(uiStrings, error.toString())),
          );
        },
      ),
    );
  }

  Widget _buildHeader(final StringLocalizations uiStrings, final TZDateTime date) {
    return Container(
      margin: const EdgeInsets.only(bottom: 20.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
            child: Text(
              // TODO(raffaelfoidl): fix UI when text too long
              widget._date.getStringRepresentation(
                todayText: uiStrings.dateFormat_today,
                yesterdayText: uiStrings.dateFormat_yesterday,
              ),
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }

  @override
  void initState() {
    super.initState();
    _fetchWorkoutLogsByDayResult = _fetchWorkoutLogsByDay(Provider.of<WorkoutLogProvider>(context, listen: false), widget._date);
  }

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    final provider = Provider.of<WorkoutLogProvider>(context, listen: false);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildHeader(uiStrings, widget._date),
        _buildWorkoutLogList(uiStrings, provider, widget._date),
      ],
    );
  }
}
