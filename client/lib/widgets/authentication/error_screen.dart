import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';

class ErrorScreen extends StatelessWidget with StringLocalizer {
  const ErrorScreen({required final this.errorText, final Key? key}) : super(key: key);

  final String? errorText;

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    final theme = Theme.of(context);
    final errorMessage = errorText != null ? '${uiStrings.errorScreen_prefix}: $errorText' : 'An unexpected error occurred.';
    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Icon(Icons.error, color: theme.errorColor, size: 64),
              const SizedBox(height: 15),
              Text(errorMessage, style: theme.textTheme.subtitle1),
            ],
          ),
        ),
      ),
    );
  }
}
