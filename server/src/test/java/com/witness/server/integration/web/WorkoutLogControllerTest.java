package com.witness.server.integration.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.workout.ExerciseLogCreateDto;
import com.witness.server.dto.workout.ExerciseLogDto;
import com.witness.server.dto.workout.RepsSetLogCreateDto;
import com.witness.server.dto.workout.RepsSetLogDto;
import com.witness.server.dto.workout.SetLogCreateDto;
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
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class WorkoutLogControllerTest extends BaseControllerIntegrationTest {
  private static final String DATA_ROOT = "data/integration/web/workout-log-controller-test/";

  private static final String SET_WORKOUT_DURATION_URL = "/%s";
  private static final String DELETE_WORKOUT_LOG_URL = "/%s";
  private static final String ADD_EXERCISE_LOG_URL = "/%s";
  private static final String UPDATE_EXERCISE_LOG_POSITIONS_URL = "%s/positions";
  private static final String DELETE_EXERCISE_LOG_URL = "%s/%s";
  private static final String ADD_SET_LOG_URL = "%s/%s";
  private static final String UPDATE_SET_LOG_URL = "%s/%s";
  private static final String UPDATE_SET_LOG_POSITIONS_URL = "%s/%s/positions";
  private static final String DELETE_SET_LOG_URL = "%s/%s/%s";

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
    return "workout";
  }

  //region /

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "GetWorkoutLogsInput1.json", type = GetByDayTestSpecification.class)
  })
  void getWorkoutLogs_matchingPersistedWorkoutLogs_return200AndWorkoutLogs(GetByDayTestSpecification specification) {
    persistUserAndMockLoggedIn(specification.currentUser);
    persistUsers(specification.persistedUsers);
    persistEntities(workoutLogRepository, specification.persistedWorkoutLogs);

    var queryParams = toMultiValueMap(Map.of("date", "2021-10-08T14:15:55.3007597+02:00"));
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

    var queryParams = toMultiValueMap(Map.of("date", "2021-10-08T14:15:55.3007597+02:00"));
    var response = get(TestAuthentication.REGULAR, requestUrl(), queryParams, WorkoutLogDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEmpty();
  }

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
        .ignoringFields("loggedOn")
        .isEqualTo(createdDto);
    assertThat(response.getBody().getLoggedOn()).isNotNull();
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

  //region /{workoutLogId}

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

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDto.json", type = ExerciseLogCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogDtoFromCreateDto.json", type = ExerciseLogDto.class),
  })
  void addExerciseLog_validRequest_return201AndWorkoutLogWithNewExerciseLog(User currentUser, WorkoutLog persistedWorkoutLog,
                                                                            Exercise referencedExercise, ExerciseLogCreateDto createDto,
                                                                            ExerciseLogDto createdExerciseLog) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_EXERCISE_LOG_URL, persistedWorkoutLog.getId()),
        HttpMethod.POST,
        createDto,
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
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDto.json", type = ExerciseLogCreateDto.class),
  })
  void addExerciseLog_nonOwnerRegularRequest_return400(User currentUser, User workoutOwner, WorkoutLog persistedWorkoutLog,
                                                       Exercise referencedExercise, ExerciseLogCreateDto createDto) {
    persistUsers(workoutOwner); // must be persisted before currentUser because otherwise ID in persistedWorkoutLog references wrong User
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, referencedExercise);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_EXERCISE_LOG_URL, persistedWorkoutLog.getId()),
        HttpMethod.POST,
        createDto,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDto.json", type = ExerciseLogCreateDto.class),
  })
  void addExerciseLog_workoutLogDoesNotExist_return404(User currentUser, ExerciseLogCreateDto createDto) {
    persistUserAndMockLoggedIn(currentUser);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_EXERCISE_LOG_URL, 1L),
        HttpMethod.POST,
        createDto,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogWithoutExerciseLogs.json", type = WorkoutLog.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDto.json", type = ExerciseLogCreateDto.class)
  })
  void addExerciseLog_exerciseDoesNotExist_return404(User currentUser, WorkoutLog persistedWorkoutLog, ExerciseLogCreateDto createDto) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(workoutLogRepository, persistedWorkoutLog);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(ADD_EXERCISE_LOG_URL, persistedWorkoutLog.getId()),
        HttpMethod.POST,
        createDto,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  //endregion

  //region /{workoutLogId}/positions

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

  //region /{workoutLogId}/{exerciseLogId}

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

  // TODO UPDATE SET LOG

  //endregion

  //region /{workoutLogId}/{exerciseLogId}/positions

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
