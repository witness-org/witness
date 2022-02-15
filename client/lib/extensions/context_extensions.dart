import 'dart:ui';
import 'package:flutter/widgets.dart';

/// Provides methods that facilitate interactions with a [BuildContext].
extension BuildContextExtensions on BuildContext {
  /// Checks whether dark mode is enabled, i.e. the dark theme is used for displaying the UI.
  bool isDarkModeEnabled() {
    final brightness = MediaQuery.of(this).platformBrightness;
    return brightness == Brightness.dark;
  }
}
