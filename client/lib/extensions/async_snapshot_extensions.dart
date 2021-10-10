import 'package:flutter/material.dart';

/// Provides extensions on the [AsyncSnapshot] type for commonly used patterns, reducing boilerplate code and
/// duplication in widget trees.
extension AsyncSnapshotExtensions<T> on AsyncSnapshot<T> {
  /// Returns a widget that depends on [AsyncSnapshot.connectionState]. If the [AsyncSnapshot.connectionState] property of
  /// the current instance is [ConnectionState.waiting], the widget represented by the [waitingWidget] parameter is returned. If
  /// [waitingWidget] is `null`,
  ///
  /// ```
  /// Center(
  ///       child: CircularProgressIndicator(),
  /// )
  /// ```
  ///
  /// is used instead. If the [AsyncSnapshot.connectionState] property of the current instance is NOT [ConnectionState.waiting], the
  /// widget represented by the [defaultWidget] parameter is returned. However, if the current instance is in an error state, the widget that is
  /// returned by the function represented by the [errorWidget] parameter is returned. If [errorWidget] is `null`,
  ///
  /// ```
  /// (final Object? error) => Center(
  ///   child: Text('Error: $error'),
  /// )
  /// ```
  ///
  /// is used instead.
  Widget waitSwitch(final Widget defaultWidget, {Widget? waitingWidget, Widget Function(Object? error)? errorWidget}) {
    waitingWidget ??= const Center(
      child: CircularProgressIndicator(),
    );
    errorWidget ??= (final error) => Center(
          child: Text('Error: $error'),
        );

    if (hasError) {
      return errorWidget(error);
    }

    if (connectionState == ConnectionState.done) {
      return defaultWidget;
    }

    return waitingWidget;
  }
}
