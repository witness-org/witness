import 'dart:math' as math;

import 'package:client/extensions/context_extensions.dart';
import 'package:client/extensions/date_time_extensions.dart';
import 'package:flutter/material.dart';
import 'package:timezone/timezone.dart';

abstract class DialogHelper {
  DialogHelper._();

  static Future<bool?> getBool(
    final BuildContext context, {
    final String? title,
    required final String content,
    required final String falseOption,
    required final String trueOption,
    final TextStyle? falseOptionStyle,
    final TextStyle? trueOptionStyle,
  }) {
    return showDialog<bool>(
      context: context,
      builder: (final ctx) => AlertDialog(
        title: title != null ? Text(title) : null,
        content: Text(content),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: Text(
              falseOption,
              style: falseOptionStyle,
            ),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: Text(
              trueOption,
              style: trueOptionStyle,
            ),
          ),
        ],
      ),
    );
  }

  static Future<void> showText(
    final BuildContext context, {
    final String? title,
    required final String content,
    required final String closeText,
  }) {
    return showDialog<void>(
      context: context,
      builder: (final ctx) => AlertDialog(
        title: title != null ? Text(title) : null,
        content: Text(content),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: Text(
              closeText,
            ),
          ),
        ],
      ),
    );
  }

  static Widget getDatePicker(
    final BuildContext context,
    final String title,
    final TZDateTime referenceDate,
    final void Function(DateTime selectedDate) selectDateAction, {
    final void Function(DateTime displayedDate)? onDisplayedMonthChangedAction,
    final bool Function(DateTime selectableDay)? selectableDayPredicate,
  }) {
    final mediaQueryData = MediaQuery.of(context);
    final textScaleFactor = math.min(mediaQueryData.textScaleFactor, 1.3);
    final dialogSize = _getDatePickerDialogSize(mediaQueryData.orientation) * textScaleFactor;

    return Dialog(
      child: AnimatedContainer(
        width: dialogSize.width,
        height: dialogSize.height,
        duration: const Duration(milliseconds: 100),
        curve: Curves.easeIn,
        child: MediaQuery(
          data: mediaQueryData.copyWith(textScaleFactor: textScaleFactor),
          child: Builder(
            builder: (final BuildContext innerContext) => Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                _buildDatePickerHeader(title, innerContext),
                Expanded(
                  child: CalendarDatePicker(
                    initialDate: referenceDate,
                    firstDate: referenceDate.subtractYears(10),
                    lastDate: referenceDate.addYears(10),
                    onDateChanged: selectDateAction,
                    onDisplayedMonthChanged: onDisplayedMonthChangedAction,
                    selectableDayPredicate: selectableDayPredicate != null
                        ? (final date) =>
                            // predicate must be fulfilled for [initialDate] -> either provided function returns `true` or [_date] is equivalent to
                            // [initialDate] (which is [referenceDate]), not taking time components into consideration
                            selectableDayPredicate(date) || date.onlyTZDate().isAtSameMomentAs(referenceDate.onlyDate())
                        : null,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  static Widget _buildDatePickerHeader(final String title, final BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;
    final Color onPrimarySurface = context.isDarkModeEnabled() ? colorScheme.onSurface : colorScheme.onPrimary;

    return SizedBox(
      height: 70,
      child: Material(
        color: colorScheme.primary,
        child: Padding(
          padding: const EdgeInsetsDirectional.only(
            top: 20,
            start: 24,
          ),
          child: Text(title, style: textTheme.headlineSmall?.copyWith(color: onPrimarySurface)),
        ),
      ),
    );
  }

  static Size _getDatePickerDialogSize(final Orientation orientation) {
    switch (orientation) {
      case Orientation.portrait:
        return const Size(330.0, 440.0);
      case Orientation.landscape:
        return const Size(496.0, 346.0);
    }
  }
}
