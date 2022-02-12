import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/widgets/common/error_key_translator.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/details/exercise_history_card.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('exercise_history');

class ExerciseHistoryPage extends StatefulWidget {
  const ExerciseHistoryPage(this._exercise, {final Key? key}) : super(key: key);

  final Exercise _exercise;

  @override
  State<ExerciseHistoryPage> createState() => _ExerciseHistoryPageState();
}

class _ExerciseHistoryPageState extends State<ExerciseHistoryPage>
    with LogMessagePreparer, StringLocalizer, ErrorKeyTranslator, AutomaticKeepAliveClientMixin<ExerciseHistoryPage> {
  final ScrollController _scrollController = ScrollController();
  late Future<void> _fetchExerciseHistoryResult;

  @override
  bool get wantKeepAlive => true;

  Future<void> _fetchExerciseHistory(final BuildContext context) async {
    _logger.v(prepare('_fetchExerciseHistory'));
    await Provider.of<ExerciseProvider>(context, listen: false).fetchExerciseHistory(widget._exercise.id);
  }

  Widget _buildExerciseHistory(final BuildContext context, final StringLocalizations uiStrings) {
    _logger.v(prepare('_buildExerciseHistory()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchExerciseHistoryResult,
        builder: (final _, final snapshot) {
          return snapshot.waitSwitch(
            RefreshIndicator(
              onRefresh: () => _fetchExerciseHistory(context),
              child: Consumer<ExerciseProvider>(
                builder: (final _, final exerciseData, final __) {
                  _logger.v(prepare('_buildExerciseHistory.Consumer.builder()'));
                  final hasHistoryEntries = exerciseData.getExercisesHistory(widget._exercise.id) != null;
                  return Scrollbar(
                    isAlwaysShown: hasHistoryEntries,
                    controller: _scrollController,
                    child: hasHistoryEntries
                        ? ListView.builder(
                            controller: _scrollController,
                            physics: const AlwaysScrollableScrollPhysics(),
                            itemCount: exerciseData.getExercisesHistory(widget._exercise.id)?.entries.length ?? 0,
                            itemBuilder: (final _, final index) {
                              final historyEntry = exerciseData.getExercisesHistory(widget._exercise.id)!.entries[index];
                              return ExerciseHistoryCard(historyEntry);
                            },
                          )
                        : ScrollConfiguration(
                            behavior: const ScrollBehavior().copyWith(overscroll: false),
                            child: SingleChildScrollView(
                              physics: const AlwaysScrollableScrollPhysics(),
                              child: SizedBox(
                                height: MediaQuery.of(context).size.height - 300,
                                child: Center(
                                  child: Text(
                                    uiStrings.exerciseHistoryPage_emptyHistory,
                                    textAlign: TextAlign.center,
                                  ),
                                ),
                              ),
                            ),
                          ),
                  );
                },
              ),
            ),
            errorWidget: (final error) => Center(
              child: Text(
                uiStrings.exerciseHistoryPage_historyEntries_errorMessage(translate(uiStrings, error.toString())),
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
    _fetchExerciseHistoryResult = _fetchExerciseHistory(context);
  }

  @override
  Widget build(final BuildContext context) {
    super.build(context);
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildExerciseHistory(context, uiStrings),
      ],
    );
  }
}
