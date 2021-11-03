package com.witness.server.web.controller;

import com.witness.server.dto.AbstractSetLogCreateDto;
import com.witness.server.dto.AbstractSetLogDto;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.mapper.SetLogMapper;
import com.witness.server.service.SecurityService;
import com.witness.server.service.WorkoutLogService;
import com.witness.server.web.meta.SecuredValidatedRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @Autowired
  public WorkoutLogController(SecurityService securityService, WorkoutLogService workoutLogService,
                              SetLogMapper setLogMapper) {
    this.securityService = securityService;
    this.workoutLogService = workoutLogService;
    this.setLogMapper = setLogMapper;
  }

  @PostMapping("")
  @ResponseStatus(HttpStatus.CREATED)
  public String createNewWorkoutLog() throws DataAccessException {
    var currentUser = securityService.getCurrentUser();
    workoutLogService.createNewWorkoutLog(currentUser.getUid());
    return "CHECK DATABASE";
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public String setWorkoutDuration(@PathVariable Long id, @RequestBody Integer duration) throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    workoutLogService.setWorkoutDuration(currentUser.getUid(), id, duration);
    return "CHECK DATABASE";
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public String deleteWorkoutLog(@PathVariable Long id) throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    workoutLogService.deleteWorkoutLog(currentUser.getUid(), id);
    return "CHECK DATABASE";
  }

  @PostMapping("/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  public String addExerciseLog(@PathVariable Long id, @RequestBody Long exerciseId) throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    workoutLogService.addExerciseLog(currentUser.getUid(), id, exerciseId);
    return "CHECK DATABASE";
  }

  @DeleteMapping("/{id}/{exerciseId}")
  @ResponseStatus(HttpStatus.OK)
  public String deleteExerciseLog(@PathVariable Long id, @PathVariable Long exerciseId) throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    workoutLogService.deleteExerciseLog(currentUser.getUid(), id, exerciseId);
    return "CHECK DATABASE";
  }

  @PostMapping("/{id}/{exerciseId}")
  @ResponseStatus(HttpStatus.CREATED)
  public String addSetLog(@PathVariable Long id, @PathVariable Long exerciseId, @RequestBody AbstractSetLogCreateDto setLogDto)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var setLog = setLogMapper.createDtoToEntity(setLogDto);
    workoutLogService.addSetLog(currentUser.getUid(), id, exerciseId, setLog);
    return "CHECK DATABASE";
  }

  @PutMapping("/{id}/{exerciseId}")
  @ResponseStatus(HttpStatus.OK)
  public String updateSetLog(@PathVariable Long id, @PathVariable Long exerciseId, @RequestBody AbstractSetLogDto setLogDto)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    var setLog = setLogMapper.dtoToEntity(setLogDto);
    workoutLogService.updateSetLog(currentUser.getUid(), id, exerciseId, setLog);
    return "CHECK DATABASE";
  }

  @DeleteMapping("/{id}/{exerciseId}/{setId}")
  @ResponseStatus(HttpStatus.OK)
  public String deleteSetLog(@PathVariable Long id, @PathVariable Long exerciseId, @PathVariable Long setId)
      throws DataAccessException, InvalidRequestException {
    var currentUser = securityService.getCurrentUser();
    workoutLogService.deleteSetLog(currentUser.getUid(), id, exerciseId, setId);
    return "CHECK DATABASE";
  }
}