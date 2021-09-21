import 'package:flutter/material.dart';

abstract class DialogHelper {
  DialogHelper._();

  static Future<bool?> getBool(
    final BuildContext context, {
    final String? title,
    required final String content,
    required final String falseOption,
    required final String trueOption,
    final TextStyle? falseOptionStyle,
    final TextStyle? trueOptionStyle,
  }) {
    return showDialog<bool>(
      context: context,
      builder: (final ctx) => AlertDialog(
        title: title != null ? Text(title) : null,
        content: Text(content),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: Text(
              falseOption,
              style: falseOptionStyle,
            ),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: Text(
              trueOption,
              style: trueOptionStyle,
            ),
          ),
        ],
      ),
    );
  }
}
