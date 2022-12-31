package com.witness.server.web.controller;

import com.witness.server.dto.exercise.ExerciseCreateDto;
import com.witness.server.dto.exercise.ExerciseDto;
import com.witness.server.dto.exercise.ExerciseHistoryDto;
import com.witness.server.dto.exercise.ExerciseStatisticsDto;
import com.witness.server.dto.exercise.UserExerciseDto;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.mapper.ExerciseHistoryMapper;
import com.witness.server.mapper.ExerciseMapper;
import com.witness.server.mapper.ExerciseStatisticsMapper;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.SecurityService;
import com.witness.server.web.meta.RequiresAdmin;
import com.witness.server.web.meta.SecuredValidatedRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@SecuredValidatedRestController
@RequestMapping("exercises")
@Tag(name = "Exercises", description = "Provides endpoint methods for operations regarding the management of initial and user exercises.")
public class ExerciseController {

  private final ExerciseService exerciseService;
  private final SecurityService securityService;
  private final ExerciseMapper exerciseMapper;
  private final ExerciseHistoryMapper exerciseHistoryMapper;
  private final ExerciseStatisticsMapper exerciseStatisticsMapper;

  @Autowired
  public ExerciseController(ExerciseService exerciseService, SecurityService securityService, ExerciseMapper exerciseMapper,
                            ExerciseHistoryMapper exerciseHistoryMapper, ExerciseStatisticsMapper exerciseStatisticsMapper) {
    this.exerciseService = exerciseService;
    this.securityService = securityService;
    this.exerciseMapper = exerciseMapper;
    this.exerciseHistoryMapper = exerciseHistoryMapper;
    this.exerciseStatisticsMapper = exerciseStatisticsMapper;
  }

