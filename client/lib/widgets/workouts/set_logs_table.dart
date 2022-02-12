import 'package:client/extensions/enum_extensions.dart';
import 'package:client/extensions/number_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/workouts/set_log_dialog.dart';
import 'package:client/widgets/workouts/set_log_form_input.dart';
import 'package:flutter/material.dart';
import 'package:reorderables/reorderables.dart';

final _logger = getLogger('set_logs_table');

class SetLogsTable extends StatefulWidget {
  const SetLogsTable(this._exerciseLog, this._updateSetLog, this._deleteSetLog, this._updateSetLogPositions, {final Key? key}) : super(key: key);

  final ExerciseLog _exerciseLog;
  final Future<void> Function(BuildContext context, SetLogFormInput setLogForm)? _updateSetLog;
  final Future<void> Function(BuildContext context, SetLog setLog)? _deleteSetLog;
  final Future<void> Function(BuildContext context, Map<String, int> positions)? _updateSetLogPositions;

  @override
  State<StatefulWidget> createState() => _SetLogsTableState();
}

class _SetLogsTableState extends State<SetLogsTable> with StringLocalizer, LogMessagePreparer {
  late List<SetLog> _setLogs = widget._exerciseLog.setLogs;

  void _reorderSetLogs(
    final int oldIndex,
    final int newIndex,
    final Future<void> Function(BuildContext context, Map<String, int> positions) positionUpdateAction,
  ) {
    setState(() {
      // this `setState()` call prevents the set log rows from "jumping around" due to them returning to their original positions before going to
      // their new positions when the server request is successfully completed
      final item = _setLogs.removeAt(oldIndex);
      _setLogs.insert(newIndex, item);
    });

    positionUpdateAction(context, _createPositionsMap(_setLogs));
  }

  Widget _buildErrorIndicator(final StringLocalizations uiStrings) {
    return Center(
      child: Text(uiStrings.setLogsTable_unknownSetLogsError),
    );
  }

  Widget _buildSeparator(final String separator, {final TextStyle? styling}) {
    return Padding(
      padding: const EdgeInsets.only(left: 5, right: 5),
      child: Text(
        separator,
        style: styling ?? const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        textAlign: TextAlign.center,
      ),
    );
  }

  List<Widget> _buildFieldWithTexts(
    final List<String> texts, {
    final String? separator,
    final TextStyle? separatorStyling,
    final TextAlign? textAlign = TextAlign.left,
  }) {
    return [
      if (separator != null) _buildSeparator(separator, styling: separatorStyling),
      ...texts.map((final item) {
        return Text(
          item,
          textAlign: textAlign,
        );
      })
    ];
  }

  List<Widget> _buildRepsTimeIndicatorField(final SetLog setLog, final StringLocalizations uiStrings) {
    if (setLog is RepsSetLog) {
      return _buildFieldWithTexts(
        [setLog.reps.toString(), ''],
        separator: uiStrings.setLogsTable_crossSeparator,
        separatorStyling: const TextStyle(fontSize: 15),
      );
    } else if (setLog is TimeSetLog) {
      return _buildFieldWithTexts(
        [setLog.seconds.toString(), uiStrings.setLogsTable_secondsSuffix],
        separator: uiStrings.setLogsTable_dotSeparator,
      );
    } else {
      return [];
    }
  }

  List<Widget> _buildRpeField(final int? rpe, final String rpePrefix, final String noRpePlaceholder) {
    return _buildFieldWithTexts([rpePrefix, rpe?.toString() ?? noRpePlaceholder]);
  }

  Widget _buildBandContainer(final Color color) {
    return Padding(
      padding: const EdgeInsets.only(left: 6),
      child: Container(
        width: 10,
        height: 10,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          color: color,
        ),
      ),
    );
  }

  Widget _buildBandsField(final List<ResistanceBand> bands, final String noResistanceBandsPlaceholder) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: bands.isNotEmpty ? bands.map((final band) => _buildBandContainer(band.mapToColor())).toList() : [Text(noResistanceBandsPlaceholder)],
    );
  }

  Widget _buildEditButton(final SetLog setLog, final Future<void> Function(BuildContext context, SetLogFormInput setLogForm) updateAction) {
    return IconButton(
      icon: const Icon(Icons.more_vert),
      onPressed: () => showDialog<String>(
        context: context,
        builder: (final BuildContext context) => SetLogDialog(
          widget._exerciseLog,
          setLog,
          updateAction,
          deleteSetLog: widget._deleteSetLog,
        ),
      ),
    );
  }

  List<Widget> _buildSetLogChildren(final StringLocalizations uiStrings, final SetLog setLog) {
    return [
      ..._buildFieldWithTexts([setLog.weightG.gInKg.toString(), uiStrings.setLogsTable_kgSuffix]),
      ..._buildRepsTimeIndicatorField(setLog, uiStrings),
      _buildBandsField(setLog.resistanceBands, uiStrings.setLogsTable_noResistanceBandsPlaceholder),
      ..._buildRpeField(setLog.rpe, uiStrings.setLogsTable_rpePrefix, uiStrings.setLogsTable_noRpePlaceholder),
      if (widget._updateSetLog != null) _buildEditButton(setLog, widget._updateSetLog!)
    ];
  }

  // override needed because otherwise, changes in the set logs would not be passed on from the widget to the state and hence, not displayed in the UI
  @override
  void didUpdateWidget(final SetLogsTable oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget._exerciseLog.setLogs != oldWidget._exerciseLog.setLogs) {
      _setLogs = widget._exerciseLog.setLogs;
    }
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    final allSetLogsValid = _setLogs.every((final log) => (log is RepsSetLog) || (log is TimeSetLog));
    if (!allSetLogsValid) {
      return _buildErrorIndicator(uiStrings);
    }

    return widget._updateSetLogPositions != null
        ? ReorderableTable(
            defaultVerticalAlignment: TableCellVerticalAlignment.middle,
            onReorder: (final int oldIndex, final int newIndex) => _reorderSetLogs(oldIndex, newIndex, widget._updateSetLogPositions!),
            children: _setLogs
                .map((final setLog) => ReorderableTableRow(
                      key: ValueKey(setLog.id),
                      mainAxisSize: MainAxisSize.max,
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: _buildSetLogChildren(uiStrings, setLog),
                    ))
                .toList(),
          )
        : Table(
            columnWidths: const {
              0: FlexColumnWidth(1), // 55.5
              1: FlexColumnWidth(0.5), // kg
              2: FlexColumnWidth(0.5), // x
              3: FlexColumnWidth(1), // 8
              4: FlexColumnWidth(0.25), // s
              5: FlexColumnWidth(2), // *****
              6: FlexColumnWidth(1), // RPE
              7: FlexColumnWidth(0.75) // 4
            },
            defaultVerticalAlignment: TableCellVerticalAlignment.middle,
            children: _setLogs
                .map((final setLog) => TableRow(
                      key: ValueKey(setLog.id),
                      children: _buildSetLogChildren(uiStrings, setLog),
                    ))
                .toList(),
          );
  }

  static Map<String, int> _createPositionsMap(final List<SetLog> setLogs) {
    var counter = 1;
    return {for (final log in setLogs) log.id.toString(): counter++};
  }
}
