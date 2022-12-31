package com.witness.server.repository;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.WorkoutLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {
  /**
   * Queries all {@link ExerciseLog} instances belonging to the specified exercise which were logged by the specified user and contain at least one
   * {@link SetLog}.
   *
   * @param exerciseId the ID of the {@link Exercise} whose logs should be queried
   * @param userId     the ID of the user whose exercise logs should be queried
   * @return a list of {@link ExerciseLog} instances describing logs with at least one set log of the exercise represented by {@code exerciseId} and
   *     logged by the user with ID {@code userId}, in decreasing order of the {@link WorkoutLog#getLoggedOn()} property
   */
  @Query("""
        SELECT
          e
        FROM
          ExerciseLog e
        WHERE
          e.exercise.id = :exerciseId AND e.workoutLog.user.id = :userId AND size(e.setLogs) > 0
        ORDER BY
          e.workoutLog.loggedOn DESC
      """)
  List<ExerciseLog> findExerciseLogsByExerciseIdAndUserId(Long exerciseId, Long userId);
}
