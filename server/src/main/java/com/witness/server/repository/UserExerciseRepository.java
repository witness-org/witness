package com.witness.server.repository;

import com.witness.server.entity.User;
import com.witness.server.entity.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {
  boolean existsByNameAndCreatedBy(String name, User createdBy);
}
