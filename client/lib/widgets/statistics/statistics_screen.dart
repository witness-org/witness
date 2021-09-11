import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';

class StatisticsScreen extends StatelessWidget {
  static const routeName = '/statistics';

  const StatisticsScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: MainAppBar(),
      drawer: AppDrawer(),
      body: Center(
        child: Text('Statistics'),
      ),
    );
  }
}
