package com.witness.server.entity.workout;

import com.witness.server.entity.user.User;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "workout_log")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkoutLog {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workout_log_id_generator")
  @SequenceGenerator(name = "workout_log_id_generator", sequenceName = "workout_log_id_sequence")
  @Column(name = "id", nullable = false)
  @NotNull
  private Long id;

  @ManyToOne(targetEntity = User.class)
  @JoinColumn(name = "logged_by_id", nullable = false)
  @NotNull
  private User user;

  @Column(name = "logged_on", nullable = false)
  @NotNull
  private ZonedDateTime loggedOn;

  @Column(name = "duration_minutes", nullable = false)
  @NotNull
  @PositiveOrZero
  private Integer durationMinutes;

  @OneToMany(targetEntity = ExerciseLog.class, mappedBy = "workoutLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @NotNull
  @Setter(AccessLevel.NONE)
  @Builder.Default
  @ToString.Exclude
  private List<ExerciseLog> exerciseLogs = new ArrayList<>();

  public void addExerciseLog(ExerciseLog exerciseLog) {
    exerciseLogs.add(exerciseLog);
    exerciseLog.setWorkoutLog(this);
  }

  public boolean removeExerciseLog(ExerciseLog exerciseLog) {
    if (exerciseLogs.remove(exerciseLog)) {
      exerciseLog.setWorkoutLog(null);
      return true;
    }

    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var that = (WorkoutLog) o;
    return Objects.equals(id, that.id) && Objects.equals(loggedOn, that.loggedOn) && Objects.equals(durationMinutes, that.durationMinutes)
        && Objects.equals(exerciseLogs, that.exerciseLogs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, loggedOn, durationMinutes, exerciseLogs);
  }
}
