package com.witness.server.repository;

import com.witness.server.entity.workout.WorkoutLog;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
  List<WorkoutLog> findByLoggedOnBetweenAndUserFirebaseIdEquals(ZonedDateTime loggedOnStart, ZonedDateTime loggedOnEnd, String firebaseId);
}
