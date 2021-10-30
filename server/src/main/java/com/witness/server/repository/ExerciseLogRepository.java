package com.witness.server.repository;

import com.witness.server.entity.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {
}
