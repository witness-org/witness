package com.witness.server.repository;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.exercise.UserExercise;
import com.witness.server.entity.user.User;
import com.witness.server.enumeration.MuscleGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

  /**
   * Tries to find an initial exercise, i.e. an {@link Exercise} object which was not created by a user.
   *
   * @param id ID of the exercise to find
   * @return the {@link Exercise} with the ID {@code id} or {@link Optional#empty()} if none found. Will never be a {@link UserExercise} instance.
   */
  @Query("""
        SELECT
          e
        FROM
          Exercise e
        WHERE
          e.id = :id AND NOT EXISTS (SELECT 1 FROM UserExercise ue WHERE ue.id = :id)
      """)
  Optional<Exercise> findInitialExerciseById(Long id);

  /**
   * Queries all initial exercises (i.e. Exercise objects) and user exercises (i.e. UserExercise objects) created by the given user which train a
   * given muscle group.
   *
   * @param user        user that created the user exercises
   * @param muscleGroup that should be trained with the queried exercises
   * @return list of Exercise objects that fulfill the given criteria
   */
  @Query("""
      SELECT
        e
      FROM
        Exercise e
      LEFT OUTER JOIN
        UserExercise u ON e.id = u.id
      WHERE
        (u.createdBy = :user OR u.createdBy IS NULL) AND :muscleGroup MEMBER OF e.muscleGroups
      """)
  List<Exercise> findAllForUser(User user, MuscleGroup muscleGroup);

  /**
   * Queries all user exercises (i.e. UserExercise objects) that were created for a given user.
   *
   * @param user user that created the user exercises
   * @return list of Exercise objects that fulfill the given criteria
   */
  @Query("""
      SELECT
        e
      FROM
        Exercise e
      JOIN
        UserExercise u ON e.id = u.id
      WHERE u.createdBy = :user
      """)
  List<Exercise> findAllByUser(User user);

  /**
   * Checks whether there exists a initial exercise (i.e. Exercise object) with the given name.
   *
   * @param name name of the exercise
   * @return true if an initial exercise with the given {@code name} exists, otherwise false
   */
  @Query("""
      SELECT
        CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
      FROM
        Exercise e
      LEFT OUTER JOIN
        UserExercise u ON e.id = u.id
      WHERE
        e.name = :name AND u.createdBy IS NULL
      """)
  boolean existsByName(String name);
}