  @PostMapping("initial-exercises")
  @ResponseStatus(HttpStatus.CREATED)
  @RequiresAdmin
  @Operation(summary = "Creates a new initial exercise. Only possible for admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "The initial exercise was successfully created."),
      @ApiResponse(responseCode = "400", description = "The initial exercise could not be created because the request was invalid."),
      @ApiResponse(responseCode = "403", description = "The initial exercise could not be created because the requester does not have the required "
                                                       + "permission.")
  })
  public ExerciseDto createInitialExercise(@Valid @RequestBody
                                           @Parameter(description = "The exercise that should be created.") ExerciseCreateDto exercise)
      throws InvalidRequestException {
    var entity = exerciseMapper.createDtoToEntity(exercise);
    var createdEntity = exerciseService.createInitialExercise(entity);
    return exerciseMapper.entityToDto(createdEntity);
  }

  @PostMapping("user-exercises")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Creates a new exercise that is visible only to the requesting user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "The user exercise was successfully created."),
      @ApiResponse(responseCode = "400", description = "The user exercise could not be created because the request was invalid."),
      @ApiResponse(responseCode = "404", description = "The exercise could not be created because the Firebase ID of the logged-in user cannot be "
                                                       + "found in the database."),
      @ApiResponse(responseCode = "500", description = "The user exercise could not be created because the logged-in user could not be found in "
                                                       + "the database.")
  })
  public UserExerciseDto createUserExercise(@Valid @RequestBody
                                            @Parameter(description = "The exercise that should be created.") ExerciseCreateDto exercise)
      throws DataAccessException, InvalidRequestException {
    var entity = exerciseMapper.createDtoToUserEntity(exercise);
    var currentUser = securityService.getCurrentUser();
    var createdEntity = exerciseService.createUserExercise(currentUser.getUid(), entity);
    return exerciseMapper.userEntityToDto(createdEntity);
  }

  @PutMapping("initial-exercises")
  @ResponseStatus(HttpStatus.OK)
  @RequiresAdmin
  @Operation(summary = "Updates an initial exercise. Only possible for admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The initial exercise was successfully updated."),
      @ApiResponse(responseCode = "400", description = "The initial exercise could not be updated because the request was invalid."),
      @ApiResponse(responseCode = "403", description = "The initial exercise could not be updated because the requester does not have the required "
                                                       + "permission."),
      @ApiResponse(responseCode = "404", description = "The requested initial exercise does not exist.")
  })
  public ExerciseDto updateInitialExercise(@Valid @RequestBody
                                           @Parameter(description = "The exercise that should be updated.") ExerciseDto exercise)
      throws InvalidRequestException, DataNotFoundException {
    var entity = exerciseMapper.dtoToEntity(exercise);
    var createdEntity = exerciseService.updateInitialExercise(entity);
    return exerciseMapper.entityToDto(createdEntity);
  }

  @PutMapping("user-exercises")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Updates an exercise that is visible only to the requesting user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The user exercise was successfully updated."),
      @ApiResponse(responseCode = "400", description = "The user exercise could not be updated because the request was invalid."),
      @ApiResponse(responseCode = "404", description = "The requested exercise does not exist or the Firebase ID of the logged-in user cannot be "
                                                       + "found in the database."),
      @ApiResponse(responseCode = "500", description = "The user exercise could not be updated because the logged-in user could not be found in "
                                                       + "the database.")
  })
  public UserExerciseDto updateUserExercise(@Valid @RequestBody
                                            @Parameter(description = "The exercise that should be updated.") ExerciseDto exercise)
      throws DataAccessException, InvalidRequestException {
    var entity = exerciseMapper.dtoToEntity(exercise);
    var currentUser = securityService.getCurrentUser();
    var createdEntity = exerciseService.updateUserExercise(currentUser.getUid(), entity);
    return exerciseMapper.userEntityToDto(createdEntity);
  }

  @GetMapping
  @Operation(summary = "Fetches all exercises which are either public or only visible to the logged-in user which train the given muscle group.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The operation was successful."),
      @ApiResponse(responseCode = "404", description = "The exercises could not be fetched because the Firebase ID of the logged-in user cannot be "
                                                       + "found in the database."),
      @ApiResponse(responseCode = "500", description = "The exercises could not be fetched because the logged-in user could not be found in the "
                                                       + "database.")
  })
  public List<ExerciseDto> getAllForUserByMuscleGroup(
      @RequestParam(name = "muscle-group") @NotNull
      @Parameter(description = "The muscle group that should be trained.", example = "CHEST") MuscleGroup muscleGroup)
      throws DataAccessException {
    var currentUser = securityService.getCurrentUser();
    var result = exerciseService.getExercisesForUserByMuscleGroup(currentUser.getUid(), muscleGroup);
    return exerciseMapper.entitiesToDtos(result);
  }

  @GetMapping("user-exercises")
  @Operation(summary = "Fetches all exercises created by the logged-in user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The operation was successful."),
      @ApiResponse(responseCode = "404", description = "The exercise could not be fetched because the Firebase ID of the logged-in user cannot be "
                                                       + "found in the database."),
      @ApiResponse(responseCode = "500", description = "The exercises could not be fetched because the logged-in user could not be found in the "
                                                       + "database.")
  })
  public List<ExerciseDto> getAllCreatedByUser() throws DataAccessException {
    var currentUser = securityService.getCurrentUser();
    var result = exerciseService.getExercisesCreatedByUser(currentUser.getUid());
    return exerciseMapper.entitiesToDtos(result);
  }

  @DeleteMapping("initial-exercises/{exerciseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @RequiresAdmin
  @Operation(summary = "Deletes an initial exercise. Only allowed for admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "The initial exercise was successfully deleted."),
      @ApiResponse(responseCode = "403", description = "The initial exercise could not be deleted because the requester does not have the required "
                                                       + "permission."),
      @ApiResponse(responseCode = "404", description = "No initial exercise with the given ID could be found.")
  })
  public void deleteInitialExercise(@PathVariable @Parameter(description = "ID of the initial exercise to delete.", example = "3") Long exerciseId)
      throws DataNotFoundException {
    exerciseService.deleteInitialExercise(exerciseId);
  }

  @DeleteMapping("user-exercises/{userExerciseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Deletes a user exercise specific to the requesting account.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "The user exercise was successfully deleted."),
      @ApiResponse(responseCode = "403", description = "The user exercise could not be deleted because the requester is not its owner."),
      @ApiResponse(responseCode = "404", description = "No user exercise with the given ID could be found."),
      @ApiResponse(responseCode = "500", description = "The exercises could not be fetched because the logged-in user could not be found in the "
                                                       + "database."),
  })
  public void deleteUserExercise(@PathVariable @Parameter(description = "ID of the user exercise to delete.", example = "8") Long userExerciseId)
      throws InvalidRequestException, DataAccessException {
    var currentUser = securityService.getCurrentUser();
    exerciseService.deleteUserExercise(currentUser.getUid(), userExerciseId);
  }

  @GetMapping("history/{exerciseId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Gets the history (i.e. recorded logs) of the specified exercise which were logged by the current user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The history entries of the specified exercise were fetched successfully."),
      @ApiResponse(responseCode = "404", description = "The history entries could not be fetched because the provided exercise ID or the Firebase ID "
                                                       + " of the logged-in user cannot be found in the database."),
      @ApiResponse(responseCode = "500", description = "The history entries could not be fetched because the logged-in user could not be found in "
                                                       + "the database.")
  })
  public ExerciseHistoryDto getExerciseHistory(
      @PathVariable @Parameter(description = "ID of the exercise whose logs should be retrieved.") Long exerciseId) throws DataAccessException {
    var currentUser = securityService.getCurrentUser();
    var exerciseLogs = exerciseService.getExerciseLogs(currentUser.getUid(), exerciseId);
    return exerciseHistoryMapper.exerciseLogsToHistoryDto(exerciseLogs);
  }

  @GetMapping("statistics/{exerciseId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Gets the statistics of the specified exercise which were logged by the current user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The statistics for the specified exercise were fetched successfully."),
      @ApiResponse(responseCode = "404", description = "The statistics could not be fetched because the provided exercise ID or the Firebase ID "
          + " of the logged-in user cannot be found in the database."),
      @ApiResponse(responseCode = "500", description = "The statistics could not be fetched because the logged-in user could not be found in "
          + "the database.")
  })
  public ExerciseStatisticsDto getExerciseStatistics(
      @PathVariable @Parameter(description = "ID of the exercise whose statistics should be retrieved.") Long exerciseId) throws DataAccessException {
    var currentUser = securityService.getCurrentUser();
    var exerciseStatistics = exerciseService.getExerciseStatistics(currentUser.getUid(), exerciseId);
    return exerciseStatisticsMapper.modelToDto(exerciseStatistics);
  }
}
