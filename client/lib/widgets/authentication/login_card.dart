import 'package:client/providers/auth_provider.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class LoginCard extends StatefulWidget {
  const LoginCard({final Key? key = const Key('login_card')}) : super(key: key);

  @override
  _LoginCardState createState() => _LoginCardState(); // ignore: library_private_types_in_public_api
}

class _LoginCardState extends State<LoginCard> with StringLocalizer {
  final GlobalKey<FormState> _formKey = GlobalKey();
  final _LoginData _loginData = _LoginData();
  var _isLoading = false;
  final _passwordController = TextEditingController();

  void _showErrorDialog(final String message) {
    showDialog<void>(
      context: context,
      builder: (final ctx) => AlertDialog(
        title: Text(_loginData.mode == _AuthMode.login ? 'Login failed' : 'Signup failed'),
        content: Text(message),
        actions: [
          TextButton(
            child: const Text('OK'),
            onPressed: () => Navigator.of(ctx).pop(),
          ),
        ],
      ),
    );
  }

  Future<void> _submit() async {
    if (_formKey.currentState == null || !_formKey.currentState!.validate()) {
      return;
    }

    _formKey.currentState!.save();
    setState(() {
      _isLoading = true;
    });

    final auth = Provider.of<AuthProvider>(context, listen: false);
    final authenticationResult = _loginData.mode == _AuthMode.login
        ? await auth.login(_loginData.user, _loginData.password)
        : await auth.signUp(_loginData.user, _loginData.password);

    if (authenticationResult.isError) {
      _showErrorDialog(authenticationResult.error!);
    }

    if (!mounted) {
      return;
    }

    setState(() {
      _isLoading = false;
    });
  }

  void _switchAuthMode() {
    _formKey.currentState?.reset();
    if (_loginData.mode == _AuthMode.login) {
      setState(() {
        _loginData.mode = _AuthMode.signUp;
      });
    } else {
      setState(() {
        _loginData.mode = _AuthMode.login;
      });
    }
  }

  @override
  Widget build(final BuildContext context) {
    final deviceSize = MediaQuery.of(context).size;
    final uiStrings = getLocalizedStrings(context);
    const animationDuration = Duration(milliseconds: 300);
    final signingUp = _loginData.mode == _AuthMode.signUp;
    return Card(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10.0),
      ),
      elevation: 8.0,
      child: AnimatedContainer(
        duration: animationDuration,
        curve: Curves.ease,
        height: signingUp ? 320 : 260,
        constraints: BoxConstraints(minHeight: signingUp ? 320 : 260),
        width: deviceSize.width * 0.75,
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: SingleChildScrollView(
            child: Column(
              children: [
                TextFormField(
                  key: const Key('login_card.email'),
                  decoration: InputDecoration(labelText: uiStrings.loginCard_form_email_label),
                  keyboardType: TextInputType.emailAddress,
                  validator: (final value) {
                    if (value == null || value.isEmpty || !value.contains('@')) {
                      return uiStrings.loginCard_form_error_invalidEmail;
                    }
                  },
                  onSaved: (final value) {
                    _loginData.user = value;
                  },
                ),
                const SizedBox(
                  height: 7.0,
                ),
                TextFormField(
                  key: const Key('login_card.password'),
                  decoration: InputDecoration(labelText: uiStrings.loginCard_form_password_label),
                  obscureText: true,
                  controller: _passwordController,
                  validator: (final value) {
                    if (value == null || value.isEmpty || value.length < 6) {
                      return uiStrings.loginCard_form_error_passwordTooShort;
                    }
                  },
                  onSaved: (final value) {
                    _loginData.password = value;
                  },
                ),
                if (signingUp)
                  AnimatedContainer(
                    constraints: BoxConstraints(
                      minHeight: signingUp ? 60 : 0,
                      maxHeight: signingUp ? 120 : 0,
                    ),
                    curve: Curves.ease,
                    duration: animationDuration,
                    child: TextFormField(
                      key: const Key('login_card.passwordConfirmation'),
                      enabled: signingUp,
                      decoration: InputDecoration(labelText: uiStrings.loginCard_form_passwordConfirmation_label),
                      obscureText: true,
                      validator: signingUp
                          ? (final value) {
                              if (value != _passwordController.text) {
                                return uiStrings.loginCard_form_error_passwordMismatch;
                              }
                            }
                          : null,
                    ),
                  ),
                const SizedBox(height: 20),
                if (_isLoading)
                  const CircularProgressIndicator()
                else
                  ElevatedButton(
                    key: const Key('login_card.submit'),
                    child: Text(signingUp ? uiStrings.loginCard_action_signUp : uiStrings.loginCard_action_login),
                    onPressed: _submit,
                  ),
                TextButton(
                  key: const Key('login_card.switchAuthMode'),
                  child: Text(signingUp ? uiStrings.loginCard_action_loginInstead : uiStrings.loginCard_action_signUpInstead),
                  onPressed: _switchAuthMode,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _LoginData {
  _AuthMode mode = _AuthMode.login;
  String? _user;
  String? _password;

  String get user {
    return _user ?? '';
  }

  set user(final String? user) {
    _user = user;
  }

  String get password {
    return _password ?? '';
  }

  set password(final String? password) {
    _password = password;
  }
}

enum _AuthMode { signUp, login }
