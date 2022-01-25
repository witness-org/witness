package com.witness.server.web.controller;

import com.witness.server.dto.workout.ExerciseLogCreateDto;
import com.witness.server.dto.workout.SetLogCreateDto;
import com.witness.server.dto.workout.SetLogDto;
import com.witness.server.dto.workout.WorkoutLogCreateDto;
import com.witness.server.dto.workout.WorkoutLogDto;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.mapper.ExerciseLogMapper;
import com.witness.server.mapper.SetLogMapper;
import com.witness.server.mapper.WorkoutLogMapper;
import com.witness.server.service.SecurityService;
import com.witness.server.service.WorkoutLogService;
import com.witness.server.web.meta.SecuredValidatedRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@SecuredValidatedRestController
@RequestMapping("workout-logs")
@Tag(name = "Workouts", description = "Provides endpoint methods for operations regarding the management of workout logs.")
public class WorkoutLogController {
  private final SecurityService securityService;
  private final WorkoutLogService workoutLogService;
  private final SetLogMapper setLogMapper;
  private final WorkoutLogMapper workoutLogMapper;
  private final ExerciseLogMapper exerciseLogMapper;

  @Autowired
  public WorkoutLogController(SecurityService securityService, WorkoutLogService workoutLogService, SetLogMapper setLogMapper,
                              WorkoutLogMapper workoutLogMapper, ExerciseLogMapper exerciseLogMapper) {
    this.securityService = securityService;
    this.workoutLogService = workoutLogService;
    this.setLogMapper = setLogMapper;
    this.workoutLogMapper = workoutLogMapper;
    this.exerciseLogMapper = exerciseLogMapper;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Gets the workout logs of the current user which were logged on a given day.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The workout logs were fetched successfully.")
  })
  public List<WorkoutLogDto> getWorkoutLogs(
      @Parameter(description = "Day to fetch workout logs from. (ISO-8601 date-time)", example = "2021-10-08T14:15:55.3007597+02:00")
      @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME,
          fallbackPatterns = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]XX") ZonedDateTime date) {
    var currentUser = securityService.getCurrentUser();
    var workoutLogs = workoutLogService.getWorkoutLogsOfDay(currentUser.getUid(), date);
    return workoutLogMapper.entitiesToDtos(workoutLogs);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Creates a new workout log.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "The new workout log has been created successfully."),
      @ApiResponse(responseCode = "400", description = "A logging type that is invalid for the referenced exercise was requested or managing "
                                                       + "exercise/set log positions failed"),
      @ApiResponse(responseCode = "404", description = "Current user or exercise referenced by new workout were not found."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user.")
  })
  public WorkoutLogDto createNewWorkoutLog(@Valid @RequestBody
                                           @Parameter(description = "The workout log to create.") WorkoutLogCreateDto workoutLog)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var workoutToCreate = workoutLogMapper.createDtoToEntity(workoutLog);
    var createdWorkoutLog = workoutLogService.createWorkoutLog(workoutToCreate, currentUser.getUid());
    return workoutLogMapper.entityToDto(createdWorkoutLog);
  }

  @PatchMapping("{workoutLogId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Sets the duration of a workout.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The workout duration has been set successfully."),
      @ApiResponse(responseCode = "400", description = "The current user is not the owner of the workout log to edit."),
      @ApiResponse(responseCode = "404", description = "The requested workout log or current user were not found."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user."),
  })
  public WorkoutLogDto setWorkoutDuration(
      @PathVariable @Parameter(description = "ID of the workout log for which the duration should be set.", example = "1") Long workoutLogId,
      @Valid @RequestBody @PositiveOrZero @Parameter(description = "New duration of the workout log", example = "45") Integer duration)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var modifiedWorkoutLog = workoutLogService.setWorkoutDuration(currentUser.getUid(), workoutLogId, duration);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @DeleteMapping("{workoutLogId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Deletes a workout log.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "The workout log has been deleted successfully."),
      @ApiResponse(responseCode = "400", description = "The current user is not the owner of the workout log to delete."),
      @ApiResponse(responseCode = "404", description = "The current user or the workout log to delete were not found."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user.")
  })
  public void deleteWorkoutLog(@PathVariable @Parameter(description = "ID of the workout log to delete", example = "2") Long workoutLogId)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    workoutLogService.deleteWorkoutLog(currentUser.getUid(), workoutLogId);
  }

  @PostMapping("{workoutLogId}/exercise-logs")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Adds an exercise log to an existing workout log.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "The exercise log has successfully been created and added to the workout log."),
      @ApiResponse(responseCode = "400", description = "The current user is not the owner of the workout log the new exercise log should be "
                                                       + "added to, the exercise log contains a set log with logging type that is invalid for the "
                                                       + "referenced exercise or managing set log positions failed."),
      @ApiResponse(responseCode = "404", description = "The workout log containing the new exercise log, the exercise referenced by the new "
                                                       + "exercise log or the current user does not exist."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user.")
  })
  public WorkoutLogDto addExerciseLog(
      @PathVariable @Parameter(description = "ID of the workout the new exercise log should be added to.", example = "3") Long workoutLogId,
      @Valid @RequestBody @Parameter(description = "New exercise log to add to the workout log.") ExerciseLogCreateDto exerciseLog)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var exerciseLogToCreate = exerciseLogMapper.createDtoToEntity(exerciseLog);
    var modifiedWorkoutLog = workoutLogService.addExerciseLog(currentUser.getUid(), workoutLogId, exerciseLogToCreate);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @PutMapping("{workoutLogId}/exercise-logs-positions")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Updates the positions of exercise logs in a workout log.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The exercise log positions have been updated successfully."),
      @ApiResponse(responseCode = "400", description = "The current user is not the owner of the workout log whose exercise log positions should "
                                                       + "be updated, the new positions do not cover all exercise logs or the requested new "
                                                       + "positions contain duplicates."),
      @ApiResponse(responseCode = "404", description = "The workout log whose exercise log positions should be updated or the current user does "
                                                       + "not exist."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user.")
  })
  public WorkoutLogDto updateExerciseLogPositions(
      @PathVariable @Parameter(description = "ID of the workout whose exercise log positions should be updated.", example = "3") Long workoutLogId,
      @Valid @RequestBody
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Mapping of exercise log IDs from the the specified workout to their one-based new positions",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
              @ExampleObject(
                  name = "Gapless Exercise Log Position Specification",
                  description = "Specifies that exercise log with ID 11 is updated to position 2, ID 22 to position 3 and ID 33 to position 1.",
                  value = "{\"11\": 2,\"22\": 3,\"33\": 1}"),
              @ExampleObject(
                  name = "Exercise Log Position Specification With Gaps",
                  description = "Maps exercise log with ID 11 to position 37, ID 22 to position 4 and ID 33 to position 15. Server internally "
                                + "simplifies this before persisting such that ID 11 is mapped to position 3, ID 22 to position 1 and ID 33 to "
                                + "position 2.",
                  value = "{\"11\": 37,\"22\": 4,\"33\": 15}")
          })) Map<Long, Integer> positions) throws InvalidRequestException, DataAccessException {
    var currentUser = securityService.getCurrentUser();
    var modifiedWorkoutLog = workoutLogService.updateExerciseLogPositions(currentUser.getUid(), workoutLogId, positions);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @DeleteMapping("{workoutLogId}/exercise-logs/{exerciseLogId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Delete an exercise log from an existing workout log.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The exercise log has been successfully removed from the workout log."),
      @ApiResponse(responseCode = "400", description = "The current user is not the owner of the workout log the exercise log should be deleted "
                                                       + "from, managing exercise log positions failed or the exercise log to delete is not part of "
                                                       + "the workout log to delete from."),
      @ApiResponse(responseCode = "404", description = "The current user, the workout log to delete the exercise log from or the exercise log to "
                                                       + "to delete does not exist."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user or the specified exercise log could not "
                                                       + "be removed from the specified workout log.")
  })
  public WorkoutLogDto deleteExerciseLog(
      @PathVariable @Parameter(description = "ID of the workout log the exercise log should be deleted from.", example = "7") Long workoutLogId,
      @PathVariable @Parameter(description = "ID of the exercise log to delete.", example = "3") Long exerciseLogId)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var modifiedWorkoutLog = workoutLogService.deleteExerciseLog(currentUser.getUid(), workoutLogId, exerciseLogId);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @PatchMapping("{workoutLogId}/exercise-logs/{exerciseLogId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Sets the comment of an existing workout log.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The comment has been successfully set for the exercise log."),
      @ApiResponse(responseCode = "400", description = "The current user is not the owner of the workout log the exercise log whose comment should "
                                                       + "be set belongs to or the exercise log whose comment should be set does not belong to the "
                                                       + "specified workout log."),
      @ApiResponse(responseCode = "404", description = "The current user, the workout log the exercise log whose comment should be set belongs to, "
                                                       + "or the exercise log whose comment should be set does not exist."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user.")
  })
  public WorkoutLogDto setExerciseLogComment(
      @PathVariable @Parameter(description = "ID of the workout log containing the exercise log to update.", example = "8") Long workoutLogId,
      @PathVariable @Parameter(description = "ID of the exercise log whose comment should bet set.", example = "3") Long exerciseLogId,
      @Valid @RequestBody(required = false) @Length(max = 256)
      @Parameter(description = "New comment of the exercise log.", example = "Skipped legs due to injury.") String comment)
      throws InvalidRequestException, DataAccessException {
    var currentUser = securityService.getCurrentUser();
    var modifiedWorkoutLog = workoutLogService.setExerciseLogComment(currentUser.getUid(), workoutLogId, exerciseLogId, comment);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @PostMapping("{workoutLogId}/exercise-logs/{exerciseLogId}/set-logs")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Add a set log to an existing exercise log in an existing workout log.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "The new set log has been successfully created."),
      @ApiResponse(responseCode = "400", description = "The current user is not the owner of the workout log the new set log should be added to, "
                                                       + "the specified exercise log does not belong to the specified workout log or a set log "
                                                       + "associated with a logging type that is invalid for the corresponding exercise was "
                                                       + "requested."),
      @ApiResponse(responseCode = "404", description = "The current user, the specified workout log or specified exercise log does not exist."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user.")
  })
  public WorkoutLogDto addSetLog(
      @PathVariable @Parameter(description = "The workout log with the exercise log to receive the new set log.", example = "28") Long workoutLogId,
      @PathVariable @Parameter(description = "The exercise log the new set log should be added to.", example = "17") Long exerciseLogId,
      @Valid @RequestBody @Parameter(description = "The set log to be added to the exercise log.") SetLogCreateDto setLogDto)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var setLog = setLogMapper.createDtoToEntity(setLogDto);
    var modifiedWorkoutLog = workoutLogService.addSetLog(currentUser.getUid(), workoutLogId, exerciseLogId, setLog);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @PutMapping("{workoutLogId}/exercise-logs/{exerciseLogId}/set-logs")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Updates a set log within an existing exercise log in an existing workout log")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The old set log has been successfully replaced with the new set log."),
      @ApiResponse(responseCode = "400", description = "The current user is not the owner of the workout log containing the set log to be updated, "
                                                       + "the specified exercise log containing the set log to update is not part of the specified "
                                                       + "workout log, the updated set log is not part of the specified exercise log, the updated "
                                                       + "set log has an updated position property or the updated set log has an associated logging "
                                                       + "type that is invalid for the containing exercise log."),
      @ApiResponse(responseCode = "404", description = "The current user, the specified workout log, the specified exercise log or the set log "
                                                       + "to update does not exist."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user.")
  })
  public WorkoutLogDto updateSetLog(
      @PathVariable @Parameter(description = "The workout with the exercise log containing the set log to update.", example = "13") Long workoutLogId,
      @PathVariable @Parameter(description = "The exercise log containing the set log to update.", example = "24") Long exerciseLogId,
      @Valid @RequestBody @Parameter(description = "The updated set log.") SetLogDto setLogDto)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var setLog = setLogMapper.dtoToEntity(setLogDto);
    var modifiedWorkoutLog = workoutLogService.updateSetLog(currentUser.getUid(), workoutLogId, exerciseLogId, setLog);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @PutMapping("{workoutLogId}/exercise-logs/{exerciseLogId}/set-logs-positions")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Updates the positions of set logs in an exercise log.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The set log positions have been updated successfully."),
      @ApiResponse(responseCode = "400", description = "The current user is not the owner of the workout log containing the set logs whose positions "
                                                       + "should be updated, the specified exercise log is not part of the specified workout log, "
                                                       + "the new positions do not cover all exercise logs or the requested new positions contain "
                                                       + "duplicates."),
      @ApiResponse(responseCode = "404", description = "The workout log whose exercise log positions should be updated or the current user does "
                                                       + "not exist."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user.")
  })
  public WorkoutLogDto updateSetLogPositions(
      @PathVariable @Parameter(description = "Workout log with the exercise log containing set logs to reposition.", example = "2") Long workoutLogId,
      @PathVariable @Parameter(description = "Exercise log containing set logs whose positions should be updated.", example = "5") Long exerciseLogId,
      @Valid @RequestBody
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Mapping of set log IDs from the the specified workout to their one-based new positions",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
              @ExampleObject(
                  name = "Gapless Set Log Position Specification",
                  description = "Specifies that set log with ID 11 is updated to position 2, ID 22 to position 3 and ID 33 to position 1.",
                  value = "{\"11\": 2,\"22\": 3,\"33\": 1}"),
              @ExampleObject(
                  name = "Set Log Position Specification With Gaps",
                  description = "Maps set log with ID 11 to position 37, ID 22 to position 4 and ID 33 to position 15. Server internally "
                                + "simplifies this before persisting such that ID 11 is mapped to position 3, ID 22 to position 1 and ID 33 to "
                                + "position 2.",
                  value = "{\"11\": 37,\"22\": 4,\"33\": 15}")
          })) Map<Long, Integer> positions) throws InvalidRequestException, DataAccessException {
    var currentUser = securityService.getCurrentUser();
    var modifiedWorkoutLog = workoutLogService.updateSetLogPositions(currentUser.getUid(), workoutLogId, exerciseLogId, positions);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @DeleteMapping("{workoutLogId}/exercise-logs/{exerciseLogId}/set-logs/{setLogId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Deletes a set log from an existing exercise log in an existing workout log.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The set log has been successfully deleted."),
      @ApiResponse(responseCode = "400", description = "The current user is not owner of the workout log containing the set log to be deleted, "
                                                       + "managing set log positions failed, the specified exercise log is not part of the specified "
                                                       + "workout log or the set log to delete is not part of the specified exercise log."),
      @ApiResponse(responseCode = "404", description = "The current user, the specified workout log, the specified exercise log or set log to delete "
                                                       + "does not exist."),
      @ApiResponse(responseCode = "500", description = "An error occurred while looking up the current user or the specified set log could not be "
                                                       + "removed from the specified exercise log.")
  })
  public WorkoutLogDto deleteSetLog(@PathVariable Long workoutLogId, @PathVariable Long exerciseLogId, @PathVariable Long setLogId)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var modifiedWorkoutLog = workoutLogService.deleteSetLog(currentUser.getUid(), workoutLogId, exerciseLogId, setLogId);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }
}
