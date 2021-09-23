import 'package:flutter/material.dart';

/// Provides extensions on the [AsyncSnapshot] type for commonly used patterns, reducing boilerplate code and
/// duplication in widget trees.
extension AsyncSnapshotExtensions<T> on AsyncSnapshot<T> {
  /// Returns a widget that depends on [AsyncSnapshot.connectionState]. If the [AsyncSnapshot.connectionState] property of
  /// the current instance is [ConnectionState.waiting], the widget represented by the [waiting] parameter is returned. If
  /// [waiting] is null,
  ///
  /// ```
  /// Center(
  ///       child: CircularProgressIndicator(),
  /// )
  /// ```
  ///
  /// is used instead. If the [AsyncSnapshot.connectionState] property of the current instance is NOT [ConnectionState.waiting], the
  /// widget represented by the [defaultWidget] parameter is returned.
  Widget waitSwitch(final Widget defaultWidget, {Widget? waiting}) {
    waiting ??= const Center(
      child: CircularProgressIndicator(),
    );

    return connectionState == ConnectionState.waiting ? waiting : defaultWidget;
  }
}
