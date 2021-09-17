import 'package:client/extensions/enum_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_information');

class ExerciseInformation extends StatelessWidget {
  final Exercise _exercise;

  const ExerciseInformation(this._exercise, {Key? key}) : super(key: key);

  Widget _buildHeading(String text) {
    return Container(
      margin: EdgeInsets.only(bottom: 4),
      child: Text(
        text,
        style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
      ),
    );
  }

  Widget _buildBadge(BuildContext context, String text, Color backgroundColor, Color textColor) {
    return Chip(
      materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
      label: Text(
        text,
        style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: textColor),
      ),
      backgroundColor: backgroundColor,
    );
  }

  Widget _buildBadgeList(BuildContext context, Iterable<String> badgeTexts, Color backgroundColor, Color textColor) {
    return Wrap(
      spacing: 6,
      runSpacing: 6,
      children: badgeTexts.map((text) => _buildBadge(context, text, backgroundColor, textColor)).toList(),
    );
  }

  Widget _buildExerciseInformation(BuildContext context) {
    _logger.v('$runtimeType._buildExerciseInformation()');
    final theme = Theme.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildHeading('Description'),
        Text(_exercise.description ?? ''),
        SizedBox(height: 15),
        _buildHeading('Tags'),
        _buildBadgeList(
          context,
          _exercise.tags.map((tag) => tag.name),
          theme.primaryColor,
          theme.colorScheme.onPrimary,
        ),
        SizedBox(height: 15),
        _buildHeading('Attributes'),
        _buildBadgeList(
          context,
          _exercise.attributes.map((attribute) => attribute.toUiString()),
          theme.colorScheme.secondary,
          theme.colorScheme.onSecondary,
        ),
        SizedBox(height: 15),
      ],
    );
  }

  Widget _buildStickyFooter(BuildContext context) {
    _logger.v('$runtimeType._buildStickyFooter()');
    return Padding(
      padding: EdgeInsets.only(left: 15, right: 15, bottom: 7),
      child: SizedBox(
        width: double.infinity,
        child: ElevatedButton.icon(
          onPressed: () => Navigator.of(context).pushNamed(EditExerciseScreen.routeName, arguments: _exercise),
          icon: Icon(Icons.edit),
          label: Text('Edit'),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          child: SingleChildScrollView(
            child: Padding(
              padding: EdgeInsets.only(left: 15, right: 15, top: 15),
              child: _buildExerciseInformation(context),
            ),
          ),
        ),
        Column(
          children: [
            Divider(),
            _buildStickyFooter(context),
          ],
        ),
      ],
    );
  }
}
