import 'package:client/models/training_programs/workout.dart';
import 'package:client/widgets/training_programs/days/workout_expander_item_body.dart';
import 'package:client/widgets/training_programs/days/workout_expander_item_header.dart';
import 'package:flutter/material.dart';

class WorkoutExpanderView extends StatefulWidget {
  final List<Workout> _workouts;

  const WorkoutExpanderView(this._workouts, {Key? key}) : super(key: key);

  @override
  _WorkoutExpanderViewState createState() => _WorkoutExpanderViewState();
}

class _WorkoutExpanderViewState extends State<WorkoutExpanderView> {
  // The initializer of late variable that are initialized at declaration runs the first time the variable is used (lazy initialization).
  // This way, we do not need to override initState().
  late List<_ExpanderPanelItem> _expanderItems = widget._workouts.map((e) => _ExpanderPanelItem(e)).toList();

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 10),
          child: ExpansionPanelList(
            expandedHeaderPadding: EdgeInsets.all(0),
            expansionCallback: (index, isExpanded) {
              setState(() => _expanderItems[index].isExpanded = !isExpanded);
            },
            children: _expanderItems.map((item) {
              return ExpansionPanel(
                canTapOnHeader: true,
                headerBuilder: (_, __) => WorkoutExpanderItemHeader(item.workout),
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
