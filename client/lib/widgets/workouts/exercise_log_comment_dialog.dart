import 'package:client/extensions/string_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_log_comment_dialog');

class ExerciseLogCommentDialog extends StatefulWidget {
  const ExerciseLogCommentDialog(this._comment, this._updateExerciseLogComment, {final Key? key}) : super(key: key);

  final String? _comment;
  final Future<void> Function(BuildContext context, String? updatedComment) _updateExerciseLogComment;

  @override
  State<StatefulWidget> createState() => _ExerciseLogCommentDialogState();
}

class _ExerciseLogCommentDialogState extends State<ExerciseLogCommentDialog> with StringLocalizer, LogMessagePreparer {
  final _formKey = GlobalKey<FormState>();
  late String? _comment = widget._comment;
  late bool _isCommentBlank = _comment.isNullOrBlank;
  late final TextEditingController _textController = TextEditingController(text: _comment);

  void _setIsCommentBlank() {
    setState(() {
      _isCommentBlank = _textController.text.isNullOrBlank;
    });
  }

  String? _validateComment(final String? value, final StringLocalizations uiStrings) {
    if (value.isNullOrBlank) {
      return uiStrings.exerciseLogItem_exerciseLogCommentDialog_commentBlankError;
    }

    if (value != null && value.length > 256) {
      return uiStrings.exerciseLogItem_exerciseLogCommentDialog_commentTooLongError;
    }

    return null;
  }

  Widget _buildForm(final StringLocalizations uiStrings) {
    return Form(
      key: _formKey,
      child: SizedBox(
        width: 800.0,
        child: TextFormField(
          controller: _textController,
          maxLength: 256,
          decoration: InputDecoration(
            icon: const Icon(Icons.notes_outlined),
            errorMaxLines: 2,
            labelText: uiStrings.exerciseLogItem_exerciseLogCommentDialog_commentLabel,
            border: const OutlineInputBorder(),
          ),
          validator: (final value) => _validateComment(value, uiStrings),
          onSaved: (final value) => _comment = value,
          maxLines: null,
        ),
      ),
    );
  }

  List<Widget> _buildActionButtons(final BuildContext context, final StringLocalizations uiStrings) {
    return [
      TextButton(
        onPressed: () => Navigator.pop(context),
        child: Text(uiStrings.exerciseLogItem_exerciseLogCommentDialog_cancel),
      ),
      if (widget._comment != null)
        TextButton(
          style: TextButton.styleFrom(foregroundColor: Theme.of(context).colorScheme.error),
          onPressed: () {
            Navigator.pop(context);
            widget._updateExerciseLogComment(context, null);
          },
          child: Text(uiStrings.exerciseLogItem_exerciseLogCommentDialog_delete),
        ),
      TextButton(
        onPressed: _isCommentBlank
            ? null
            : () {
                if (_formKey.currentState != null && _formKey.currentState!.validate()) {
                  _formKey.currentState!.save();
                  Navigator.pop(context);
                  widget._updateExerciseLogComment(context, _comment);
                }
              },
        child: Text(uiStrings.exerciseLogItem_exerciseLogCommentDialog_confirm),
      ),
    ];
  }

  @override
  void initState() {
    super.initState();
    _textController.addListener(_setIsCommentBlank);
  }

  @override
  void dispose() {
    super.dispose();
    _textController.dispose();
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return AlertDialog(
      title: Text(uiStrings.exerciseLogItem_exerciseLogCommentDialog_title),
      content: _buildForm(uiStrings),
      actions: _buildActionButtons(context, uiStrings),
    );
  }
}
