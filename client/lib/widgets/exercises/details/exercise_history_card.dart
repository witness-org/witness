import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/models/exercises/exercise_history_entry.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/workouts/exercise_log_item_content.dart';
import 'package:flutter/material.dart';

class ExerciseHistoryCard extends StatelessWidget with StringLocalizer {
  const ExerciseHistoryCard(this._historyEntry, {final Key? key}) : super(key: key);

  final ExerciseHistoryEntry _historyEntry;

  Widget _buildCommentLabel(final BuildContext context, final StringLocalizations uiStrings, final String comment) {
    return Padding(
      padding: const EdgeInsets.only(left: 5, right: 5),
      child: TextButton(
        child: Row(
          children: [
            const Icon(Icons.notes_outlined, size: 15),
            const SizedBox(width: 5),
            Flexible(
              child: Text(
                comment,
                overflow: TextOverflow.ellipsis,
              ),
            ),
          ],
        ),
        style: TextButton.styleFrom(
          tapTargetSize: MaterialTapTargetSize.shrinkWrap,
          textStyle: const TextStyle(fontWeight: FontWeight.normal),
        ),
        onPressed: () {
          DialogHelper.showText(
            context,
            title: uiStrings.exerciseHistoryCard_logCommentDialog_title,
            content: comment,
            closeText: uiStrings.exerciseHistoryCard_logCommentDialog_closeText,
          );
        },
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 3, horizontal: 10),
      child: Card(
        elevation: 2.5,
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 8),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Padding(
                padding: const EdgeInsets.only(left: 14, right: 14),
                child: Text(
                  _historyEntry.loggedOn.getStringRepresentation(),
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
              ),
              const SizedBox(height: 4),
              if (_historyEntry.exerciseLog.comment != null) _buildCommentLabel(context, uiStrings, _historyEntry.exerciseLog.comment!),
              const SizedBox(height: 8),
              ExerciseLogItemContent(null, _historyEntry.exerciseLog),
            ],
          ),
        ),
      ),
    );
  }
}
