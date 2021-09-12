import 'package:client/models/exercise.dart';
import 'package:client/models/exercise_attribute.dart';
import 'package:client/services/mock_data/exercise_tags.dart' as tags;

final data = [
  Exercise(
    id: 1,
    title: 'Overhead Press',
    description: 'A very nice exercise',
    tags: [tags.byId(1)],
    attributes: [
      ExerciseAttribute.weight,
      ExerciseAttribute.reps,
    ],
  ),
  Exercise(
    id: 2,
    title: 'Plank',
    description:
        'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet.',
    tags: [
      tags.byId(1),
      tags.byId(2),
      tags.byId(3),
      tags.byId(5),
      tags.byId(6),
      tags.byId(7),
      tags.byId(8),
      tags.byId(1),
      tags.byId(2),
      tags.byId(3),
      tags.byId(5),
      tags.byId(6),
      tags.byId(7),
      tags.byId(8),
    ],
    attributes: [
      ExerciseAttribute.band,
      ExerciseAttribute.distance,
      ExerciseAttribute.reps,
      ExerciseAttribute.time,
      ExerciseAttribute.weight,
      ExerciseAttribute.band,
      ExerciseAttribute.distance,
      ExerciseAttribute.reps,
      ExerciseAttribute.time,
      ExerciseAttribute.weight,
      ExerciseAttribute.band,
      ExerciseAttribute.distance,
      ExerciseAttribute.reps,
      ExerciseAttribute.time,
      ExerciseAttribute.weight,
      ExerciseAttribute.band,
      ExerciseAttribute.distance,
      ExerciseAttribute.reps,
      ExerciseAttribute.time,
      ExerciseAttribute.weight,
    ],
  ),
  Exercise(
    id: 3,
    title: 'Barbell Curl',
    description: 'Another nice exercise!',
    tags: [tags.byId(3)],
    attributes: [
      ExerciseAttribute.weight,
      ExerciseAttribute.reps,
    ],
  ),
  Exercise(
    id: 4,
    title: 'Jump Squats',
    description: 'Another nice exercise!',
    tags: [tags.byId(4)],
    attributes: [
      ExerciseAttribute.weight,
      ExerciseAttribute.reps,
      ExerciseAttribute.time,
    ],
  ),
  Exercise(
    id: 5,
    title: 'Running',
    description: 'Very refreshing',
    tags: [tags.byId(4)],
    attributes: [
      ExerciseAttribute.distance,
      ExerciseAttribute.time,
    ],
  ),
  Exercise(
    id: 6,
    title: 'Bench Dips',
    description: 'Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice',
    tags: [tags.byId(5)],
    attributes: [
      ExerciseAttribute.weight,
      ExerciseAttribute.reps,
    ],
  ),
  Exercise(
    id: 7,
    title: 'Band Curl',
    description: 'Choose the right one',
    tags: [tags.byId(3)],
    attributes: [
      ExerciseAttribute.reps,
      ExerciseAttribute.weight,
      ExerciseAttribute.band,
    ],
  ),
  Exercise(
    id: 8,
    title: 'Hyperextension',
    description: 'Better than superman',
    tags: [tags.byId(6)],
    attributes: [
      ExerciseAttribute.reps,
      ExerciseAttribute.weight,
    ],
  ),
  Exercise(
    id: 9,
    title: 'Superman',
    description: 'Better than nothing',
    tags: [tags.byId(6)],
    attributes: [
      ExerciseAttribute.reps,
      ExerciseAttribute.weight,
    ],
  ),
  Exercise(
    id: 10,
    title: 'Push Up',
    description: 'That is something one can do',
    tags: [tags.byId(7)],
    attributes: [
      ExerciseAttribute.reps,
      ExerciseAttribute.weight,
    ],
  ),
  Exercise(
    id: 11,
    title: 'Leg Press',
    description: 'Do not bend your knees in the wrong direction!',
    tags: [tags.byId(8)],
    attributes: [
      ExerciseAttribute.reps,
      ExerciseAttribute.weight,
    ],
  ),
  // TODO do we want to allow Exercises to not have tags and/or attributes?
  // e.g. allow during creation and server automatically assigns tag "other"?
  Exercise(
    id: 12,
    title: 'Special Exercise',
    description: 'I will not tell you how it works.',
    tags: [],
    attributes: [],
  )
];

List<Exercise> byTagId(int tagId) {
  return data.where((exercise) => exercise.tags.any((tag) => tag.id == tagId)).toList();
}
