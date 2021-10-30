import 'package:injector/injector.dart';

/// Certain program variables may have to be amended for app executions in different contexts (e.g. integration testing). This class encapsulates
/// arguments that may be altered. When running the app normally, the instance returned by [AppArguments.empty] is used, i.e. no custom/modified
/// configuration.
class AppArguments {
  /// Initializes an [AppArguments] instance with additional or overridden dependencies as specified by [overrideDependencies]. If no such parameter
  /// is specified, a function that does not register any additional or overridden dependencies is used instead.
  const AppArguments({final this.overrideDependencies = noOverrides});

  /// Returns an [AppArguments] instance with no additional or modified program arguments. This is the default for regular app executions (during
  /// development, debug, profiling and in production).
  const AppArguments.empty() : this();

  /// Provides access to the global dependency container. May be used to register additional or override dependencies (e.g. switch actual instances
  /// for mocks).
  ///
  /// **Note:** When overriding object instances, make sure to specify `override: true` in the [Injector.registerSingleton] or
  /// [Injector.registerDependency] invocation, respectively. Otherwise, runtime errors will ensue.
  final void Function(Injector injector) overrideDependencies;

  static void noOverrides(final Injector injector) {}
}
