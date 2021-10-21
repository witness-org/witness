import 'package:client/extensions/enum_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_information');

class ExerciseInformation extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const ExerciseInformation(this._exercise, {final Key? key}) : super(key: key);

  final Exercise _exercise;

  Widget _buildHeading(final String text) {
    return Container(
      margin: const EdgeInsets.only(bottom: 4),
      child: Text(
        text,
        style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
      ),
    );
  }

  Widget _buildChip(final BuildContext context, final String text, final Color backgroundColor, final Color textColor) {
    return Chip(
      materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
      label: Text(
        text,
        style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: textColor),
      ),
      backgroundColor: backgroundColor,
    );
  }

  Widget _buildChipList(final BuildContext context, final Iterable<String> chipText, final Color backgroundColor, final Color textColor) {
    return Wrap(
      spacing: 6,
      runSpacing: 6,
      children: chipText.map((final text) => _buildChip(context, text, backgroundColor, textColor)).toList(),
    );
  }

  Widget _buildExerciseInformation(final BuildContext context, final StringLocalizations uiStrings) {
    _logger.v(prepare('_buildExerciseInformation()'));
    final theme = Theme.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildHeading(uiStrings.exerciseInformation_body_heading_description),
        Text(_exercise.description ?? uiStrings.exerciseInformation_body_description_empty),
        const SizedBox(height: 15),
        _buildHeading(uiStrings.exerciseInformation_body_heading_muscleGroups),
        _buildChipList(
          context,
          _exercise.muscleGroups.map((final group) => group.toUiString()),
          theme.primaryColor,
          theme.colorScheme.onPrimary,
        ),
        const SizedBox(height: 15),
        _buildHeading(uiStrings.exerciseInformation_body_heading_loggingTypes),
        _buildChipList(
          context,
          _exercise.loggingTypes.map((final attribute) => attribute.toUiString()),
          theme.colorScheme.secondary,
          theme.colorScheme.onSecondary,
        ),
        const SizedBox(height: 15),
      ],
    );
  }

  Widget _buildStickyFooter(final BuildContext context, final StringLocalizations uiStrings) {
    _logger.v(prepare('_buildStickyFooter()'));
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, bottom: 7),
      child: SizedBox(
        width: double.infinity,
        child: ElevatedButton.icon(
          onPressed: () => Navigator.of(context).pushNamed(EditExerciseScreen.routeName, arguments: _exercise),
          icon: const Icon(Icons.edit),
          label: Text(uiStrings.exerciseInformation_footer_editButton_text),
        ),
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          child: SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.only(left: 15, right: 15, top: 15),
              child: _buildExerciseInformation(context, uiStrings),
            ),
          ),
        ),
        Column(
          children: [
            const Divider(),
            _buildStickyFooter(context, uiStrings),
          ],
        ),
      ],
    );
  }
}
