package com.witness.server.integration.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.workout.ExerciseLogCreateDto;
import com.witness.server.dto.workout.ExerciseLogDto;
import com.witness.server.dto.workout.RepsSetLogCreateDto;
import com.witness.server.dto.workout.RepsSetLogDto;
import com.witness.server.dto.workout.SetLogCreateDto;
import com.witness.server.dto.workout.SetLogDto;
import com.witness.server.dto.workout.TimeSetLogCreateDto;
import com.witness.server.dto.workout.TimeSetLogDto;
import com.witness.server.dto.workout.WorkoutLogCreateDto;
import com.witness.server.dto.workout.WorkoutLogDto;
import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.user.User;
import com.witness.server.entity.workout.WorkoutLog;
import com.witness.server.repository.ExerciseLogRepository;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.SetLogRepository;
import com.witness.server.repository.WorkoutLogRepository;
import com.witness.server.util.Comparators;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class WorkoutLogControllerTest extends BaseControllerIntegrationTest {
  private static final String DATA_ROOT = "data/integration/web/workout-log-controller-test/";

  private static final String GET_LOGGING_DAYS_URL = "/logging-days";
  private static final String SET_WORKOUT_DURATION_URL = "%s";
  private static final String DELETE_WORKOUT_LOG_URL = "%s";
  private static final String ADD_EXERCISE_LOG_URL = "%s/exercise-logs";
  private static final String UPDATE_EXERCISE_LOG_POSITIONS_URL = "%s/exercise-logs-positions";
  private static final String DELETE_EXERCISE_LOG_URL = "%s/exercise-logs/%s";
  private static final String ADD_SET_LOG_URL = "%s/exercise-logs/%s/set-logs";
  private static final String UPDATE_SET_LOG_URL = "%s/exercise-logs/%s/set-logs";
  private static final String UPDATE_SET_LOG_POSITIONS_URL = "%s/exercise-logs/%s/set-logs-positions";
  private static final String DELETE_SET_LOG_URL = "%s/exercise-logs/%s/set-logs/%s";

  @Autowired
  private WorkoutLogRepository workoutLogRepository;

  @Autowired
  private ExerciseLogRepository exerciseLogRepository;

  @Autowired
  private SetLogRepository setLogRepository;

  @Autowired
  private ExerciseRepository exerciseRepository;

  @Override
  String getEndpointUrl() {
    return "workout-logs";
  }

  //region all workout logs by date

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "GetWorkoutLogsInput1.json", type = GetByDayTestSpecification.class)
  })
  void getWorkoutLogs_matchingPersistedWorkoutLogs_return200AndWorkoutLogs(GetByDayTestSpecification specification) {
    persistUserAndMockLoggedIn(specification.currentUser);
    persistUsers(specification.persistedUsers);
    persistEntities(workoutLogRepository, specification.persistedWorkoutLogs);

    var queryParams = toMultiValueMap(Map.of("date", specification.searchDate));
    var response = get(TestAuthentication.REGULAR, requestUrl(), queryParams, WorkoutLogDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .isEqualTo(specification.expectedWorkoutLogs);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "GetWorkoutLogsInput2.json", type = GetByDayTestSpecification.class)
  })
  void getWorkoutLogs_noMatchingPersistedWorkoutLogs_return200AndEmptyList(GetByDayTestSpecification specification) {
    persistUserAndMockLoggedIn(specification.currentUser);
    persistUsers(specification.persistedUsers);
    persistEntities(workoutLogRepository, specification.persistedWorkoutLogs);

    var queryParams = toMultiValueMap(Map.of("date", specification.searchDate));
    var response = get(TestAuthentication.REGULAR, requestUrl(), queryParams, WorkoutLogDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEmpty();
  }

  //endregion

  //region all logging days in period

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "GetLoggingDaysInput1.json", type = GetByPeriodTestSpecification.class)
  })
  void getLoggingDays_matchingNonEmptyPersistedWorkoutLogs_return200AndMap(GetByPeriodTestSpecification specification) {
    persistUserAndMockLoggedIn(specification.currentUser);
    persistUsers(specification.persistedUsers);
    persistEntities(exerciseRepository, specification.persistedExercises);
    persistEntities(workoutLogRepository, specification.persistedWorkoutLogs);

    var queryParams = toMultiValueMap(Map.of("startDate", specification.startDate, "endDate", specification.endDate));
    var response = get(TestAuthentication.REGULAR, requestUrl(GET_LOGGING_DAYS_URL), queryParams,
        new ParameterizedTypeReference<HashMap<ZonedDateTime, Integer>>() {
        });
    var responseMap = response.getBody();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseMap).hasSameSizeAs(specification.expectedMapping);
    assertThat(responseMap).isNotEmpty();
    assertThat(responseMap.entrySet())
        .usingElementComparator(Comparators.LOGGING_DAY_COMPARATOR)
        .isEqualTo(specification.expectedMapping.entrySet());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "GetLoggingDaysInput2.json", type = GetByPeriodTestSpecification.class)
  })
  void getLoggingDays_noNonEmptyPersistedWorkoutLogs_return200AndEmptyMap(GetByPeriodTestSpecification specification) {
    persistUserAndMockLoggedIn(specification.currentUser);
    persistUsers(specification.persistedUsers);
    persistEntities(exerciseRepository, specification.persistedExercises);
    persistEntities(workoutLogRepository, specification.persistedWorkoutLogs);

    var queryParams = toMultiValueMap(Map.of("startDate", specification.startDate, "endDate", specification.endDate));
    var response = get(TestAuthentication.REGULAR, requestUrl(GET_LOGGING_DAYS_URL), queryParams,
        new ParameterizedTypeReference<HashMap<ZonedDateTime, Integer>>() {
        });

    var responseMap = response.getBody();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseMap).isNotNull();
    assertThat(responseMap).isEmpty();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "GetLoggingDaysInputStartDateAfterEndDate.json", type = GetByPeriodTestSpecification.class)
  })
  void getLoggingDays_startDateAfterEndDate_return400(GetByPeriodTestSpecification specification) {
    persistUserAndMockLoggedIn(specification.currentUser);

    var queryParams = toMultiValueMap(Map.of("startDate", specification.startDate, "endDate", specification.endDate));
    var response = get(TestAuthentication.REGULAR, requestUrl(GET_LOGGING_DAYS_URL), queryParams, Map.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  //endregion

  //region create workout log

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogCreateDto1.json", type = WorkoutLogCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogDtoFromCreateDto1.json", type = WorkoutLogDto.class)
  })
  void createNewWorkoutLog_validRequest_return201AndCreatedWorkoutLog(User currentUser, Exercise persistedExercise, WorkoutLogCreateDto createDto,
                                                                      WorkoutLogDto createdDto) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.REGULAR, requestUrl(), HttpMethod.POST, createDto, WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .isEqualTo(createdDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "InvalidWorkoutLogCreateDto.json", type = WorkoutLogCreateDto.class)
  })
  void createNewWorkoutLog_validRequest_return201AndCreatedWorkoutLog(User currentUser, Exercise persistedExercise, WorkoutLogCreateDto createDto) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.REGULAR, requestUrl(), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise2.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogCreateDto2.json", type = WorkoutLogCreateDto.class)
  })
  void createNewWorkoutLog_invalidLoggingType_return400(User currentUser, Exercise persistedExercise, WorkoutLogCreateDto createDto) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.REGULAR, requestUrl(), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogCreateDto1.json", type = WorkoutLogCreateDto.class)
  })
  void createNewWorkoutLog_exerciseDoesNotExist_return404(User currentUser, WorkoutLogCreateDto createDto) {
    persistUserAndMockLoggedIn(currentUser);

    var response = exchange(TestAuthentication.REGULAR, requestUrl(), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  //endregion

  //region set workout log duration

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class)
  })
  void setWorkoutLogDuration_ownerValidRequest_return200AndModifiedWorkoutLog(User currentUser, WorkoutLog persistedWorkoutLog) {
    var newDuration = 124;
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(SET_WORKOUT_DURATION_URL, persistedWorkoutLog.getId()),
        HttpMethod.PATCH,
        newDuration,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDurationMinutes()).isEqualTo(newDuration);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "AdminUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class)
  })
  void setWorkoutLogDuration_adminNonOwnerRequest_return200AndModifiedWorkoutLog(User currentUser, User workoutOwner,
                                                                                 WorkoutLog persistedWorkoutLog) {
    final var newDuration = 124;
    persistUsers(workoutOwner); // must be persisted before currentUser because otherwise ID in persistedWorkoutLog references wrong User
    persistUserAndMockLoggedIn(currentUser.toBuilder().id(2L).build());
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(SET_WORKOUT_DURATION_URL, persistedWorkoutLog.getId()),
        HttpMethod.PATCH,
        newDuration,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDurationMinutes()).isEqualTo(newDuration);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser2.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class)
  })
  void setWorkoutLogDuration_regularNonOwnerRequest_return400(User currentUser, User workoutOwner, WorkoutLog persistedWorkoutLog) {
    final var newDuration = 124;
    persistUsers(workoutOwner); // must be persisted before currentUser because otherwise ID in persistedWorkoutLog references wrong User
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(SET_WORKOUT_DURATION_URL, persistedWorkoutLog.getId()),
        HttpMethod.PATCH,
        newDuration,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class)
  })
  void setWorkoutLogDuration_workoutLogDoesNotExist_return404(User currentUser, WorkoutLog persistedWorkoutLog) {
    var newDuration = 124;
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(SET_WORKOUT_DURATION_URL, persistedWorkoutLog.getId() - 1),
        HttpMethod.PATCH,
        newDuration,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  //endregion

  //region delete workout log

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class)
  })
  void deleteWorkoutLog_validRequest_return204(User currentUser, WorkoutLog persistedWorkoutLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_WORKOUT_LOG_URL, persistedWorkoutLog.getId()),
        HttpMethod.DELETE,
        Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class)
  })
  void deleteWorkoutLog_workoutLogDoesNotExist_return404(User currentUser) {
    persistUserAndMockLoggedIn(currentUser);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_WORKOUT_LOG_URL, 1L),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  //endregion

  //region add exercise logs to workout log

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDtos.json", type = ExerciseLogCreateDto[].class, arrayToList = true),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogDtoFromCreateDto.json", type = ExerciseLogDto.class),
  })
  void addExerciseLogs_validRequest_return201AndWorkoutLogWithNewExerciseLog(User currentUser, WorkoutLog persistedWorkoutLog,
                                                                             Exercise referencedExercise, List<ExerciseLogCreateDto> createDtos,
                                                                             ExerciseLogDto createdExerciseLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_EXERCISE_LOG_URL, persistedWorkoutLog.getId()),
        HttpMethod.POST,
        createDtos,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0)).isEqualTo(createdExerciseLog);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser2.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDtos.json", type = ExerciseLogCreateDto[].class, arrayToList = true),
  })
  void addExerciseLogs_nonOwnerRegularRequest_return400(User currentUser, User workoutOwner, WorkoutLog persistedWorkoutLog,
                                                        Exercise referencedExercise, List<ExerciseLogCreateDto> createDtos) {
    persistUsers(workoutOwner); // must be persisted before currentUser because otherwise ID in persistedWorkoutLog references wrong User
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_EXERCISE_LOG_URL, persistedWorkoutLog.getId()),
        HttpMethod.POST,
        createDtos,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDtos.json", type = ExerciseLogCreateDto[].class, arrayToList = true),
  })
  void addExerciseLogs_workoutLogDoesNotExist_return404(User currentUser, List<ExerciseLogCreateDto> createDtos) {
    persistUserAndMockLoggedIn(currentUser);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_EXERCISE_LOG_URL, 1L),
        HttpMethod.POST,
        createDtos,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDtos.json", type = ExerciseLogCreateDto[].class, arrayToList = true)
  })
  void addExerciseLogs_exerciseDoesNotExist_return404(User currentUser, WorkoutLog persistedWorkoutLog, List<ExerciseLogCreateDto> createDtos) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_EXERCISE_LOG_URL, persistedWorkoutLog.getId()),
        HttpMethod.POST,
        createDtos,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  //endregion

  //region update exercise log order

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UpdateExerciseLogPositionsInputWithoutGaps.json", type = UpdateExerciseLogPositionsTestSpecification.class)
  })
  void updateExerciseLogPositions_gaplessSpecification_return200AndUpdatedExerciseLogPositions(
      UpdateExerciseLogPositionsTestSpecification specification) {
    updateExerciseLogPositions_testDelegateSuccess(specification);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UpdateExerciseLogPositionsInputWithGaps.json", type = UpdateExerciseLogPositionsTestSpecification.class)
  })
  void updateExerciseLogPositions_specificationWithGaps_return200AndUpdatedExerciseLogPositionsWithoutGaps(
      UpdateExerciseLogPositionsTestSpecification specification) {
    updateExerciseLogPositions_testDelegateSuccess(specification);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "IncompleteUpdateExerciseLogPositionsInput.json", type = UpdateExerciseLogPositionsTestSpecification.class)
  })
  void updateExerciseLogPositions_incompleteSpecification_return400(UpdateExerciseLogPositionsTestSpecification specification) {
    updateExerciseLogPositions_testDelegateFailure(specification);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "OverdefinedUpdateExerciseLogPositionsInput.json", type = UpdateExerciseLogPositionsTestSpecification.class)
  })
  void updateExerciseLogPositions_overdefinedSpecification_return400(UpdateExerciseLogPositionsTestSpecification specification) {
    updateExerciseLogPositions_testDelegateFailure(specification);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "AmbiguousUpdateExerciseLogPositionsInput.json", type = UpdateExerciseLogPositionsTestSpecification.class)
  })
  void updateExerciseLogPositions_ambiguousSpecification_return400(UpdateExerciseLogPositionsTestSpecification specification) {
    updateExerciseLogPositions_testDelegateFailure(specification);
  }

  private void updateExerciseLogPositions_testDelegateSuccess(UpdateExerciseLogPositionsTestSpecification specification) {
    persistUserAndMockLoggedIn(specification.currentUser);
    persistEntities(exerciseRepository, specification.referencedExercise);
    persistEntities(workoutLogRepository, specification.workoutLogWithExerciseLogs.toBuilder().exerciseLogs(new ArrayList<>()).build());
    persistEntities(exerciseLogRepository, specification.workoutLogWithExerciseLogs.getExerciseLogs());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(UPDATE_EXERCISE_LOG_POSITIONS_URL, specification.workoutLogWithExerciseLogs.getId()),
        HttpMethod.PUT,
        specification.updateSpecification,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).allSatisfy(exerciseLog ->
        assertThat(exerciseLog.getPosition()).isEqualTo(specification.expectedPositions.get(exerciseLog.getId())));
  }

  private void updateExerciseLogPositions_testDelegateFailure(UpdateExerciseLogPositionsTestSpecification specification) {
    persistUserAndMockLoggedIn(specification.currentUser);
    persistEntities(exerciseRepository, specification.referencedExercise);
    persistEntities(workoutLogRepository, specification.workoutLogWithExerciseLogs.toBuilder().exerciseLogs(new ArrayList<>()).build());
    persistEntities(exerciseLogRepository, specification.workoutLogWithExerciseLogs.getExerciseLogs());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(UPDATE_EXERCISE_LOG_POSITIONS_URL, specification.workoutLogWithExerciseLogs.getId()),
        HttpMethod.PUT,
        specification.updateSpecification,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  //endregion

  //region delete exercise log from workout log

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneExerciseLog.json", type = WorkoutLog.class)
  })
  void deleteExerciseLog_validRequest_return200AndModifiedWorkoutLog(User currentUser, Exercise referencedExercise, WorkoutLog persistedWorkoutLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog.toBuilder().exerciseLogs(new ArrayList<>()).build());
    persistEntities(exerciseLogRepository, persistedWorkoutLog.getExerciseLogs());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_EXERCISE_LOG_URL, persistedWorkoutLog.getId(), persistedWorkoutLog.getExerciseLogs().get(0).getId()),
        HttpMethod.DELETE,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).isEmpty();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
  })
  void deleteExerciseLog_workoutLogDoesNotExist_return404(User currentUser) {
    persistUserAndMockLoggedIn(currentUser);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_EXERCISE_LOG_URL, 1L, 1L),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneExerciseLog.json", type = WorkoutLog.class)
  })
  void deleteExerciseLog_exerciseLogDoesNotExist_return404(User currentUser, Exercise referencedExercise, WorkoutLog persistedWorkoutLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog.toBuilder().exerciseLogs(new ArrayList<>()).build());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_EXERCISE_LOG_URL, persistedWorkoutLog.getId(), 1L),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  //endregion

  //region set exercise log comment

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneExerciseLog.json", type = WorkoutLog.class)
  })
  void setExerciseLogComment_validRequest_return200AndModifiedWorkoutLog(User currentUser, Exercise referencedExercise,
                                                                         WorkoutLog persistedWorkoutLog) {
    persistUserAndMockLoggedIn(currentUser);
    final var newComment = "log comment";
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog.toBuilder().exerciseLogs(new ArrayList<>()).build());
    persistEntities(exerciseLogRepository, persistedWorkoutLog.getExerciseLogs());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_EXERCISE_LOG_URL, persistedWorkoutLog.getId(), persistedWorkoutLog.getExerciseLogs().get(0).getId()),
        HttpMethod.PATCH,
        newComment,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getComment()).isEqualTo(newComment);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneExerciseLog.json", type = WorkoutLog.class)
  })
  void setExerciseLogComment_nonExistingExerciseLog_return404(User currentUser, Exercise referencedExercise, WorkoutLog persistedWorkoutLog) {
    persistUserAndMockLoggedIn(currentUser);
    var newComment = "log comment";
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog.toBuilder().exerciseLogs(new ArrayList<>()).build());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_EXERCISE_LOG_URL, persistedWorkoutLog.getId(), persistedWorkoutLog.getExerciseLogs().get(0).getId()),
        HttpMethod.PATCH,
        newComment,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  //endregion

  //region add set log to exericse log

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneExerciseLog.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "RepsSetLogCreateDto.json", type = RepsSetLogCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "RepsSetLogFromCreateDto.json", type = RepsSetLogDto.class)
  })
  void addSetLog_validRepsRequest_return201AndModifiedWorkoutLog(User currentUser, Exercise referencedExercise, WorkoutLog persistedWorkoutLog,
                                                                 SetLogCreateDto setLogToCreate, RepsSetLogDto createdSetLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog.toBuilder().exerciseLogs(new ArrayList<>()).build());
    persistEntities(exerciseLogRepository, persistedWorkoutLog.getExerciseLogs());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_SET_LOG_URL, persistedWorkoutLog.getId(), persistedWorkoutLog.getExerciseLogs().get(0).getId()),
        HttpMethod.POST,
        setLogToCreate,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs().get(0)).isInstanceOf(RepsSetLogDto.class);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs().get(0)).isEqualTo(createdSetLog);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneExerciseLog.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "TimeSetLogCreateDto.json", type = TimeSetLogCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "TimeSetLogFromCreateDto.json", type = TimeSetLogDto.class)
  })
  void addSetLog_validTimeRequest_return201AndModifiedWorkoutLog(User currentUser, Exercise referencedExercise, WorkoutLog persistedWorkoutLog,
                                                                 TimeSetLogCreateDto setLogToCreate, TimeSetLogDto createdSetLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog.toBuilder().exerciseLogs(new ArrayList<>()).build());
    persistEntities(exerciseLogRepository, persistedWorkoutLog.getExerciseLogs());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_SET_LOG_URL, persistedWorkoutLog.getId(), persistedWorkoutLog.getExerciseLogs().get(0).getId()),
        HttpMethod.POST,
        setLogToCreate,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs().get(0)).isInstanceOf(TimeSetLogDto.class);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs().get(0)).isEqualTo(createdSetLog);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneExerciseLog.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "InvalidTimeSetLogCreateDto.json", type = TimeSetLogCreateDto.class)
  })
  void addSetLog_invalidTimeRequest_return400(User currentUser, Exercise referencedExercise, WorkoutLog persistedWorkoutLog,
                                              TimeSetLogCreateDto setLogToCreate) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog.toBuilder().exerciseLogs(new ArrayList<>()).build());
    persistEntities(exerciseLogRepository, persistedWorkoutLog.getExerciseLogs());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_SET_LOG_URL, persistedWorkoutLog.getId(), persistedWorkoutLog.getExerciseLogs().get(0).getId()),
        HttpMethod.POST,
        setLogToCreate,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  //endregion

  //region update set log in exercise log

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneSetLog.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "UpdateSetLogDto.json", type = SetLogDto.class)
  })
  void updateSetLog_changeLoggingType_return201AndModifiedWorkoutLog(User currentUser, Exercise referencedExercise, WorkoutLog workoutLogWithSetLogs,
                                                                     SetLogDto updateSetLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, workoutLogWithSetLogs.toBuilder().exerciseLogs(new ArrayList<>()).build());
    workoutLogWithSetLogs.getExerciseLogs()
        .forEach(log -> persistEntities(exerciseLogRepository, log.toBuilder().setLogs(new ArrayList<>()).build()));
    workoutLogWithSetLogs.getExerciseLogs().forEach(log -> persistEntities(setLogRepository, log.getSetLogs()));

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(UPDATE_SET_LOG_URL, workoutLogWithSetLogs.getId(), workoutLogWithSetLogs.getExerciseLogs().get(0).getSetLogs().get(0).getId()),
        HttpMethod.PUT,
        updateSetLog,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs().get(0))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(updateSetLog);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneSetLog.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "UpdateSetLogDto2.json", type = SetLogDto.class)
  })
  void updateSetLog_changeProperties_return201AndModifiedWorkoutLog(User currentUser, Exercise referencedExercise, WorkoutLog workoutLogWithSetLogs,
                                                                    SetLogDto updateSetLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, workoutLogWithSetLogs.toBuilder().exerciseLogs(new ArrayList<>()).build());
    workoutLogWithSetLogs.getExerciseLogs()
        .forEach(log -> persistEntities(exerciseLogRepository, log.toBuilder().setLogs(new ArrayList<>()).build()));
    workoutLogWithSetLogs.getExerciseLogs().forEach(log -> persistEntities(setLogRepository, log.getSetLogs()));

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(UPDATE_SET_LOG_URL, workoutLogWithSetLogs.getId(), workoutLogWithSetLogs.getExerciseLogs().get(0).getSetLogs().get(0).getId()),
        HttpMethod.PUT,
        updateSetLog,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs().get(0))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(updateSetLog);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise2.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneSetLog.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "UpdateSetLogDto2.json", type = SetLogDto.class)
  })
  void updateSetLog_changeToInvalidLoggingType_return400(User currentUser, Exercise referencedExercise, WorkoutLog workoutLogWithSetLogs,
                                                         SetLogDto updateSetLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, workoutLogWithSetLogs.toBuilder().exerciseLogs(new ArrayList<>()).build());
    workoutLogWithSetLogs.getExerciseLogs()
        .forEach(log -> persistEntities(exerciseLogRepository, log.toBuilder().setLogs(new ArrayList<>()).build()));
    workoutLogWithSetLogs.getExerciseLogs().forEach(log -> persistEntities(setLogRepository, log.getSetLogs()));

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(UPDATE_SET_LOG_URL, workoutLogWithSetLogs.getId(), workoutLogWithSetLogs.getExerciseLogs().get(0).getSetLogs().get(0).getId()),
        HttpMethod.PUT,
        updateSetLog,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise2.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneSetLog.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "UpdateSetLogDto3.json", type = SetLogDto.class)
  })
  void updateSetLog_changePosition_return400(User currentUser, Exercise referencedExercise, WorkoutLog workoutLogWithSetLogs,
                                             SetLogDto updateSetLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, workoutLogWithSetLogs.toBuilder().exerciseLogs(new ArrayList<>()).build());
    workoutLogWithSetLogs.getExerciseLogs()
        .forEach(log -> persistEntities(exerciseLogRepository, log.toBuilder().setLogs(new ArrayList<>()).build()));
    workoutLogWithSetLogs.getExerciseLogs().forEach(log -> persistEntities(setLogRepository, log.getSetLogs()));

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(UPDATE_SET_LOG_URL, workoutLogWithSetLogs.getId(), workoutLogWithSetLogs.getExerciseLogs().get(0).getSetLogs().get(0).getId()),
        HttpMethod.PUT,
        updateSetLog,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  //endregion

  //region update set log order in exercise log

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UpdateSetLogPositionsInputWithoutGaps.json", type = UpdateSetLogPositionsTestSpecification.class)
  })
  void updateSetLogPositions_gaplessSpecification_return200AndUpdatedExerciseLogPositions(
      UpdateSetLogPositionsTestSpecification specification) {
    updateSetLogPositions_testDelegateSuccess(specification, specification.workoutLogWithSetLogs.getExerciseLogs().get(0).getId(), 0);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UpdateSetLogPositionsInputWithGaps.json", type = UpdateSetLogPositionsTestSpecification.class)
  })
  void updateSetLogPositions_specificationWithGaps_return200AndUpdatedExerciseLogPositionsWithoutGaps(
      UpdateSetLogPositionsTestSpecification specification) {
    updateSetLogPositions_testDelegateSuccess(specification, specification.workoutLogWithSetLogs.getExerciseLogs().get(0).getId(), 0);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "IncompleteUpdateSetLogPositionsInput.json", type = UpdateSetLogPositionsTestSpecification.class)
  })
  void updateSetLogPositions_incompleteSpecification_return400(UpdateSetLogPositionsTestSpecification specification) {
    updateSetLogPositions_testDelegateFailure(specification, specification.workoutLogWithSetLogs.getExerciseLogs().get(0).getId());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "OverdefinedUpdateSetLogPositionsInput.json", type = UpdateSetLogPositionsTestSpecification.class)
  })
  void updateSetLogPositions_overdefinedSpecification_return400(UpdateSetLogPositionsTestSpecification specification) {
    updateSetLogPositions_testDelegateFailure(specification, specification.workoutLogWithSetLogs.getExerciseLogs().get(0).getId());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "AmbiguousUpdateSetLogPositionsInput.json", type = UpdateSetLogPositionsTestSpecification.class)
  })
  void updateSetLogPositions_ambiguousSpecification_return400(UpdateSetLogPositionsTestSpecification specification) {
    updateSetLogPositions_testDelegateFailure(specification, specification.workoutLogWithSetLogs.getExerciseLogs().get(0).getId());
  }

  private void updateSetLogPositions_testDelegateSuccess(UpdateSetLogPositionsTestSpecification specification, long exerciseLogId,
                                                         int exerciseLogIndex) {
    persistUserAndMockLoggedIn(specification.currentUser);
    persistEntities(exerciseRepository, specification.referencedExercise);
    persistEntities(workoutLogRepository, specification.workoutLogWithSetLogs.toBuilder().exerciseLogs(new ArrayList<>()).build());
    specification.workoutLogWithSetLogs.getExerciseLogs()
        .forEach(log -> persistEntities(exerciseLogRepository, log.toBuilder().setLogs(new ArrayList<>()).build()));
    specification.workoutLogWithSetLogs.getExerciseLogs().forEach(log -> persistEntities(setLogRepository, log.getSetLogs()));

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(UPDATE_SET_LOG_POSITIONS_URL, specification.workoutLogWithSetLogs.getId(), exerciseLogId),
        HttpMethod.PUT,
        specification.updateSpecification,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).isNotNull();
    assertThat(response.getBody().getExerciseLogs().get(exerciseLogIndex).getSetLogs()).allSatisfy(exerciseLog ->
        assertThat(exerciseLog.getPosition()).isEqualTo(specification.expectedPositions.get(exerciseLog.getId())));
  }

  private void updateSetLogPositions_testDelegateFailure(UpdateSetLogPositionsTestSpecification specification, long exerciseLogId) {
    persistUserAndMockLoggedIn(specification.currentUser);
    persistEntities(exerciseRepository, specification.referencedExercise);
    persistEntities(workoutLogRepository, specification.workoutLogWithSetLogs.toBuilder().exerciseLogs(new ArrayList<>()).build());
    specification.workoutLogWithSetLogs.getExerciseLogs()
        .forEach(log -> persistEntities(exerciseLogRepository, log.toBuilder().setLogs(new ArrayList<>()).build()));
    specification.workoutLogWithSetLogs.getExerciseLogs().forEach(log -> persistEntities(setLogRepository, log.getSetLogs()));

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(UPDATE_SET_LOG_POSITIONS_URL, specification.workoutLogWithSetLogs.getId(), exerciseLogId),
        HttpMethod.PUT,
        specification.updateSpecification,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  //endregion

  //region delete set log from exercise log

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneSetLog.json", type = WorkoutLog.class)
  })
  void deleteSetLog_validRequest_return200AndModifiedWorkoutLog(User currentUser, Exercise referencedExercise, WorkoutLog persistedWorkoutLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog.toBuilder().exerciseLogs(new ArrayList<>()).build());
    persistedWorkoutLog.getExerciseLogs()
        .forEach(log -> persistEntities(exerciseLogRepository, log.toBuilder().setLogs(new ArrayList<>()).build()));
    persistedWorkoutLog.getExerciseLogs().forEach(log -> persistEntities(setLogRepository, log.getSetLogs()));

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_SET_LOG_URL,
            persistedWorkoutLog.getId(),
            persistedWorkoutLog.getExerciseLogs().get(0).getId(),
            persistedWorkoutLog.getExerciseLogs().get(0).getSetLogs().get(0).getId()),
        HttpMethod.DELETE,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getExerciseLogs()).hasSize(1);
    assertThat(response.getBody().getExerciseLogs().get(0).getSetLogs()).isEmpty();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithOneExerciseLog.json", type = WorkoutLog.class)
  })
  void deleteSetLog_setLogDoesNotExist_return400(User currentUser, Exercise referencedExercise, WorkoutLog persistedWorkoutLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog.toBuilder().exerciseLogs(new ArrayList<>()).build());
    persistEntities(exerciseLogRepository, persistedWorkoutLog.getExerciseLogs());

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_SET_LOG_URL,
            persistedWorkoutLog.getId(),
            persistedWorkoutLog.getExerciseLogs().get(0).getId(),
            1L),
        HttpMethod.DELETE,
        WorkoutLogDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  //endregion

  @Data
  @NoArgsConstructor
  static class GetByDayTestSpecification {
    private User currentUser;
    private User[] persistedUsers;
    private String searchDate;
    private WorkoutLog[] persistedWorkoutLogs;
    private WorkoutLogDto[] expectedWorkoutLogs;
  }

  @Data
  @NoArgsConstructor
  static class GetByPeriodTestSpecification {
    private User currentUser;
    private User[] persistedUsers;
    private String startDate;
    private String endDate;
    private Exercise[] persistedExercises;
    private WorkoutLog[] persistedWorkoutLogs;
    private Map<ZonedDateTime, Integer> expectedMapping;
  }

  @Data
  @NoArgsConstructor
  static class UpdateExerciseLogPositionsTestSpecification {
    private User currentUser;
    private Exercise referencedExercise;
    private WorkoutLog workoutLogWithExerciseLogs;
    private Map<Long, Integer> updateSpecification;
    private Map<Long, Integer> expectedPositions;
  }

  @Data
  @NoArgsConstructor
  static class UpdateSetLogPositionsTestSpecification {
    private User currentUser;
    private Exercise referencedExercise;
    private WorkoutLog workoutLogWithSetLogs;
    private Map<Long, Integer> updateSpecification;
    private Map<Long, Integer> expectedPositions;
  }
}
