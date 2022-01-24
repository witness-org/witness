package com.witness.server.integration.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.exercise.ExerciseCreateDto;
import com.witness.server.dto.exercise.ExerciseDto;
import com.witness.server.dto.exercise.UserExerciseDto;
import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.exercise.UserExercise;
import com.witness.server.entity.user.User;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.UserExerciseRepository;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class ExerciseControllerTest extends BaseControllerIntegrationTest {
  private static final String DATA_ROOT = "data/integration/web/exercise-controller-test/";

  private static final String CREATE_INITIAL_EXERCISE_URL = "initial-exercises";
  private static final String DELETE_INITIAL_EXERCISE_URL = "initial-exercises/%s";
  private static final String CREATE_USER_EXERCISE_URL = "user-exercises";
  private static final String DELETE_USER_EXERCISE_URL = "user-exercises/%s";
  private static final String UPDATE_INITIAL_EXERCISE_URL = "initial-exercises";
  private static final String UPDATE_USER_EXERCISE_URL = "user-exercises";
  private static final String GET_ALL_FOR_USER_BY_MUSCLE_GROUP_URL = "";
  private static final String GET_ALL_CREATED_BY_USER_URL = "user-exercises";

  @Autowired
  private ExerciseRepository exerciseRepository;

  @Autowired
  private UserExerciseRepository userExerciseRepository;

  @Override
  String getEndpointUrl() {
    return "exercises";
  }

  //region new initial exercise

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDto1.json", type = ExerciseDto.class)
  })
  void createInitialExercise_validExerciseDtoAsAdmin_return201AndCorrectDto(ExerciseCreateDto createDto, ExerciseDto exerciseDto) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(exerciseDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_validExerciseDtoAsPremium_return403(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.PREMIUM, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_validExerciseDtoAsRegular_return403(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_validExerciseDtoNoAuthorization_return401(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.NONE, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullName.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoNullNameAsAdmin_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyName.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoEmptyNameAsAdmin_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoLongName.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoLongNameAsAdmin_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoLongDescription.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoLongDescriptionAsAdmin_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullMuscleGroups.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoNullMuscleGroupsAsAdmin_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullLoggingTypes.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoNullLoggingTypesAsAdmin_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyMuscleGroups.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoEmptyMuscleGroupsAsAdmin_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyLoggingTypes.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoEmptyLoggingTypesAsAdmin_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_validExerciseDtoTakenNameAsAdmin_return400(Exercise persistedExercise, ExerciseCreateDto createDto) {
    persistEntities(exerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_INITIAL_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "AdminUser.json", type = User.class)
  })
  void deleteInitialExercise_existingExerciseAsAdmin_return204(Exercise persistedExercise, User currentUser) {
    persistUserAndMockLoggedIn(currentUser);
    persistEntities(exerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(DELETE_INITIAL_EXERCISE_URL, persistedExercise.getId()),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void deleteInitialExercise_existingExerciseAsPremium_return403() {
    var response = exchange(TestAuthentication.PREMIUM,
        requestUrl(DELETE_INITIAL_EXERCISE_URL, 1L),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void deleteInitialExercise_nonExistingAsAdmin_return404() {
    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(DELETE_INITIAL_EXERCISE_URL, 1L),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class)
  })
  void deleteUserExercise_existingExerciseAsRegular_return204(UserExercise persistedExercise, User currentUser) {
    persistUserAndMockLoggedIn(currentUser);
    persistExercisesForLoggedInUser(persistedExercise);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_USER_EXERCISE_URL, persistedExercise.getId()),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "PremiumUser2.json", type = User.class)
  })
  void deleteUserExercise_existingExerciseByOtherUserAsRegular_return400(UserExercise persistedExercise, User currentUser, User creator) {
    persistUserAndMockLoggedIn(currentUser);
    persistUsers(creator);
    persistedExercise.setCreatedBy(creator);
    persistEntities(userExerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_USER_EXERCISE_URL, persistedExercise.getId()),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "AdminUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "PremiumUser2.json", type = User.class)
  })
  void deleteUserExercise_existingExerciseByOtherUserAsAdmin_return204(UserExercise persistedExercise, User currentUser, User creator) {
    persistUserAndMockLoggedIn(currentUser);
    persistUsers(creator);
    persistedExercise.setCreatedBy(creator);
    persistEntities(userExerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(DELETE_USER_EXERCISE_URL, persistedExercise.getId()),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
  })
  void deleteUserExercise_nonExistingAsRegular_return404(User currentUser) {
    persistUserAndMockLoggedIn(currentUser);

    var response = exchange(TestAuthentication.REGULAR,
        requestUrl(DELETE_USER_EXERCISE_URL, 1L),
        HttpMethod.DELETE,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  //endregion

  //region new user exercise

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "AdminUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseDto1Admin.json", type = UserExerciseDto.class)
  })
  void createUserExercise_validExerciseDtoAsAdmin_return201AndCorrectDto(User user, ExerciseCreateDto createDto, UserExerciseDto exerciseDto) {
    persistUserAndMockLoggedIn(user);

    var response = exchange(TestAuthentication.ADMIN, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(exerciseDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "PremiumUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseDto1Premium.json", type = UserExerciseDto.class)
  })
  void createUserExercise_validExerciseDtoAsPremium_return201AndCorrectDto(User user, ExerciseCreateDto createDto, UserExerciseDto exerciseDto) {
    persistUserAndMockLoggedIn(user);

    var response = exchange(TestAuthentication.PREMIUM, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(exerciseDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseDto1Regular.json", type = UserExerciseDto.class)
  })
  void createUserExercise_validExerciseDtoAsRegular_return201AndCorrectDto(User user, ExerciseCreateDto createDto, UserExerciseDto exerciseDto) {
    persistUserAndMockLoggedIn(user);

    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(exerciseDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_validExerciseDtoNoAuthorization_return401(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.NONE, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullName.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoNullNameAsRegular_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyName.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoEmptyNameAsRegular_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoLongName.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoLongNameAsRegular_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoLongDescription.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoLongDescriptionAsRegular_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullMuscleGroups.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoNullMuscleGroupsAsRegular_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullLoggingTypes.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoNullLoggingTypesAsRegular_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyMuscleGroups.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoEmptyMuscleGroupsAsRegular_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyLoggingTypes.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoEmptyLoggingTypesAsRegular_return400(ExerciseCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_validExerciseDtoTakenNameInitialAsRegular_return400(User user, Exercise persistedExercise, ExerciseCreateDto createDto) {
    persistUserAndMockLoggedIn(user);
    persistEntities(exerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseCreateDto1.json", type = ExerciseCreateDto.class),
  })
  void createUserExercise_validExerciseDtoTakenNameUserAsRegular_return400(User user, UserExercise persistedUserExercise,
                                                                           ExerciseCreateDto createDto) {
    persistUserAndMockLoggedIn(user);
    persistExercisesForLoggedInUser(persistedUserExercise);

    var response = exchange(TestAuthentication.REGULAR, requestUrl(CREATE_USER_EXERCISE_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  //endregion

  //region all by muscle group

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class)
  })
  void getAllForUserByMuscleGroup_nonePersistedAsRegular_return200AndEmptyList(User user) {
    persistUserAndMockLoggedIn(user);

    var params = toMultiValueMap(Map.of("muscle-group", MuscleGroup.CHEST.toString()));
    var response = get(TestAuthentication.REGULAR, requestUrl(GET_ALL_FOR_USER_BY_MUSCLE_GROUP_URL), params, ExerciseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise2.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDtos_1-2.json", type = ExerciseDto[].class)
  })
  void getAllForUserByMuscleGroup_oneInitialOneCreatedPersistedAsRegular_return200AndEmptyList(User user, Exercise persistedInitialExercise,
                                                                                               UserExercise persistedUserExercise,
                                                                                               ExerciseDto[] expected) {
    persistUserAndMockLoggedIn(user);
    persistEntities(exerciseRepository, persistedInitialExercise);
    persistExercisesForLoggedInUser(persistedUserExercise);

    var params = toMultiValueMap(Map.of("muscle-group", MuscleGroup.CHEST.toString()));
    var response = get(TestAuthentication.REGULAR, requestUrl(GET_ALL_FOR_USER_BY_MUSCLE_GROUP_URL), params, ExerciseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .usingElementComparatorIgnoringFields("id")
        .containsExactlyInAnyOrder(expected);
  }

  //endregion

  //region all created by user
  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class)
  })
  void getAllCreatedByUser_noExercisesPersistedAsRegular_return200AndEmptyList(User user) {
    persistUserAndMockLoggedIn(user);

    var response = get(TestAuthentication.REGULAR, requestUrl(GET_ALL_CREATED_BY_USER_URL), ExerciseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise2.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDtos_2.json", type = ExerciseDto[].class)
  })
  void getAllCreatedByUser_oneInitialOneCreatedPersistedAsRegular_return200AndEmptyList(User user, Exercise persistedInitialExercise,
                                                                                        UserExercise persistedUserExercise,
                                                                                        ExerciseDto[] expected) {
    persistUserAndMockLoggedIn(user);
    persistEntities(exerciseRepository, persistedInitialExercise);
    persistExercisesForLoggedInUser(persistedUserExercise);

    var response = get(TestAuthentication.REGULAR, requestUrl(GET_ALL_CREATED_BY_USER_URL), ExerciseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .usingElementComparatorIgnoringFields("id")
        .containsExactlyInAnyOrder(expected);
  }

  //endregion

  //region update initial exercise

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseDto1_updatedNewName.json", type = ExerciseDto.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDto1_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateInitialExercise_validExerciseDtoAsAdmin_return200AndCorrectDto(ExerciseDto request, Exercise persistedExercise, ExerciseDto expected) {
    persistEntities(exerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.ADMIN, requestUrl(UPDATE_INITIAL_EXERCISE_URL), HttpMethod.PUT, request, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseDto1_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateInitialExercise_validExerciseDtoAsPremium_return403(ExerciseDto request) {
    var response = exchange(TestAuthentication.PREMIUM, requestUrl(UPDATE_INITIAL_EXERCISE_URL), HttpMethod.PUT, request, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseDto1_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateInitialExercise_validExerciseDtoAsRegular_return403(ExerciseDto request) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(UPDATE_INITIAL_EXERCISE_URL), HttpMethod.PUT, request, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseDto1_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateInitialExercise_nonExistentExerciseDtoAsAdmin_return404(ExerciseDto request) {
    var response = exchange(TestAuthentication.ADMIN, requestUrl(UPDATE_INITIAL_EXERCISE_URL), HttpMethod.PUT, request, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercises_1-2.json", type = Exercise[].class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDto1_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateInitialExercise_existingNameExerciseDtoAsAdmin_return400(Exercise[] persistedExercises, ExerciseDto request) {
    persistEntities(exerciseRepository, persistedExercises);

    var response = exchange(TestAuthentication.ADMIN, requestUrl(UPDATE_INITIAL_EXERCISE_URL), HttpMethod.PUT, request, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  //endregion

  //region update user exercise

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "AdminUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateUserExercise_validExerciseDtoAsAdmin_return200AndCorrectDto(User user, ExerciseDto request, UserExercise persistedExercise,
                                                                         ExerciseDto expected) {
    persistUserAndMockLoggedIn(user);
    persistExercisesForLoggedInUser(persistedExercise);

    var response = exchange(TestAuthentication.ADMIN, requestUrl(UPDATE_USER_EXERCISE_URL), HttpMethod.PUT, request, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "PremiumUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateUserExercise_validExerciseDtoAsPremium_return200AndCorrectDto(User user, ExerciseDto request, UserExercise persistedExercise,
                                                                           ExerciseDto expected) {
    persistUserAndMockLoggedIn(user);
    persistExercisesForLoggedInUser(persistedExercise);

    var response = exchange(TestAuthentication.ADMIN, requestUrl(UPDATE_USER_EXERCISE_URL), HttpMethod.PUT, request, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateUserExercise_validExerciseDtoAsRegular_return200AndCorrectDto(User user, ExerciseDto request, UserExercise persistedExercise,
                                                                           ExerciseDto expected) {
    persistUserAndMockLoggedIn(user);
    persistExercisesForLoggedInUser(persistedExercise);

    var response = exchange(TestAuthentication.ADMIN, requestUrl(UPDATE_USER_EXERCISE_URL), HttpMethod.PUT, request, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateUserExercise_nonExistentExerciseDtoAsRegular_return404(User user, ExerciseDto request) {
    persistUserAndMockLoggedIn(user);
    var response = exchange(TestAuthentication.REGULAR, requestUrl(UPDATE_USER_EXERCISE_URL), HttpMethod.PUT, request, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class),
      @JsonFileSource(value = DATA_ROOT + "RegularUser2.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Regular2.json", type = UserExercise.class)
  })
  void updateUserExercise_exerciseDtoWrongUserAsRegular_return404(User requester, ExerciseDto request, User persistedUser,
                                                                  UserExercise persistedExercise) {
    persistUserAndMockLoggedIn(requester);
    persistUsers(persistedUser);
    persistEntities(userExerciseRepository, persistedExercise);
    var response = exchange(TestAuthentication.REGULAR, requestUrl(UPDATE_USER_EXERCISE_URL), HttpMethod.PUT, request, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateUserExercise_existingInitialNameExerciseDtoAsRegular_return400(User user, Exercise persistedExercise, UserExercise persistedUserExercise,
                                                                            ExerciseDto request) {
    persistUserAndMockLoggedIn(user);
    persistExercisesForLoggedInUser(persistedUserExercise);
    persistEntities(exerciseRepository, persistedExercise);

    var response = exchange(TestAuthentication.REGULAR, requestUrl(UPDATE_USER_EXERCISE_URL), HttpMethod.PUT, request, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "RegularUser.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercises_1-2.json", type = UserExercise[].class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1Dto_updatedNewName.json", type = ExerciseDto.class)
  })
  void updateUserExercise_existingUserNameExerciseDtoAsRegular_return400(User user, UserExercise[] persistedUserExercises,
                                                                         ExerciseDto request) {
    persistUserAndMockLoggedIn(user);
    persistExercisesForLoggedInUser(persistedUserExercises);

    var response = exchange(TestAuthentication.REGULAR, requestUrl(UPDATE_USER_EXERCISE_URL), HttpMethod.PUT, request, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  //endregion

  private void persistExercisesForLoggedInUser(UserExercise... exercises) {
    for (var exercise : exercises) {
      var user = getLoggedInUser();
      exercise.setCreatedBy(user);
      persistEntities(userExerciseRepository, exercise);
    }
  }
}
