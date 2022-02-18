package com.witness.server.repository;

import com.witness.server.entity.workout.SetLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetLogRepository extends JpaRepository<SetLog, Long> {

  List<SetLog> findSetLogsByExerciseLogExerciseIdEqualsAndExerciseLogWorkoutLogUserIdEquals(Long exerciseId, Long userId);
}
