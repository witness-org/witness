import 'dart:convert';

import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/exercise_log_create.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/set_log_create.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/models/workouts/workout_log_create.dart';
import 'package:client/services/base_service.dart';
import 'package:client/services/server_response.dart';
import 'package:http/http.dart' as http;
import 'package:timezone/timezone.dart';

final _logger = getLogger('workout_log_service');

class WorkoutLogService extends BaseService {
  @override
  String get targetResource => 'workout-logs';

  Future<ServerResponse<List<WorkoutLog>, String>> getWorkoutLogsByDate(final TZDateTime date, final String? token) async {
    final requestUri = getUri('', queryParameters: {'date': date.toIso8601String()});
    _logger
      ..i('Delegating fetching of workout logs to server')
      ..i('GET $requestUri');

    final response = await http.get(requestUri, headers: getHttpHeaders(authorization: token));

    if (response.statusCode == 200) {
      final responseList = decodeResponse<List<dynamic>>(response);
      return ServerResponse.success(responseList.map((final dynamic e) => WorkoutLog.fromJson(e as Map<String, dynamic>)).toList());
    } else {
      final responseMap = decodeResponse<Map<String, dynamic>>(response);
      _logger.e('Could not fetch workout logs: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> patchWorkoutLogDuration(
      final WorkoutLog workoutLog, final int? durationMinutes, final String? token) async {
    final requestUri = getUri('${workoutLog.id}');
    _logger
      ..i('Delegating setting duration of workout to server')
      ..i('PATCH $requestUri');

    final payload = json.encode(durationMinutes);
    final response = await http.patch(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: payload);
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not update workout log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> putExerciseLogPositions(
      final WorkoutLog workoutLog, final Map<String, int> positions, final String? token) async {
    final requestUri = getUri('${workoutLog.id}/exercise-logs-positions');
    _logger
      ..i('Delegating setting exercise log positions in workout to server')
      ..i('PUT $requestUri');

    final payload = json.encode(positions);
    final response = await http.put(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: payload);
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not set exercise log positions in workout log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> postNewWorkoutLog(final WorkoutLogCreate workoutLog, final String? token) async {
    final requestUri = getUri('');
    _logger
      ..i('Delegating creating new workout log to server')
      ..i('POST $requestUri');

    final payload = json.encode(workoutLog);
    final response = await http.post(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: payload);
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 201) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not create workout log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<void, String>> deleteWorkoutLog(final WorkoutLog workoutLog, final String? token) async {
    final requestUri = getUri('${workoutLog.id}');
    _logger
      ..i('Delegating deleting workout log to server')
      ..i('DELETE $requestUri');

    final response = await http.delete(requestUri, headers: getHttpHeaders(authorization: token));

    if (response.statusCode == 204) {
      return const ServerResponse.success(null);
    } else {
      final responseMap = decodeResponse<Map<String, dynamic>>(response);
      _logger.e('Could not create workout log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> postNewExerciseLog(
      final WorkoutLog workoutLog, final ExerciseLogCreate logCreate, final String? token) async {
    final requestUri = getUri('${workoutLog.id}/exercise-logs');
    _logger
      ..i('Delegating creating new exercise log to server')
      ..i('POST $requestUri');

    final payload = json.encode(logCreate);
    final response = await http.post(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: payload);
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 201) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not create exercise log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> deleteExerciseLog(
      final WorkoutLog workoutLog, final ExerciseLog exerciseLog, final String? token) async {
    final requestUri = getUri('${workoutLog.id}/exercise-logs/${exerciseLog.id}');
    _logger
      ..i('Delegating deleting exercise log to server')
      ..i('POST $requestUri');

    final response = await http.delete(requestUri, headers: getHttpHeaders(authorization: token));
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not delete exercise log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> postNewSetLog(
      final WorkoutLog workoutLog, final ExerciseLog exerciseLog, final SetLogCreate logCreate, final String? token) async {
    final requestUri = getUri('${workoutLog.id}/exercise-logs/${exerciseLog.id}/set-logs');
    _logger
      ..i('Delegating creating new set log to server')
      ..i('POST $requestUri');

    final payload = json.encode(logCreate);
    final response = await http.post(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: payload);
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 201) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not create set log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> patchExerciseLogComment(
      final WorkoutLog workoutLog, final ExerciseLog exerciseLog, final String? comment, final String? token) async {
    final requestUri = getUri('${workoutLog.id}/exercise-logs/${exerciseLog.id}');
    _logger
      ..i('Delegating setting comment of exercise log to server')
      ..i('PATCH $requestUri');

    final response = await http.patch(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: comment);
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not update exercise log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> putSetLog(final WorkoutLog workoutLog, final SetLog setLog, final String? token) async {
    final requestUri = getUri('${workoutLog.id}/exercise-logs/${setLog.exerciseLogId}/set-logs');
    _logger
      ..i('Delegating updating set log to server')
      ..i('PUT $requestUri');

    final payload = json.encode(setLog);
    final response = await http.put(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: payload);
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not update set log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> deleteSetLog(final WorkoutLog workoutLog, final SetLog setLog, final String? token) async {
    final requestUri = getUri('${workoutLog.id}/exercise-logs/${setLog.exerciseLogId}/set-logs/${setLog.id}');
    _logger
      ..i('Delegating deleting set log to server')
      ..i('DELETE $requestUri');

    final response = await http.delete(requestUri, headers: getHttpHeaders(authorization: token));
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not delete set log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }

  Future<ServerResponse<WorkoutLog, String>> putSetLogPositions(
      final WorkoutLog workoutLog, final ExerciseLog exerciseLog, final Map<String, int> positions, final String? token) async {
    final requestUri = getUri('${workoutLog.id}/exercise-logs/${exerciseLog.id}/set-logs-positions');
    _logger
      ..i('Delegating setting set log positions in exercise log in workout to server')
      ..i('PUT $requestUri');

    final payload = json.encode(positions);
    final response = await http.put(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: payload);
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      return ServerResponse.success(WorkoutLog.fromJson(responseMap));
    } else {
      _logger.e('Could not set set log positions in exercise log in workout log: ${responseMap['message']}');
      return ServerResponse.failure(responseMap['message'].toString());
    }
  }
}
