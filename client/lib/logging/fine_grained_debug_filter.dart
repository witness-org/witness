import 'package:flutter/foundation.dart';
import 'package:logger/logger.dart';

/// Prints all log messages that are both at least of level `minLevel` AND contained in `allowedLevels`. Furthermore, logs are
/// only printed in development mode (debug mode), i.e. during release mode (production mode) executions, no logs are printed.
///
/// This filter can be used to specify a minimum log level as well as exclude certain log levels from being displayed. For example,
/// this can be useful to quickly have a debug session where ONLY verbose log statements are interesting - as to reduce output cluttering.
class FineGrainedDebugFilter extends LogFilter {
  final Set<Level> allowedLevels;
  final Level minLevel;

  FineGrainedDebugFilter({required this.minLevel, required this.allowedLevels});

  @override
  bool shouldLog(LogEvent event) {
    // only log in release mode
    if (kReleaseMode) {
      return false;
    }

    return event.level.index >= minLevel.index && allowedLevels.contains(event.level);
  }
}
