import 'package:client/models/training_programs/workout.dart';
import 'package:client/widgets/training_programs/days/workout_expander_item_body.dart';
import 'package:client/widgets/training_programs/days/workout_expander_item_header.dart';
import 'package:flutter/material.dart';

class WorkoutExpanderView extends StatefulWidget {
  const WorkoutExpanderView(this._workouts, {final Key? key}) : super(key: key);

  final List<Workout> _workouts;

  @override
  _WorkoutExpanderViewState createState() => _WorkoutExpanderViewState(); // ignore: library_private_types_in_public_api
}

class _WorkoutExpanderViewState extends State<WorkoutExpanderView> {
  // The initializer of late variable that are initialized at declaration runs the first time the variable is used (lazy initialization).
  // This way, we do not need to override initState().
  late final List<_ExpanderPanelItem> _expanderItems = widget._workouts.map((final e) => _ExpanderPanelItem(e, isExpanded: false)).toList();

  @override
  Widget build(final BuildContext context) {
    return Expanded(
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 10),
          child: ExpansionPanelList(
            expandedHeaderPadding: EdgeInsets.zero,
            expansionCallback: (final index, final isExpanded) {
              setState(() => _expanderItems[index].isExpanded = !isExpanded);
            },
            children: _expanderItems.map((final item) {
              return ExpansionPanel(
                canTapOnHeader: true,
                headerBuilder: (final _, final __) => WorkoutExpanderItemHeader(item.workout),
                body: WorkoutExpanderItemBody(item.workout),
                isExpanded: item.isExpanded,
              );
            }).toList(),
          ),
        ),
      ),
    );
  }
}

class _ExpanderPanelItem {
  _ExpanderPanelItem(this.workout, {this.isExpanded = false});

  Workout workout;
  bool isExpanded;
}
