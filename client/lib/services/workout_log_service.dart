import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/services/base_service.dart';
import 'package:client/services/server_response.dart';
import 'package:http/http.dart' as http;
import 'package:timezone/timezone.dart';

final _logger = getLogger('workout_log_service');

class WorkoutLogService extends BaseService {
  Future<ServerResponse<List<WorkoutLog>, String>> getWorkoutLogsByDate(final TZDateTime date, final String? token) async {
    final requestUri = getUri('workout', queryParameters: {'date': date.toIso8601String()});
    _logger
      ..i('Delegating fetching of workout logs to server')
      ..i('GET $requestUri');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

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
}
