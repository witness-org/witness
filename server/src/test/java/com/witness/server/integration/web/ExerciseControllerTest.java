package com.witness.server.integration.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.ExerciseCreateDto;
import com.witness.server.dto.ExerciseDto;
import com.witness.server.dto.UserExerciseDto;
import com.witness.server.entity.Exercise;
import com.witness.server.entity.UserExercise;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.UserExerciseRepository;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;

class ExerciseControllerTest extends BaseControllerIntegrationTest {
  private static final String DATA_ROOT = "data/integration/web/exercise-controller-test/";

  private static final String CREATE_INITIAL_EXERCISE_URL = "/newInitialExercise";
  private static final String CREATE_USER_EXERCISE_URL = "/newUserExercise";
  private static final String GET_ALL_FOR_USER_BY_MUSCLE_GROUP_URL = "/allByMuscleGroup";
  private static final String GET_ALL_CREATED_BY_USER_URL = "/allCreatedByUser";

  @Autowired
  private ExerciseRepository exerciseRepository;

  @Autowired
  private UserExerciseRepository userExerciseRepository;

  @Override
  String getEndpointUrl() {
    return "exercise";
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDto1.json", type = ExerciseDto.class)
  })
  void createInitialExercise_validExerciseDtoAsAdmin_return201AndReturnCorrectDto(ExerciseCreateDto exerciseCreateDto,
                                                                                  ExerciseDto exerciseDto) {
    var response =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(exerciseDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_validExerciseDtoAsPremium_return403(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.PREMIUM, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_validExerciseDtoAsRegular_return403(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.REGULAR, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_validExerciseDtoNoAuthorization_return401(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.NONE, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullName.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoNullNameAsAdmin_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyName.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoEmptyNameAsAdmin_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoLongName.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoLongNameAsAdmin_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoLongDescription.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoLongDescriptionAsAdmin_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullMuscleGroups.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoNullMuscleGroupsAsAdmin_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullLoggingTypes.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoNullLoggingTypesAsAdmin_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyMuscleGroups.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoEmptyMuscleGroupsAsAdmin_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyLoggingTypes.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_exerciseDtoEmptyLoggingTypesAsAdmin_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, ExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createInitialExercise_validExerciseDtoTakenNameAsAdmin_return400(Exercise persistedExercise,
                                                                        ExerciseCreateDto secondDto) {
    persistEntities(exerciseRepository, persistedExercise);

    var secondResponse =
        exchange(TestAuthentication.ADMIN, requestUrl() + CREATE_INITIAL_EXERCISE_URL, HttpMethod.POST,
            secondDto, ExerciseDto.class);

    assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseDto1.json", type = UserExerciseDto.class)
  })
  void createUserExercise_validExerciseDtoAsAdmin_return201AndReturnCorrectDto(ExerciseCreateDto exerciseCreateDto,
                                                                               UserExerciseDto exerciseDto) {
    TestAuthentication authMode = TestAuthentication.ADMIN;
    persistUserAndMockLoggedIn(authMode);

    var response =
        exchange(authMode, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison()
        .ignoringFields("id", "createdById")
        .isEqualTo(exerciseDto);
    assertThat(response.getBody().getCreatedByUserId()).isNotNull();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDto1.json", type = UserExerciseDto.class)
  })
  void createUserExercise_validExerciseDtoAsPremium_return201AndReturnCorrectDto(ExerciseCreateDto exerciseCreateDto,
                                                                                 UserExerciseDto exerciseDto) {
    TestAuthentication authMode = TestAuthentication.PREMIUM;
    persistUserAndMockLoggedIn(authMode);

    var response =
        exchange(authMode, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison()
        .ignoringFields("id", "createdByUserId")
        .isEqualTo(exerciseDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseDto1.json", type = UserExerciseDto.class)
  })
  void createUserExercise_validExerciseDtoAsRegular_return201AndReturnCorrectDto(ExerciseCreateDto exerciseCreateDto,
                                                                                 UserExerciseDto exerciseDto) {
    TestAuthentication authMode = TestAuthentication.REGULAR;
    persistUserAndMockLoggedIn(authMode);

    var response =
        exchange(authMode, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison()
        .ignoringFields("id", "createdByUserId")
        .isEqualTo(exerciseDto);
    assertThat(response.getBody().getCreatedByUserId()).isNotNull();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_validExerciseDtoNoAuthorization_return401(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.NONE, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullName.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoNullNameAsRegular_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.REGULAR, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyName.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoEmptyNameAsRegular_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.REGULAR, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoLongName.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoLongNameAsRegular_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.REGULAR, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoLongDescription.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoLongDescriptionAsRegular_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.REGULAR, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullMuscleGroups.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoNullMuscleGroupsAsRegular_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.REGULAR, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoNullLoggingTypes.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoNullLoggingTypesAsRegular_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.REGULAR, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyMuscleGroups.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoEmptyMuscleGroupsAsRegular_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.REGULAR, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDtoEmptyLoggingTypes.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_exerciseDtoEmptyLoggingTypesAsRegular_return400(ExerciseCreateDto exerciseCreateDto) {
    var response =
        exchange(TestAuthentication.REGULAR, requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST,
            exerciseCreateDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class)
  })
  void createUserExercise_validExerciseDtoTakenNameInitialAsRegular_return400(Exercise persistedExercise,
                                                                              ExerciseCreateDto createDto) {
    persistEntities(exerciseRepository, persistedExercise);

    var secondResponse = exchange(TestAuthentication.REGULAR,
        requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST, createDto, ExerciseDto.class);

    assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class),
  })
  void createUserExercise_validExerciseDtoTakenNameUserAsRegular_return400(UserExercise persistedUserExercise,
                                                                           ExerciseCreateDto createDto) {
    TestAuthentication authMode = TestAuthentication.REGULAR;
    persistUserAndMockLoggedIn(authMode);
    persistUserExercises(persistedUserExercise);

    var response = exchange(authMode,
        requestUrl() + CREATE_USER_EXERCISE_URL, HttpMethod.POST, createDto, UserExerciseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void getAllForUserByMuscleGroup_nonePersistedAsRegular_return200AndEmptyList() {
    TestAuthentication authMode = TestAuthentication.REGULAR;
    persistUserAndMockLoggedIn(authMode);

    var params = new LinkedMultiValueMap<String, String>();
    params.put("muscleGroup", Collections.singletonList(MuscleGroup.CHEST.toString()));
    var response = get(authMode, requestUrl() + GET_ALL_FOR_USER_BY_MUSCLE_GROUP_URL, params, ExerciseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise2.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDtos_1-2.json", type = ExerciseDto[].class)
  })
  void getAllForUserByMuscleGroup_oneInitialOneCreatedPersistedAsRegular_return200AndEmptyList(Exercise persistedInitialExercise,
                                                                                               UserExercise persistedUserExercise,
                                                                                               ExerciseDto[] expected) {
    TestAuthentication authMode = TestAuthentication.REGULAR;
    persistUserAndMockLoggedIn(authMode);
    persistEntities(exerciseRepository, persistedInitialExercise);
    persistUserExercises(persistedUserExercise);

    var params = new LinkedMultiValueMap<String, String>();
    params.put("muscleGroup", Collections.singletonList(MuscleGroup.CHEST.toString()));
    var response = get(authMode, requestUrl() + GET_ALL_FOR_USER_BY_MUSCLE_GROUP_URL, params, ExerciseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .usingElementComparatorIgnoringFields("id")
        .containsExactlyInAnyOrder(expected);
  }

  @Test
  void getAllCreatedByUser_noExercisesPersistedAsRegular_return200AndEmptyList() {
    TestAuthentication authMode = TestAuthentication.REGULAR;
    persistUserAndMockLoggedIn(authMode);

    var response = get(authMode, requestUrl() + GET_ALL_CREATED_BY_USER_URL, ExerciseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise2.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDtos_2.json", type = ExerciseDto[].class)
  })
  void getAllCreatedByUser_oneInitialOneCreatedPersistedAsRegular_return200AndEmptyList(Exercise persistedInitialExercise,
                                                                                        UserExercise persistedUserExercise,
                                                                                        ExerciseDto[] expected) {
    TestAuthentication authMode = TestAuthentication.REGULAR;
    persistUserAndMockLoggedIn(authMode);
    persistEntities(exerciseRepository, persistedInitialExercise);
    persistUserExercises(persistedUserExercise);

    var response = get(authMode, requestUrl() + GET_ALL_CREATED_BY_USER_URL, ExerciseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .usingElementComparatorIgnoringFields("id")
        .containsExactlyInAnyOrder(expected);
  }

  private void persistUserExercises(UserExercise... exercises) {
    for (UserExercise exercise : exercises) {
      var user = getLoggedInUser();
      exercise.setCreatedBy(user);
      persistEntities(userExerciseRepository, exercise);
    }
  }
}
