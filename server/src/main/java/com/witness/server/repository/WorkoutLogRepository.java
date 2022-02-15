package com.witness.server.repository;

import com.witness.server.entity.workout.WorkoutLog;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
  /**
   * Queries all workout logs that logged between a given start date and a given end date by the user with the provided Firebase ID.
   *
   * @param loggedOnStart start date of the period that the resulting workout logs should be logged in
   * @param loggedOnEnd   end date of the period that the resulting workout logs should be logged in
   * @param firebaseId    Firebase ID of the user that logged the resulting workout logs
   * @return list of {@link WorkoutLog} instances logged between {@code loggedOnStart} and {@code loggedOnEnd} by {@code firebaseId}
   */
  List<WorkoutLog> findByLoggedOnBetweenAndUserFirebaseIdEquals(ZonedDateTime loggedOnStart, ZonedDateTime loggedOnEnd, String firebaseId);

  /**
   * Queries all workout logs that contain at least one exercise log and were logged between a given start date and a given end date by the user with
   * the provided Firebase ID.
   *
   * @param loggedOnStart start date of the period that the resulting workout logs should be logged in
   * @param loggedOnEnd end date of the period that the resulting workout logs should be logged in
   * @param firebaseId Firebase ID of the user that logged the resulting workout logs
   * @return list of {@link WorkoutLog} instances that fulfill the given criteria
   */
  @Query("""
        SELECT
          w
        FROM
          WorkoutLog w
        WHERE
          w.exerciseLogs.size > 0 AND w.loggedOn >= :loggedOnStart AND w.loggedOn <= :loggedOnEnd AND w.user.firebaseId = :firebaseId
      """)
  List<WorkoutLog> findNonEmptyByLoggedOnBetweenAndUserFirebaseIdEquals(ZonedDateTime loggedOnStart, ZonedDateTime loggedOnEnd, String firebaseId);
}
