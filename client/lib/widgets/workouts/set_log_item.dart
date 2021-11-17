import 'package:client/logging/log_message_preparer.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class SetLogItem extends StatefulWidget with LogMessagePreparer {
  const SetLogItem(this.setLog, {final Key? key}) : super(key: key);
  final SetLog setLog;

  @override
  State<StatefulWidget> createState() => SetLogItemState();
}

class SetLogItemState extends State<SetLogItem> with StringLocalizer, LogMessagePreparer {
  SetLog? _setLog;

  Widget _buildTextRowElement(final String text, {final String? separator, final TextStyle? separatorStyling}) {
    return Row(
      children: [
        if (separator != null)
          Padding(
            padding: const EdgeInsets.only(left: 5, right: 5),
            child: Text(
              separator,
              style: separatorStyling ??
                  const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
        Text(
          text,
          style: const TextStyle(color: Colors.black),
        )
      ],
    );
  }

  Widget _buildRepsOrSecondsIndicatorElement(final SetLog setLog) {
    if (setLog is RepsSetLog) {
      return _buildTextRowElement(setLog.reps.toString(),
          separator: '×',
          separatorStyling: const TextStyle(
            fontSize: 15,
          ));
    } else if (setLog is TimeSetLog) {
      return _buildTextRowElement(
        setLog.seconds.toString() + 's.',
        separator: '•',
      );
    }

    throw Exception('Encountered invalid set log!');
  }

  @override
  void initState() {
    super.initState();

    _setLog = widget.setLog;
  }

  @override
  Widget build(final BuildContext context) {
    return OutlinedButton(
      child: Row(
        children: [
          _buildTextRowElement(_setLog!.weightKg.toString() + 'kg'),
          _buildRepsOrSecondsIndicatorElement(_setLog!),
          if (_setLog!.rpe != null) _buildTextRowElement('RPE: ' + _setLog!.rpe.toString() ?? '', separator: '⋅'),
        ],
      ),
      onPressed: () {},
    );
  }
}
