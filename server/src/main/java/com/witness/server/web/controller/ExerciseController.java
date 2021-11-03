package com.witness.server.web.controller;

import com.witness.server.dto.exercise.ExerciseCreateDto;
import com.witness.server.dto.exercise.ExerciseDto;
import com.witness.server.dto.exercise.UserExerciseDto;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.mapper.ExerciseMapper;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.SecurityService;
import com.witness.server.web.meta.RequiresAdmin;
import com.witness.server.web.meta.SecuredValidatedRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@SecuredValidatedRestController
@RequestMapping("exercise")
@Tag(name = "Exercises",
    description = "This controller provides endpoint methods for operations regarding the management of initial and user exercises.")
public class ExerciseController {

  private final ExerciseService exerciseService;
  private final SecurityService securityService;
  private final ExerciseMapper exerciseMapper;

  @Autowired
  public ExerciseController(ExerciseService exerciseService, SecurityService securityService, ExerciseMapper exerciseMapper) {
    this.exerciseService = exerciseService;
    this.securityService = securityService;
    this.exerciseMapper = exerciseMapper;
  }

  @PostMapping("newInitialExercise")
  @ResponseStatus(HttpStatus.CREATED)
  @RequiresAdmin
  @Operation(summary = "Creates a new initial exercise. Only possible for admins.")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "The initial exercise was successfully created."),
          @ApiResponse(responseCode = "400", description = "The initial exercise could not be created because the request was invalid.")
      }
  )
  public ExerciseDto createInitialExercise(@Valid @RequestBody
                                           @Parameter(description = "The exercise that should be created.") ExerciseCreateDto exercise)
      throws InvalidRequestException {
    var entity = exerciseMapper.createDtoToEntity(exercise);
    var createdEntity = exerciseService.createInitialExercise(entity);
    return exerciseMapper.entityToDto(createdEntity);
  }

  @PostMapping("newUserExercise")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Creates a new exercise that is visible only to the requesting user.")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "201", description = "The user exercise was successfully created."),
          @ApiResponse(responseCode = "400", description = "The user exercise could not be created because the request was invalid."),
          @ApiResponse(responseCode = "404",
              description = "The exercise could not be created because the Firebase ID of the logged-in user cannot be found in the database."),
          @ApiResponse(responseCode = "500",
              description = "The user exercise could not be created because the logged-in user could not be found in the database.")
      }
  )
  public UserExerciseDto createUserExercise(@Valid @RequestBody
                                            @Parameter(description = "The exercise that should be created.") ExerciseCreateDto exercise)
      throws DataAccessException, InvalidRequestException {
    var entity = exerciseMapper.createDtoToUserEntity(exercise);
    var currentUser = securityService.getCurrentUser();
    var createdEntity = exerciseService.createUserExercise(currentUser.getUid(), entity);
    return exerciseMapper.userEntityToDto(createdEntity);
  }

  @PutMapping("updateInitialExercise")
  @ResponseStatus(HttpStatus.OK)
  @RequiresAdmin
  @Operation(summary = "Updates an initial exercise. Only possible for admins.")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "The initial exercise was successfully updated."),
          @ApiResponse(responseCode = "400", description = "The initial exercise could not be updated because the request was invalid."),
          @ApiResponse(responseCode = "404", description = "The requested initial exercise does not exist.")
      }
  )
  public ExerciseDto updateInitialExercise(@Valid @RequestBody
                                           @Parameter(description = "The exercise that should be updated.") ExerciseDto exercise)
      throws InvalidRequestException, DataNotFoundException {
    var entity = exerciseMapper.dtoToEntity(exercise);
    var createdEntity = exerciseService.updateInitialExercise(entity);
    return exerciseMapper.entityToDto(createdEntity);
  }

  @PutMapping("updateUserExercise")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Updates an exercise that is visible only to the requesting user.")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "The user exercise was successfully updated."),
          @ApiResponse(responseCode = "400", description = "The user exercise could not be updated because the request was invalid."),
          @ApiResponse(responseCode = "404",
              description = "The requested exercise does not exist or the Firebase ID of the logged-in user cannot be found in the database."),
          @ApiResponse(responseCode = "500",
              description = "The user exercise could not be updated because the logged-in user could not be found in the database.")
      }
  )
  public UserExerciseDto updateUserExercise(@Valid @RequestBody
                                            @Parameter(description = "The exercise that should be updated.") ExerciseDto exercise)
      throws DataAccessException, InvalidRequestException {
    var entity = exerciseMapper.dtoToEntity(exercise);
    var currentUser = securityService.getCurrentUser();
    var createdEntity = exerciseService.updateUserExercise(currentUser.getUid(), entity);
    return exerciseMapper.userEntityToDto(createdEntity);
  }

  @GetMapping("allByMuscleGroup")
  @Operation(summary = "Fetches all exercises which are either public or only visible to the logged-in user which train the given muscle group.")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "The operation was successful."),
          @ApiResponse(responseCode = "404",
              description = "The exercises could not be fetched because the Firebase ID of the logged-in user cannot be found in the database."),
          @ApiResponse(responseCode = "500",
              description = "The exercises could not be fetched because the logged-in user could not be found in the database.")
      }
  )
  public List<ExerciseDto> getAllForUserByMuscleGroup(
      @RequestParam(name = "muscleGroup") @NotNull
      @Parameter(description = "The muscle group that should be trained.", example = "CHEST") MuscleGroup muscleGroup)
      throws DataAccessException {
    var currentUser = securityService.getCurrentUser();
    var result = exerciseService.getExercisesForUserByMuscleGroup(currentUser.getUid(), muscleGroup);
    return exerciseMapper.entitiesToDtos(result);
  }

  @GetMapping("allCreatedByUser")
  @Operation(summary = "Fetches all exercises created by the logged-in user.")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "The operation was successful."),
          @ApiResponse(responseCode = "404",
              description = "The exercise could not be fetched because the Firebase ID of the logged-in user cannot be found in the database."),
          @ApiResponse(responseCode = "500",
              description = "The exercises could not be fetched because the logged-in user could not be found in the database.")
      }
  )
  public List<ExerciseDto> getAllCreatedByUser() throws DataAccessException {
    var currentUser = securityService.getCurrentUser();
    var result = exerciseService.getExercisesCreatedByUser(currentUser.getUid());
    return exerciseMapper.entitiesToDtos(result);
  }
}
