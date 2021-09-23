/// Class that ought to be used as a mixin to prepare messages that are sent to and printed via a Logger instance. This preparation process
/// may, for instance, entail adding prefixes to the message to be logged.
abstract class LogMessagePreparer {
  /// Prefixes [message] with the runtime type of the logging class. Returns a [Function] that takes no argument end returns the prefixed message
  /// [String]. The [Function] is used as return type because calling [Type.toString] on [runtimeType] is a non-trivial operation that can negatively
  /// impact the performance. Therefore, the message string (containing the result of [runtimeType.toString()]) is not evaluated when issuing the
  /// log command, but lazily: The function is only invoked if the LogFilter in use determines the message should be printed. As a consequence,
  /// the expensive [Type.toString] calls on [runtimeType] are _not_ executed in release mode, but only during development (debug mode).
  /// Example:
  /// ```
  /// final _logger = getLogger('myFileName')
  /// class MyClass with LogMessagePreparer {
  ///   void foo() {
  ///     _logger.v(prepare('foo()'));
  ///   }
  /// }
  /// ```
  ///
  /// The snippet above prints the message "MyClass.foo()", where the expensive operation to determine the runtime type representation "MyClass"
  /// is only executed if the LogFilter decides to print the accompanying LogEvent.
  String Function() prepare(final String message) {
    return () => '$runtimeType.$message';
  }
}
