import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/extensions/list_extensions.dart';
import 'package:client/extensions/map_extensions.dart';
import 'package:client/extensions/model_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/exercise_log_create.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/set_log_create.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/models/workouts/workout_log_create.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/providers/base_provider.dart';
import 'package:client/services/server_response.dart';
import 'package:client/services/workout_log_service.dart';
import 'package:collection/collection.dart' as collection;
import 'package:injector/injector.dart';
import 'package:timezone/timezone.dart';

final _logger = getLogger('workout_log_provider');

class WorkoutLogProvider extends BaseProvider {
  WorkoutLogProvider._(this._auth, this._workoutLogs, this._workoutLogsCount) : super(_logger);

  WorkoutLogProvider.empty() : this._(null, <TZDateTime, List<WorkoutLog>>{}, <TZDateTime, int>{});

  WorkoutLogProvider.fromProviders(final AuthProvider auth, final WorkoutLogProvider? instance)
      : this._(auth, (instance?._workoutLogs).orEmpty(), (instance?._workoutLogsCount).orEmpty());

  static final Injector _injector = Injector.appInstance;
  late final WorkoutLogService _workoutLogService = _injector.get<WorkoutLogService>();

  final Map<TZDateTime, List<WorkoutLog>> _workoutLogs;
  final Map<TZDateTime, int> _workoutLogsCount;
  final AuthProvider? _auth;

  List<WorkoutLog> getWorkoutLogsByDay(final TZDateTime date) {
    return collection.UnmodifiableListView(_workoutLogs[date.onlyDate()].orEmpty());
  }

  bool hasWorkoutLogsWithExerciseLogs(final TZDateTime date) {
    return _workoutLogsCount[date.onlyDate()] != null && _workoutLogsCount[date.onlyDate()]! > 0;
  }

  Future<void> fetchWorkoutLogsByDay(final TZDateTime date) async {
    _logger.i('Fetching workouts that were logged on "${date.toIso8601String()}"');

    final response = await _workoutLogService.getWorkoutLogsByDay(date, await _auth?.getToken());
    final resultList = response.success;

    if (response.isSuccessAndResponse) {
      for (final workoutLog in resultList!) {
        workoutLog.sortLogs();
      }

      _workoutLogs[date.onlyDate()] = resultList;
      notifyListeners();
    }

    if (resultList != null) {
      _logger.i('Received ${resultList.length} workout logs');
    } else {
      _logger.e('Fetching workout logs failed');
      return Future.error(response.error ?? '');
    }
  }

  Future<void> getLoggingDaysInPeriod(final TZDateTime startDate, final TZDateTime endDate) async {
    _logger.i('Fetching logging days between "${startDate.toIso8601String()}" and "${endDate.toIso8601String()}"');

    final response = await _workoutLogService.getLoggingDaysInPeriod(startDate, endDate, await _auth?.getToken());
    final resultMap = response.success;

    if (response.isSuccessAndResponse) {
      final datesNotInResponse = _workoutLogsCount.whereKeys(
        (final date) => date.key.compareTo(startDate) >= 0 && date.key.compareTo(endDate) <= 0 && !resultMap!.keys.contains(date.key),
      );

      for (final date in datesNotInResponse) {
        _workoutLogsCount.remove(date);
      }

      for (final entry in resultMap!.entries) {
        _workoutLogsCount[entry.key.onlyDate()] = entry.value;
      }

      notifyListeners();
    }

    if (resultMap != null) {
      _logger.i('Received ${resultMap.keys.length} logging days');
    } else {
      _logger.e('Fetching logging days failed');
      return Future.error(response.error ?? '');
    }
  }

  Future<ServerResponse<WorkoutLog, String?>> patchWorkoutLogDuration(final WorkoutLog workoutLog, final int? durationMinutes) async {
    _logger.i('Setting workout duration for workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.patchWorkoutLogDuration(workoutLog, durationMinutes, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Setting duration of workout log succeeded');
      _updateWorkoutLogOnDay(workoutLog.loggedOn.onlyDate(), workoutLog.id, response.success!);
    } else {
      _logger.e('Setting duration of workout log failed: ${response.error}');
    }

