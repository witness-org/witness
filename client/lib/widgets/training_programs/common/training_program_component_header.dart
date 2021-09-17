import 'package:flutter/material.dart';

class TrainingProgramComponentHeader extends StatelessWidget {
  final List<Widget> _widgets;

  const TrainingProgramComponentHeader(this._widgets, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ..._widgets,
        Divider(),
      ],
    );
  }
}
