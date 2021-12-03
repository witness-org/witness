import 'package:flutter/material.dart';

class TrainingProgramComponentHeader extends StatelessWidget {
  const TrainingProgramComponentHeader(this._widgets, {final Key? key}) : super(key: key);

  final List<Widget> _widgets;

  @override
  Widget build(final BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ..._widgets,
        const Divider(),
      ],
    );
  }
}
