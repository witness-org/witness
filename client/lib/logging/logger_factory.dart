import 'package:client/logging/colored_prefix_printer.dart';
import 'package:client/logging/fine_grained_debug_filter.dart';
import 'package:logger/logger.dart';

/// Instantiates a [Logger] with [loggerName] as name. It is recommended to choose the class or file name in which the
/// logger is created as [loggerName]. The returned [Logger] instance prefixes messages with the log level and the its
/// own name. The messages are in a color appropriate for the log level. For example, the snippet
///
/// ```dart
/// final _logger = getLogger('MyService');
/// _logger.e('No network connection.');
/// ```
///
/// prints the message '[[]ERROR][] MyService: No network connection.' in a color that indicates an error.
///
Logger getLogger(final String loggerName) {
  return Logger(
    printer: ColoredPrefixPrinter(
      PrettyPrinter(
        noBoxingByDefault: true,
        methodCount: 0,
        printEmojis: false,
        lineLength: 150,
      ),
      loggerName,
    ),
    filter: FineGrainedDebugFilter(
      minLevel: Level.verbose,
      allowedLevels: {
        Level.verbose,
        Level.debug,
        Level.info,
        Level.warning,
        Level.error,
        Level.wtf,
      },
    ),
  );
}
