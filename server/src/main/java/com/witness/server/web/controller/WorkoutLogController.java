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
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
@RequestMapping("workouts")
public class WorkoutLogController {

  // TODO add comment for exercise log (PATCH request)
  // TODO change position of exercises (PUT request)
  // TODO maybe change position of sets (PUT request)

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
  public List<WorkoutLogDto> getWorkoutLogs(
      @Parameter(description = "Day to fetch workout logs from. (ISO-8601 date-time)", example = "2021-10-08T14:15:55.3007597+02:00")
      @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime date) {
    var currentUser = securityService.getCurrentUser();
    var workoutLogs = workoutLogService.getWorkoutLogs(currentUser.getUid(), date);
    return workoutLogMapper.entitiesToDtos(workoutLogs);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Creates a new workout log.")
  public WorkoutLogDto createNewWorkoutLog(@Valid @RequestBody WorkoutLogCreateDto workoutLog) throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var workoutToCreate = workoutLogMapper.createDtoToEntity(workoutLog);
    var createdWorkoutLog = workoutLogService.createWorkoutLog(workoutToCreate, currentUser.getUid());
    return workoutLogMapper.entityToDto(createdWorkoutLog);
  }

  @PatchMapping("{workoutLogId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Sets the duration of a workout.")
  public WorkoutLogDto setWorkoutDuration(@PathVariable Long workoutLogId, @Valid @RequestBody @Positive Integer duration) throws DataAccessException,
      InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var modifiedWorkoutLog = workoutLogService.setWorkoutDuration(currentUser.getUid(), workoutLogId, duration);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @DeleteMapping("{workoutLogId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Deletes a workout log.")
  public void deleteWorkoutLog(@PathVariable Long workoutLogId) throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    workoutLogService.deleteWorkoutLog(currentUser.getUid(), workoutLogId);
  }

  @PostMapping("{workoutLogId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Adds an exercise log to an existing workout log.")
  public WorkoutLogDto addExerciseLog(@PathVariable Long workoutLogId, @Valid @RequestBody ExerciseLogCreateDto exerciseLog)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var exerciseLogToCreate = exerciseLogMapper.createDtoToEntity(exerciseLog);
    var modifiedWorkoutLog = workoutLogService.addExerciseLog(currentUser.getUid(), workoutLogId, exerciseLogToCreate);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @DeleteMapping("{workoutLogId}/{exerciseId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Delete an exercise log from an existing workout log.")
  public WorkoutLogDto deleteExerciseLog(@PathVariable Long workoutLogId, @PathVariable Long exerciseId)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var modifiedWorkoutLog = workoutLogService.deleteExerciseLog(currentUser.getUid(), workoutLogId, exerciseId);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @PostMapping("{workoutLogId}/{exerciseLogId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Add a set log to an existing exercise log in an existing workout log.")
  public WorkoutLogDto addSetLog(@PathVariable Long workoutLogId, @PathVariable Long exerciseLogId, @Valid @RequestBody SetLogCreateDto setLogDto)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var setLog = setLogMapper.createDtoToEntity(setLogDto);
    var modifiedWorkoutLog = workoutLogService.addSetLog(currentUser.getUid(), workoutLogId, exerciseLogId, setLog);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @PutMapping("{workoutLogId}/{exerciseLogId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Update a set log within an existing exercise log in an existing workout log")
  public WorkoutLogDto updateSetLog(@PathVariable Long workoutLogId, @PathVariable Long exerciseLogId, @Valid @RequestBody SetLogDto setLogDto)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var setLog = setLogMapper.dtoToEntity(setLogDto);
    var modifiedWorkoutLog = workoutLogService.updateSetLog(currentUser.getUid(), workoutLogId, exerciseLogId, setLog);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }

  @DeleteMapping("{workoutLogId}/{exerciseLogId}/{setLogId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Delete a set log from an existing exercise log in an existing workout log.")
  public WorkoutLogDto deleteSetLog(@PathVariable Long workoutLogId, @PathVariable Long exerciseLogId, @PathVariable Long setLogId)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var modifiedWorkoutLog = workoutLogService.deleteSetLog(currentUser.getUid(), workoutLogId, exerciseLogId, setLogId);
    return workoutLogMapper.entityToDto(modifiedWorkoutLog);
  }
}
