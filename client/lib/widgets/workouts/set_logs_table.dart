import 'package:client/extensions/enum_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';

class SetLogTable extends StatefulWidget with LogMessagePreparer {
  const SetLogTable(this.setLogs, {final Key? key}) : super(key: key);
  final List<SetLog> setLogs;

  @override
  SetLogTableState createState() => SetLogTableState();
}

class SetLogTableState extends State<SetLogTable> with StringLocalizer {
  List<SetLog>? _setLogs;

  Widget _buildSeparator(final String separator, {final TextStyle? styling}) {
    return TableCell(
      child: Padding(
        padding: const EdgeInsets.only(left: 5, right: 5),
        child: Text(
          separator,
          style: styling ??
              const TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
          textAlign: TextAlign.center,
        ),
      ),
    );
  }

  List<Widget> _buildFieldWithTexts(final List<String> texts,
      {final String? separator, final TextStyle? separatorStyling, final TextAlign? textAlign = TextAlign.right}) {
    return [
      if (separator != null) _buildSeparator(separator, styling: separatorStyling),
      ...texts.map((final item) {
        return TableCell(
          child: Text(
            item,
            textAlign: textAlign,
          ),
        );
      })
    ];
  }

  List<Widget> _buildEmpty(final int number) {
    return [
      for (var i = 0; i < number; i++)
        TableCell(
          child: Container(),
        )
    ];
  }

  List<Widget> _buildRepsOrSecondsIndicatorField(final SetLog setLog, final StringLocalizations uiStrings) {
    if (setLog is RepsSetLog) {
      return _buildFieldWithTexts(
        [setLog.reps.toString()],
        separator: uiStrings.setLogsTable_crossSeparator,
        separatorStyling: const TextStyle(fontSize: 15),
      );
    } else if (setLog is TimeSetLog) {
      return _buildFieldWithTexts(
        [setLog.seconds.toString(), uiStrings.setLogsTable_secondsSuffix],
        separator: uiStrings.setLogsTable_dotSeparator,
      );
    }

    throw Exception('Encountered invalid set log!');
  }

  List<Widget> _buildRpeField(final int? rpe, final String rpePrefix) {
    if (rpe != null) {
      return _buildFieldWithTexts([rpePrefix, rpe.toString()]);
    } else {
      return _buildEmpty(2);
    }
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
    return TableCell(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: bands.isNotEmpty
            ? bands.map((final band) {
                return _buildBandContainer(band.mapToColor());
              }).toList()
            : [
                Text(noResistanceBandsPlaceholder),
              ],
      ),
    );
  }

  Widget _buildEditButton() {
    return TableCell(
        child: IconButton(
      icon: const Icon(Icons.more_vert),
      onPressed: () {},
    ));
  }

  @override
  void initState() {
    super.initState();

    _setLogs = widget.setLogs;
  }

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    return Table(
      columnWidths: const <int, TableColumnWidth>{
        0: FixedColumnWidth(40),
        1: FixedColumnWidth(20),
        2: FixedColumnWidth(20),
        3: FixedColumnWidth(20),
        4: FlexColumnWidth(),
        5: IntrinsicColumnWidth(),
        6: FixedColumnWidth(20),
        7: FixedColumnWidth(20)
      },
      defaultVerticalAlignment: TableCellVerticalAlignment.middle,
      children: [
        ..._setLogs!.map((final item) {
          return TableRow(
            children: [
              ..._buildFieldWithTexts([item.weightKg.toString(), uiStrings.setLogsTable_kgSuffix]),
              ..._buildRepsOrSecondsIndicatorField(item, uiStrings),
              _buildBandsField(item.resistanceBands, uiStrings.setLogsTable_noResistanceBandsPlaceholder),
              ..._buildRpeField(item.rpe, uiStrings.setLogsTable_rpePrefix),
              _buildEditButton()
            ],
          );
        }).toList()
      ],
    );
  }
}
