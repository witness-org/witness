import 'package:client/extensions/enum_extensions.dart';
import 'package:client/models/exercises/exercise_attribute.dart';
import 'package:client/models/training_programs/exercise_set.dart';
import 'package:flutter/material.dart';

class WorkoutExerciseSetView extends StatelessWidget {
  const WorkoutExerciseSetView(this._set, {final Key? key}) : super(key: key);

  final ExerciseSet _set;

  Widget _buildAttributeValues(final BuildContext context, final Map<ExerciseAttribute, Object> attributes) {
    return Column(
      children: attributes.entries
          .map(
            (final attribute) => TextFormField(
              initialValue: attribute.value.toString(),
              decoration: InputDecoration(
                labelText: attribute.key.toUiString(),
              ),
            ),
          )
          .toList(),
    );
  }

  @override
  Widget build(final BuildContext context) {
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(border: Border.all(color: Colors.grey), borderRadius: BorderRadius.circular(10)),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Set ${_set.number}: ',
              style: Theme.of(context).textTheme.subtitle1?.merge(const TextStyle(fontWeight: FontWeight.bold)),
            ),
            _buildAttributeValues(context, _set.attributes),
            const Divider(),
          ],
        ),
      ),
    );
  }
}
