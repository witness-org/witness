import 'package:flutter/material.dart';

abstract class DialogHelper {
  static Future<bool?> getBool(
    BuildContext context, {
    String? title,
    required String content,
    required String falseOption,
    required String trueOption,
    TextStyle? falseOptionStyle,
    TextStyle? trueOptionStyle,
  }) {
    return showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
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
