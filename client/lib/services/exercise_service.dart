import 'dart:convert';

import 'package:client/extensions/enum_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/exercise_create.dart';
import 'package:client/models/exercises/exercise_history.dart';
import 'package:client/models/exercises/exercise_statistics.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/services/base_service.dart';
import 'package:client/services/server_response.dart';
import 'package:http/http.dart' as http;

final _logger = getLogger('exercise_service');

class ExerciseService extends BaseService {
  const ExerciseService() : super('exercises');

  Future<ServerResponse<List<Exercise>, String?>> getExercisesByMuscleGroup(final MuscleGroup group, final String? token) async {
    final requestUri = getUri('', queryParameters: {'muscle-group': group.toDtoString()});
    _logger
      ..i('Delegating fetching of exercises to server')
      ..i('GET $requestUri');

    final response = await http.get(requestUri, headers: getHttpHeaders(authorization: token));

    if (response.statusCode == 200) {
      final responseList = decodeResponse<List<dynamic>>(response);
      return ServerResponse.success(responseList.map((final dynamic e) => Exercise.fromJson(e as Map<String, dynamic>)).toList());
    } else {
      final responseMap = decodeResponse<Map<String, dynamic>>(response);
      _logger.e('Could not fetch exercises: ${responseMap['message']}');
      return ServerResponse.failure(getFailureStringFromResponseMap(responseMap));
    }
  }

  Future<ServerResponse<Exercise, String?>> postUserExercise(final ExerciseCreate exercise, final String? token) async {
    final requestUri = getUri('user-exercises');
    _logger
      ..i('Delegating creation of new user exercise to server')
      ..i('POST $requestUri');

    final payload = json.encode(exercise);
    final response = await http.post(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: payload);

    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 201) {
      return ServerResponse.success(Exercise.fromJson(responseMap));
    } else {
      _logger.e('Could not create user exercise: ${responseMap['message']}');
      return ServerResponse.failure(getFailureStringFromResponseMap(responseMap));
    }
  }

  Future<ServerResponse<Exercise, String?>> putUserExercise(final Exercise exercise, final String? token) async {
    final requestUri = getUri('user-exercises');
    _logger
      ..i('Delegating update of user exercise to server')
      ..i('PUT $requestUri');

    final payload = json.encode(exercise);
    final response = await http.put(requestUri, headers: getHttpHeaders(authorization: token, jsonContent: true), body: payload);
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      return ServerResponse.success(Exercise.fromJson(responseMap));
    } else {
      _logger.e('Could not update user exercise: ${responseMap['message']}');
      return ServerResponse.failure(getFailureStringFromResponseMap(responseMap));
    }
  }

  Future<ServerResponse<void, String?>> deleteUserExercise(final int id, final String? token) async {
    final requestUri = getUri('user-exercises/$id');
    _logger
      ..i('Delegating deletion of user exercise to server')
      ..i('DELETE $requestUri');

    final response = await http.delete(requestUri, headers: getHttpHeaders(authorization: token));

    if (response.statusCode == 204) {
      return const ServerResponse.success(null);
    } else {
      final responseMap = decodeResponse<Map<String, dynamic>>(response);
      _logger.e('Could not delete user exercise: ${responseMap['message']}');
      return ServerResponse.failure(getFailureStringFromResponseMap(responseMap));
    }
  }

  Future<ServerResponse<ExerciseHistory, String?>> getExerciseHistory(final int exerciseId, final String? token) async {
    final requestUri = getUri('history/$exerciseId');
    _logger
      ..i('Delegating retrieval of exercise history to server')
      ..i('GET $requestUri');

    final response = await http.get(requestUri, headers: getHttpHeaders(authorization: token));
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      final exerciseHistory = ExerciseHistory.fromJson(responseMap);
      return ServerResponse.success(exerciseHistory);
    } else {
      _logger.e('Could not fetch exercise history: ${responseMap['message']}');
      return ServerResponse.failure(getFailureStringFromResponseMap(responseMap));
    }
  }

  Future<ServerResponse<ExerciseStatistics, String?>> getExerciseStatistics(final int exerciseId, final String? token) async {
    final requestUri = getUri('statistics/$exerciseId');
    _logger
      ..i('Delegating retrieval of exercise statistics to server')
      ..i('GET $requestUri');

    final response = await http.get(requestUri, headers: getHttpHeaders(authorization: token));
    final responseMap = decodeResponse<Map<String, dynamic>>(response);

    if (response.statusCode == 200) {
      final exerciseStatistics = ExerciseStatistics.fromJson(responseMap);

      return ServerResponse.success(exerciseStatistics);
    } else {
      _logger.e('Could not fetch exercise statistics: ${responseMap['message']}');
      return ServerResponse.failure(getFailureStringFromResponseMap(responseMap));
    }
  }
}
