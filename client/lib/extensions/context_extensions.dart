import 'dart:ui';
import 'package:flutter/widgets.dart';

extension DarkMode on BuildContext {
  /// Checks whether dark mode is enabled, i.e. the dark theme is used for displaying the UI.
  bool isDarkModeEnabled() {
    final brightness = MediaQuery.of(this).platformBrightness;
    return brightness == Brightness.dark;
  }
}
