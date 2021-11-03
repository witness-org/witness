package com.witness.server.entity;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workout_log")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutLog {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workout_log_id_generator")
  @SequenceGenerator(name = "workout_log_id_generator", sequenceName = "workout_log_id_sequence")
  @Column(name = "id", nullable = false)
  @NotNull
  private Long id;

  @ManyToOne
  @JoinColumn(name = "logged_by_id", nullable = false)
  @NotNull
  private User user;

  @Column(name = "logged_on", nullable = false)
  @NotNull
  private ZonedDateTime loggedOn;

  @Column(name = "duration")
  private Integer durationMinutes;

  @OneToMany(targetEntity = ExerciseLog.class, mappedBy = "workoutLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @NotNull
  @Setter(AccessLevel.NONE)
  @Builder.Default
  private List<ExerciseLog> exerciseLogs = new ArrayList<>();

  public boolean addExerciseLog(ExerciseLog exerciseLog) {
    return exerciseLogs.add(exerciseLog);
  }

  public boolean removeExerciseLog(ExerciseLog exerciseLog) {
    return exerciseLogs.remove(exerciseLog);
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

  @Override
  public String toString() {
    return "WorkoutLog{"
        + "id=" + id
        + ", loggedOn=" + loggedOn
        + ", durationMinutes=" + durationMinutes
        + ", exerciseLogs=" + exerciseLogs
        + '}';
  }
}
