package com.witness.server.repository;

import com.witness.server.entity.user.User;
import com.witness.server.entity.exercise.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {
  boolean existsByNameAndCreatedBy(String name, User createdBy);
}
