import 'package:client/widgets/authentication/login_card.dart';
import 'package:flutter/material.dart';

class LoginScreen extends StatelessWidget {
  const LoginScreen({final Key? key}) : super(key: key);

  static const routeName = '/login';

  @override
  Widget build(final BuildContext context) {
    final deviceSize = MediaQuery.of(context).size;
    return Scaffold(
      body: SingleChildScrollView(
        child: SizedBox(
          height: deviceSize.height,
          width: deviceSize.width,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: const [
              LoginCard(key: Key('login_card')),
            ],
          ),
        ),
      ),
    );
  }
}
