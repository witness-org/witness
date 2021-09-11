import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';

class ProgramsScreen extends StatelessWidget {
  static const routeName = '/programs';

  const ProgramsScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: MainAppBar(),
      drawer: AppDrawer(),
      body: Center(
        child: Text('Programs'),
      ),
    );
  }
}
