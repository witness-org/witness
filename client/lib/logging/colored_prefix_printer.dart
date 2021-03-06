import 'package:logger/logger.dart';

/// A decorator for a [LogPrinter] that allows for the prepending of every line in the log output with a string
/// for the level of that log, in the correct color (if the terminal supports it). Heavily inspired by [PrefixPrinter]. However, in
/// addition to [PrefixPrinter], this class is also capable of printing a customizable prefix in the form of a logger name, which
/// preferably corresponds to the name of the instantiating class or its file name. For example,
///
/// ```dart
/// PrefixPrinter(PrettyPrinter(), 'testClass');
/// ```
///
/// would prepend "[[]DEBUG][] testClass:" to every line in a debug log. You can supply parameters for a custom message for a specific log level.
class ColoredPrefixPrinter extends LogPrinter {
  ColoredPrefixPrinter(
    this._realPrinter,
    this._loggerName, {
    final String debug = '[DEBUG]',
    final String verbose = '[VERBOSE]',
    final String wtf = '[WTF]',
    final String info = '[INFO]',
    final String warning = '[WARNING]',
    final String error = '[ERROR]',
  }) {
    _prefixMap = {
      Level.debug: debug,
      Level.verbose: verbose,
      Level.wtf: wtf,
      Level.info: info,
      Level.warning: warning,
      Level.error: error,
    };

    final len = _longestPrefixLength();
    _prefixMap.forEach((final k, final v) => _prefixMap[k] = '${v.padLeft(len)} ');
  }

  final LogPrinter _realPrinter;
  final String _loggerName;
  late Map<Level, String> _prefixMap;

  static final levelColors = {
    Level.verbose: AnsiColor.fg(AnsiColor.grey(0.5)),
    Level.debug: AnsiColor.none(),
    Level.info: AnsiColor.fg(12),
    Level.warning: AnsiColor.fg(208),
    Level.error: AnsiColor.fg(196),
    Level.wtf: AnsiColor.fg(199),
  };

  @override
  List<String> log(final LogEvent event) {
    final realLogs = _realPrinter.log(event);
    final color = _getLevelColor(event.level);
    final level = _prefixMap[event.level]!;
    return realLogs.map((final logLine) => '${color(level)}${color(_loggerName + ':')} $logLine').toList();
  }

  int _longestPrefixLength() {
    compFunc(final String a, final String b) => a.length > b.length ? a : b;
    return _prefixMap.values.reduce(compFunc).length;
  }

  AnsiColor _getLevelColor(final Level level) {
    return levelColors[level] ?? AnsiColor.none();
  }
}
