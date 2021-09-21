import 'package:client/models/training_programs/detail/training_program.dart';
import 'package:client/models/training_programs/detail/training_week.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/models/training_programs/workout.dart';
import 'package:client/services/mock_data/training_programs/training_days.dart' as training_days;
import 'package:client/services/mock_data/training_programs/training_weeks.dart' as training_weeks;

final data = [
  TrainingProgram(
    id: 1,
    name: "Skill-Up: Master's Program",
    description: 'Go farther with Skill-Up.',
    isPublished: true,
    weeks: [
      training_weeks.byId(6),
      training_weeks.byId(1),
      training_weeks.byId(5),
    ],
  ),
  TrainingProgram(
    id: 2,
    name: 'Disruptor Training: ' + ('Test ' * 15),
    isPublished: true,
    description:
        "You've Always Got Time For Disruptor Training. Apart from that, for UI testing, it is very important to have at least one very long description to test the wrapping capabilities.",
    weeks: [
      training_weeks.byId(1),
      training_weeks.byId(6),
      training_weeks.byId(5),
      training_weeks.byId(7),
    ],
  ),
  TrainingProgram(
    id: 3,
    name: 'Active Achievement',
    isPublished: false,
    //description: 'Why Have Cotton When You Can Have Active Achievement?', // no description => no blank line should appear in UI
    weeks: [
      training_weeks.byId(4),
    ],
  ),
  TrainingProgram(
    id: 4,
    name: 'Practice to Perfection',
    isPublished: true,
    description: "Life's beautiful with Practice to Perfection.",
    weeks: [
      training_weeks.byId(6),
      training_weeks.byId(2),
      training_weeks.byId(5),
      training_weeks.byId(2),
      training_weeks.byId(4),
    ],
  ),
  TrainingProgram(
    id: 5,
    name: 'Commission Kings',
    isPublished: true,
    description: 'Commission Kings. See more. Do more.',
    weeks: [
      training_weeks.byId(6),
      training_weeks.byId(7),
      training_weeks.byId(3),
    ],
  ),
  TrainingProgram(
    id: 6,
    name: 'Impact Training',
    isPublished: false,
    description: 'Impact Training a cut above the rest.',
    weeks: [
      training_weeks.byId(5),
      training_weeks.byId(4),
      training_weeks.byId(1),
      training_weeks.byId(7),
    ],
  ),
  TrainingProgram(
    id: 7,
    name: 'Excalibur Training',
    isPublished: false,
    description: 'Just one more Excalibur Training will do.',
    weeks: [
      training_weeks.byId(3),
      training_weeks.byId(5),
      training_weeks.byId(3),
    ],
  ),
  TrainingProgram(
    id: 8,
    name: 'Royal Bencher Training',
    isPublished: true,
    description: 'Royal Bencher: one size fits all.',
    weeks: [
      training_weeks.byId(1),
      training_weeks.byId(3),
      training_weeks.byId(7),
    ],
  ),
  TrainingProgram(
    id: 9,
    name: 'Top Cruncher Program',
    isPublished: false,
    description: "You Can't Top a Top Cruncher.",
    weeks: [
      training_weeks.byId(4),
      training_weeks.byId(2),
      training_weeks.byId(6),
    ],
  ),
  TrainingProgram(
    id: 10,
    name: 'Rejuvenating Course',
    isPublished: true,
    description: 'Refresh Yourself.',
    weeks: [
      training_weeks.byId(5),
      training_weeks.byId(3),
      training_weeks.byId(6),
      training_weeks.byId(2),
    ],
  ),
];

List<TrainingProgramOverview> getOverview() {
  return data
      .map(
        (final program) => TrainingProgramOverview(
          id: program.id,
          name: program.name,
          isPublished: program.isPublished,
          numberOfWeeks: program.weeks.length,
          description: program.description,
        ),
      )
      .toList();
}

void deleteProgram(final int programId) {
  data.removeWhere((final program) => program.id == programId);
}

List<TrainingWeek> getWeeks(final int programId) {
  return data.firstWhere((final program) => program.id == programId).weeks;
}

List<TrainingWeekOverview> getWeeksOverview(final int programId) {
  return data
      .firstWhere((final program) => program.id == programId)
      .weeks
      .map(
        (final week) => TrainingWeekOverview(
          id: week.id,
          number: week.number,
          numberOfDays: week.days.length,
          totalNumberOfWorkouts: _getTotalNumberOfWorkouts(week),
          description: week.description,
        ),
      )
      .toList();
}

List<TrainingDayOverview> getDays(final int weekId) {
  return training_weeks
      .byId(weekId)
      .days
      .map(
        (final day) => TrainingDayOverview(
          id: day.id,
          number: day.number,
          numberOfWorkouts: day.workouts.length,
          description: day.description,
          name: day.name,
        ),
      )
      .toList();
}

List<Workout> getWorkouts(final int dayId) {
  return training_days.byId(dayId).workouts;
}

int _getTotalNumberOfWorkouts(final TrainingWeek week) {
  return week.days.fold(0, (final previousValue, final element) => previousValue + element.workouts.length);
}
