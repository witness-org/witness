import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/services/workout_log_service.dart';
import 'package:collection/collection.dart' as collection;
import 'package:flutter/material.dart';
import 'package:injector/injector.dart';
import 'package:timezone/timezone.dart';

final _logger = getLogger('workout_log_provider');

class WorkoutLogProvider with ChangeNotifier {
  WorkoutLogProvider._(this._auth, this._workoutLogs);

  WorkoutLogProvider.empty()
      : this._(
          null,
          <TZDateTime, List<WorkoutLog>>{},
        );

  WorkoutLogProvider.fromProviders(final AuthProvider auth, final WorkoutLogProvider? instance)
      : this._(
          auth,
          instance?._workoutLogs ?? <TZDateTime, List<WorkoutLog>>{},
        );

  static final Injector _injector = Injector.appInstance;
  late final WorkoutLogService _workoutLogService = _injector.get<WorkoutLogService>();

  final Map<TZDateTime, List<WorkoutLog>> _workoutLogs;
  final AuthProvider? _auth;

  List<WorkoutLog> getWorkoutLogsByDate(final TZDateTime date) {
    return collection.UnmodifiableListView(_workoutLogs[date] ?? <WorkoutLog>[]);
  }

  Future<void> fetchWorkoutLogsByDate(final TZDateTime date) async {
    _logger.i('Fetching workouts that were logged on "${date.toIso8601String()}"');

    final response = await _workoutLogService.getWorkoutLogsByDate(date, await _auth?.getToken());
    final resultList = response.success;

    if (response.isSuccessAndResponse) {
      _workoutLogs[date] = resultList!;
      notifyListeners();
    }

    if (resultList != null) {
      _logger.i('Received ${resultList.length} exercises');
    } else {
      _logger.w('Fetching exercises failed');
    }
  }
}