    return response;
  }

  Future<ServerResponse<WorkoutLog, String?>> putExerciseLogPositions(final WorkoutLog workoutLog, final Map<String, int> positions) async {
    _logger.i('Updating exercise log order for workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.putExerciseLogPositions(workoutLog, positions, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Update of exercise log order for workout log succeeded');
      _updateWorkoutLogOnDay(workoutLog.loggedOn.onlyDate(), workoutLog.id, response.success!);
    } else {
      _logger.e('Update of exercise log order for workout log failed: ${response.error}');
      // set positions back to previous state (given by parameter) and notifying listeners to re-render UI state before position update attempt
      workoutLog.sortLogs();
      notifyListeners();
    }

    return response;
  }

  Future<ServerResponse<WorkoutLog, String?>> postNewWorkoutLog(final WorkoutLogCreate workoutLog) async {
    _logger.i('Creating new workout log');

    final loggingDay = workoutLog.loggedOn.onlyDate();
    final response = await _workoutLogService.postNewWorkoutLog(workoutLog, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Creation of workout log succeeded');
      final newWorkoutLog = response.success!;

      if (_workoutLogs.containsKey(loggingDay)) {
        // there are already workout logs for the given `loggingDay` -> append workout log to list
        _workoutLogs[loggingDay]!.add(newWorkoutLog);
      } else {
        // there are no workout logs for the given `loggingDay` yet -> assign list with workout log
        _workoutLogs[loggingDay] = [newWorkoutLog];
      }

      notifyListeners();
    } else {
      _logger.e('Creation of workout log failed: ${response.error}');
    }

    return response;
  }

  Future<ServerResponse<void, String?>> deleteWorkoutLog(final WorkoutLog workoutLog) async {
    _logger.i('Deleting workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.deleteWorkoutLog(workoutLog, await _auth?.getToken());

    if (response.isSuccessNoResponse) {
      _logger.i('Deletion of workout log succeeded');
      _workoutLogs[workoutLog.loggedOn.onlyDate()]?.removeWhere((final log) => log.id == workoutLog.id);
      notifyListeners();
    } else {
      _logger.e('Deletion of workout log failed: ${response.error}');
    }

    return response;
  }

  Future<ServerResponse<WorkoutLog, String?>> postNewExerciseLogs(final WorkoutLog workoutLog, final List<ExerciseLogCreate> logCreates) async {
    _logger.i('Creating new exercise logs for workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.postNewExerciseLogs(workoutLog, logCreates, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Creating new exercise logs for workout log succeeded');
      _updateWorkoutLogOnDay(workoutLog.loggedOn.onlyDate(), workoutLog.id, response.success!);
    } else {
      _logger.e('Creating new exercise logs for workout log failed: ${response.error}');
    }

    return response;
  }

  Future<ServerResponse<WorkoutLog, String?>> deleteExerciseLog(final WorkoutLog workoutLog, final ExerciseLog exerciseLog) async {
    _logger.i('Deleting exercise log with ID ${exerciseLog.id} from workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.deleteExerciseLog(workoutLog, exerciseLog, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Deletion of exercise log succeeded');
      _updateWorkoutLogOnDay(workoutLog.loggedOn.onlyDate(), workoutLog.id, response.success!);
    } else {
      _logger.e('Deletion of exercise log failed: ${response.error}');
    }

    return response;
  }

  Future<ServerResponse<WorkoutLog, String?>> postNewSetLog(
    final WorkoutLog workoutLog,
    final ExerciseLog exerciseLog,
    final SetLogCreate logCreate,
  ) async {
    _logger.i('Creating new set log for exercise log with ID ${exerciseLog.id} from workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.postNewSetLog(workoutLog, exerciseLog, logCreate, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Creation of set log succeeded');
      _updateWorkoutLogOnDay(workoutLog.loggedOn.onlyDate(), workoutLog.id, response.success!);
    } else {
      _logger.e('Creation of set log failed: ${response.error}');
    }

    return response;
  }

  Future<ServerResponse<WorkoutLog, String?>> patchExerciseLogComment(
    final WorkoutLog workoutLog,
    final ExerciseLog exerciseLog,
    final String? comment,
  ) async {
    _logger.i('Setting comment for exercise log with ID ${exerciseLog.id} from workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.patchExerciseLogComment(workoutLog, exerciseLog, comment, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Setting comment for exercise log succeeded');
      _updateWorkoutLogOnDay(workoutLog.loggedOn.onlyDate(), workoutLog.id, response.success!);
    } else {
      _logger.e('Setting comment for exercise log failed: ${response.error}');
    }

    return response;
  }

  Future<ServerResponse<WorkoutLog, String?>> putSetLog(final WorkoutLog workoutLog, final SetLog setLog) async {
    _logger.i('Updating set log with ID ${setLog.id} from exercise log with ID ${setLog.exerciseLogId} from workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.putSetLog(workoutLog, setLog, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Update of set log succeeded');
      _updateWorkoutLogOnDay(workoutLog.loggedOn.onlyDate(), workoutLog.id, response.success!);
    } else {
      _logger.e('Update of set log failed: ${response.error}');
    }

    return response;
  }

  Future<ServerResponse<WorkoutLog, String?>> deleteSetLog(final WorkoutLog workoutLog, final SetLog setLog) async {
    _logger.i('Deleting set log with ID ${setLog.id} from exercise log with ID ${setLog.exerciseLogId} from workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.deleteSetLog(workoutLog, setLog, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Deletion of set log succeeded');
      _updateWorkoutLogOnDay(workoutLog.loggedOn.onlyDate(), workoutLog.id, response.success!);
    } else {
      _logger.e('Deletion of set log failed: ${response.error}');
    }

    return response;
  }

  Future<ServerResponse<WorkoutLog, String?>> putSetLogPositions(
    final WorkoutLog workoutLog,
    final ExerciseLog exerciseLog,
    final Map<String, int> positions,
  ) async {
    _logger.i('Updating set log order for exercise log with ID ${exerciseLog.id} from workout log with ID ${workoutLog.id}');
    final response = await _workoutLogService.putSetLogPositions(workoutLog, exerciseLog, positions, await _auth?.getToken());

    if (response.isSuccessAndResponse) {
      _logger.i('Update of set log order succeeded');
      _updateWorkoutLogOnDay(workoutLog.loggedOn.onlyDate(), workoutLog.id, response.success!);
    } else {
      _logger.e('Update of set log order failed: ${response.error}');
      // set positions back to previous state (given by parameter) and notifying listeners to re-render UI state before position update attempt
      workoutLog.sortLogs();
      notifyListeners();
    }

    return response;
  }

  void _updateWorkoutLogOnDay(final TZDateTime loggingDay, final int workoutLogId, final WorkoutLog updatedWorkoutLog) {
    final workoutLogIndex = _workoutLogs[loggingDay]?.indexWhere((final log) => log.id == workoutLogId);
    updatedWorkoutLog.sortLogs();

    if (workoutLogIndex == null) {
      // no entries at all in workout log list for given `loggingDay` -> assign list with workout log
      _workoutLogs[loggingDay] = [updatedWorkoutLog];
    } else if (workoutLogIndex == -1) {
      // no entry with matching ID in workout log list for given `loggingDay` -> append workout log to list
      _workoutLogs[loggingDay]!.add(updatedWorkoutLog);
    } else {
      // "old" entry was found in workout log list for given `loggingDay` -> replace it with updated workout log
      _workoutLogs[loggingDay]![workoutLogIndex] = updatedWorkoutLog;
    }

    notifyListeners();
  }
}
