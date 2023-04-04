import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/extensions/number_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/exercise_statistics.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/common/error_key_translator.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('exercise_statistics');

class ExerciseStatisticsPage extends StatefulWidget {
  const ExerciseStatisticsPage(this._exercise, {final Key? key}) : super(key: key);

  final Exercise _exercise;

  @override
  State<StatefulWidget> createState() => _ExerciseStatisticsPageState();
}

class _ExerciseStatisticsPageState extends State<ExerciseStatisticsPage>
    with LogMessagePreparer, StringLocalizer, ErrorKeyTranslator, AutomaticKeepAliveClientMixin<ExerciseStatisticsPage> {
  late Future<void> _fetchExerciseStatisticsResult;

  @override
  bool get wantKeepAlive => true;

  Future<void> _fetchExerciseStatistics() async {
    _logger.v(prepare('_fetchExerciseStatistics'));
    await Provider.of<ExerciseProvider>(context, listen: false).fetchExerciseStatistics(widget._exercise.id);
  }

  /// Wraps the [child] widget in a widget that is always scrollable without scroll glow. This is needed to display a widget that is not scrollable by
  /// default in a [RefreshIndicator] without losing the ability to refresh as the [RefreshIndicator] only works with scrollable widgets.
  Widget _buildRefreshableWidget(final Widget child) {
    return ScrollConfiguration(
      behavior: const ScrollBehavior().copyWith(overscroll: false),
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        child: SizedBox(
          height: MediaQuery.of(context).size.height - 300,
          child: child,
        ),
      ),
    );
  }

  Widget _buildExerciseStatistics(final StringLocalizations uiStrings) {
    _logger.v(prepare('_buildExerciseStatistics()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchExerciseStatisticsResult,
        builder: (final _, final snapshot) {
          return snapshot.waitSwitch(
            RefreshIndicator(
              onRefresh: _fetchExerciseStatistics,
              child: Consumer<ExerciseProvider>(
                builder: (final _, final exerciseData, final __) {
                  _logger.v(prepare('_buildExerciseStatistics.Consumer.builder()'));
                  final exerciseStatistics = exerciseData.getExercisesStatistics(widget._exercise.id);
                  return Scrollbar(
                    thumbVisibility: false,
                    child: exerciseStatistics != null
                        ? _buildRefreshableWidget(_ExerciseStatisticsParameters(exerciseStatistics, widget._exercise.loggingTypes))
                        : _buildRefreshableWidget(
                            Center(
                              child: Text(
                                uiStrings.exerciseStatisticsPage_noStatistics,
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                  );
                },
              ),
            ),
            errorWidget: (final error) => Center(
              child: Text(
                uiStrings.exerciseStatisticsPage_statistics_errorMessage(translate(uiStrings, error.toString())),
                textAlign: TextAlign.center,
              ),
            ),
          );
        },
      ),
    );
  }

  @override
  void initState() {
    super.initState();
    _fetchExerciseStatisticsResult = _fetchExerciseStatistics();
  }

  @override
  Widget build(final BuildContext context) {
    super.build(context);
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildExerciseStatistics(uiStrings),
      ],
    );
  }
}

class _ExerciseStatisticsParameters extends StatelessWidget with StringLocalizer {
  const _ExerciseStatisticsParameters(this._exerciseStatistics, this._loggingTypes, {final Key? key}) : super(key: key);

  final ExerciseStatistics _exerciseStatistics;
  final List<LoggingType> _loggingTypes;

  Widget _buildPaddedTableCell(final Widget child) {
    return TableCell(
      verticalAlignment: TableCellVerticalAlignment.middle,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 20),
        child: child,
      ),
    );
  }

  Widget _buildPaddedTextTableCell(final String text, {final bool isHeading = false}) {
    return _buildPaddedTableCell(
      Text(
        text,
        style: isHeading ? const TextStyle(fontWeight: FontWeight.bold) : null,
      ),
    );
  }

  Widget _buildOneRepMaximumInfoButton(final BuildContext context, final StringLocalizations uiStrings) {
    return IconButton(
      iconSize: 15,
      onPressed: () {
        DialogHelper.showText(
          context,
          title: uiStrings.exerciseStatisticsPage_statisticsEstimatedOneRepMaxDialog_heading,
          content: uiStrings.exerciseStatisticsPage_statisticsEstimatedOneRepMaxDialog_content,
          closeText: uiStrings.exerciseStatisticsPage_statisticsEstimatedOneRepMaxDialog_closeText,
        );
      },
      icon: const Icon(Icons.help),
    );
  }

  Widget _buildStatisticsParameters(final BuildContext context, final StringLocalizations uiStrings, final ExerciseStatistics statistics) {
    final hasRepsLogging = _loggingTypes.contains(LoggingType.reps);
    final hasTimeLogging = _loggingTypes.contains(LoggingType.time);

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 20),
      child: Column(
        children: [
          Table(
            defaultColumnWidth: const FixedColumnWidth(130),
            children: [
              TableRow(
                children: [
                  _buildPaddedTextTableCell(uiStrings.exerciseStatisticsPage_statistics_maximumWeightHeading, isHeading: true),
                  _buildPaddedTextTableCell(uiStrings.exerciseStatisticsPage_statistics_weightInKg(statistics.maxWeightG.gInKg.toString())),
                  _buildPaddedTableCell(Container()),
                ],
              ),
              if (hasRepsLogging && statistics.maxReps != null)
                TableRow(
                  children: [
                    _buildPaddedTextTableCell(uiStrings.exerciseStatisticsPage_statistics_maximumRepsHeading, isHeading: true),
                    _buildPaddedTextTableCell('${statistics.maxReps!}'),
                    _buildPaddedTableCell(Container()),
                  ],
                ),
              if (hasTimeLogging && statistics.maxSeconds != null)
                TableRow(
                  children: [
                    _buildPaddedTextTableCell(uiStrings.exerciseStatisticsPage_statistics_maximumTimeHeading, isHeading: true),
                    _buildPaddedTextTableCell(uiStrings.exerciseStatisticsPage_statistics_timeInS(statistics.maxSeconds!)),
                    _buildPaddedTableCell(Container()),
                  ],
                ),
              if (hasRepsLogging && statistics.estimatedOneRepMaxG != null)
                TableRow(
                  children: [
                    _buildPaddedTextTableCell(uiStrings.exerciseStatisticsPage_statistics_estimatedOneRepMaxHeading, isHeading: true),
                    _buildPaddedTextTableCell(
                        uiStrings.exerciseStatisticsPage_statistics_weightInKg(statistics.estimatedOneRepMaxG!.gInKg.toString())),
                    _buildPaddedTableCell(_buildOneRepMaximumInfoButton(context, uiStrings)),
                  ],
                ),
            ],
          ),
          if (hasRepsLogging && statistics.estimatedOneRepMaxG == null)
            Column(
              children: [
                const SizedBox(height: 30),
                Text(
                  uiStrings.exerciseStatisticsPage_statistics_estimatedOneRepMaxNotCalculatedHint,
                  textAlign: TextAlign.center,
                ),
              ],
            ),
        ],
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    return _buildStatisticsParameters(context, uiStrings, _exerciseStatistics);
  }
}
