import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';

class TrainingProgramComponentCard extends StatelessWidget with StringLocalizer {
  const TrainingProgramComponentCard(
    final this._headerText,
    final this._bodyTextLines,
    final this.detailsAction,
    final this.deleteAction, {
    final Key? key,
  }) : super(key: key);

  final String _headerText;
  final List<String?> _bodyTextLines;
  final void Function(BuildContext context, StringLocalizations localizations) detailsAction;
  final void Function(BuildContext context, StringLocalizations localizations) deleteAction;

  void _openDetailsScreen(final BuildContext context, final StringLocalizations uiStrings) {
    detailsAction(context, uiStrings);
  }

  void _deleteComponent(final BuildContext context, final StringLocalizations uiStrings) {
    deleteAction(context, uiStrings);
  }

  Widget _buildCardHeader() {
    return Text(
      _headerText,
      style: const TextStyle(
        fontSize: 16,
        fontWeight: FontWeight.bold,
      ),
    );
  }

  List<Widget> _buildCardBody() {
    final body = <Widget>[];
    for (var i = 0; i < _bodyTextLines.length; i++) {
      if (_bodyTextLines[i] != null) {
        body.add(Text(_bodyTextLines[i]!));
      }
      if (i != _bodyTextLines.length - 1) {
        body.add(const SizedBox(height: 5));
      }
    }
    return body;
  }

  Widget _buildCardFooter(final BuildContext context, final StringLocalizations uiStrings) {
    final theme = Theme.of(context);
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        TextButton.icon(
          onPressed: () => _openDetailsScreen(context, uiStrings),
          icon: const Icon(Icons.edit),
          label: Text(uiStrings.trainingProgramComponentCard_footer_details),
        ),
        TextButton.icon(
          onPressed: () => _deleteComponent(context, uiStrings),
          icon: Icon(Icons.delete, color: theme.errorColor),
          label: Text(uiStrings.trainingProgramComponentCard_footer_delete),
          style: TextButton.styleFrom(primary: theme.errorColor),
        ),
      ],
    );
  }

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    return InkWell(
      onTap: () => _openDetailsScreen(context, uiStrings),
      child: Container(
        width: double.infinity,
        margin: const EdgeInsets.symmetric(vertical: 2.5),
        child: Card(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
          ),
          elevation: 3,
          margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
          child: Padding(
            padding: const EdgeInsets.only(left: 10, top: 10, right: 10),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildCardHeader(),
                const SizedBox(height: 5),
                ..._buildCardBody(),
                const Divider(),
                _buildCardFooter(context, uiStrings),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
