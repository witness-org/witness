import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';

class SplashScreen extends StatelessWidget with StringLocalizer {
  const SplashScreen({final Key? key}) : super(key: key);

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    final theme = Theme.of(context);
    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const CircularProgressIndicator(),
              const SizedBox(height: 15),
              Text(uiStrings.splashScreen_initialization, style: theme.textTheme.titleMedium),
            ],
          ),
        ),
      ),
    );
  }
}
