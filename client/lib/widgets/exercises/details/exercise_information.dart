import 'package:client/extensions/enum_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_information');

class ExerciseInformation extends StatelessWidget with LogMessagePreparer {
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

  Widget _buildBadge(final BuildContext context, final String text, final Color backgroundColor, final Color textColor) {
    return Chip(
      materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
      label: Text(
        text,
        style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: textColor),
      ),
      backgroundColor: backgroundColor,
    );
  }

  Widget _buildBadgeList(final BuildContext context, final Iterable<String> badgeTexts, final Color backgroundColor, final Color textColor) {
    return Wrap(
      spacing: 6,
      runSpacing: 6,
      children: badgeTexts.map((final text) => _buildBadge(context, text, backgroundColor, textColor)).toList(),
    );
  }

  Widget _buildExerciseInformation(final BuildContext context) {
    _logger.v(prepare('_buildExerciseInformation()'));
    final theme = Theme.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildHeading('Description'),
        Text(_exercise.description ?? ''),
        const SizedBox(height: 15),
        _buildHeading('Tags'),
        _buildBadgeList(
          context,
          _exercise.tags.map((final tag) => tag.name),
          theme.primaryColor,
          theme.colorScheme.onPrimary,
        ),
        const SizedBox(height: 15),
        _buildHeading('Attributes'),
        _buildBadgeList(
          context,
          _exercise.attributes.map((final attribute) => attribute.toUiString()),
          theme.colorScheme.secondary,
          theme.colorScheme.onSecondary,
        ),
        const SizedBox(height: 15),
      ],
    );
  }

  Widget _buildStickyFooter(final BuildContext context) {
    _logger.v(prepare('_buildStickyFooter()'));
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, bottom: 7),
      child: SizedBox(
        width: double.infinity,
        child: ElevatedButton.icon(
          onPressed: () => Navigator.of(context).pushNamed(EditExerciseScreen.routeName, arguments: _exercise),
          icon: const Icon(Icons.edit),
          label: const Text('Edit'),
        ),
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          child: SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.only(left: 15, right: 15, top: 15),
              child: _buildExerciseInformation(context),
            ),
          ),
        ),
        Column(
          children: [
            const Divider(),
            _buildStickyFooter(context),
          ],
        ),
      ],
    );
  }
}