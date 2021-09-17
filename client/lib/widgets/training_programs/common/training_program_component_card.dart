import 'package:flutter/material.dart';

class TrainingProgramComponentCard extends StatelessWidget {
  final String _headerText;
  final List<String?> _bodyTextLines;
  final void Function(BuildContext) detailsAction;
  final void Function(BuildContext) deleteAction;

  const TrainingProgramComponentCard(this._headerText, this._bodyTextLines, this.detailsAction, this.deleteAction, {Key? key}) : super(key: key);

  void _openDetailsScreen(BuildContext context) {
    detailsAction(context);
  }

  void _deleteComponent(BuildContext context) {
    deleteAction(context);
  }

  Widget _buildCardHeader() {
    return Text(
      _headerText,
      style: TextStyle(
        fontSize: 16,
        fontWeight: FontWeight.bold,
      ),
    );
  }

  List<Widget> _buildCardBody() {
    var body = <Widget>[];
    for (var i = 0; i < _bodyTextLines.length; i++) {
      if (_bodyTextLines[i] != null) {
        body.add(Text(_bodyTextLines[i]!));
      }
      if (i != _bodyTextLines.length - 1) {
        body.add(SizedBox(height: 5));
      }
    }
    return body;
  }

  Widget _buildCardFooter(BuildContext context) {
    final theme = Theme.of(context);
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        TextButton.icon(
          onPressed: () => _openDetailsScreen(context),
          icon: Icon(Icons.edit),
          label: Text('Details/Edit'),
        ),
        TextButton.icon(
          onPressed: () => _deleteComponent(context),
          icon: Icon(Icons.delete, color: theme.errorColor),
          label: Text('Delete'),
          style: TextButton.styleFrom(primary: theme.errorColor),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () => _openDetailsScreen(context),
      child: Container(
        width: double.infinity,
        margin: EdgeInsets.symmetric(vertical: 2.5),
        child: Card(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
          ),
          elevation: 3,
          margin: EdgeInsets.symmetric(horizontal: 10, vertical: 5),
          child: Padding(
            padding: const EdgeInsets.only(left: 10, top: 10, right: 10),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildCardHeader(),
                SizedBox(height: 5),
                ..._buildCardBody(),
                Divider(),
                _buildCardFooter(context),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
