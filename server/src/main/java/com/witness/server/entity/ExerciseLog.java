package com.witness.server.entity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercise_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseLog extends ExerciseReference {

  /**
   * Constructs a new {@link ExerciseLog} instance.
   *
   * @param position   exercise log position
   * @param exercise   executed exercise
   * @param id         ID of the exercise log
   * @param comment    comment accompanying the exercise log
   * @param workoutLog reference to the workout log
   * @param setLogs    executed sets
   */
  @Builder(toBuilder = true)
  public ExerciseLog(Integer position, Exercise exercise, Long id, String comment, WorkoutLog workoutLog, List<SetLog> setLogs) {
    super(position, exercise);
    this.id = id;
    this.comment = comment;
    this.workoutLog = workoutLog;
    this.setLogs = setLogs != null ? setLogs : new ArrayList<>();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exercise_log_id_generator")
  @SequenceGenerator(name = "exercise_log_id_generator", sequenceName = "exercise_log_id_sequence")
  @Column(name = "id", nullable = false)
  @NotNull
  private Long id;

  @Column(name = "comment")
  private String comment;

  @ManyToOne(targetEntity = WorkoutLog.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "workout_log", nullable = false)
  @NotNull
  private WorkoutLog workoutLog;

  @OneToMany(targetEntity = SetLog.class, mappedBy = "exerciseLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @NotNull
  private List<SetLog> setLogs = new ArrayList<>();

  /**
   * Adds a {@link SetLog} entry.
   *
   * @param setLog set log entry to add
   * @return true if the operation was successful; otherwise false
   */
  public boolean addSetLog(SetLog setLog) {
    if (setLogs == null) {
      setLogs = new ArrayList<>();
    }

    return setLogs.add(setLog);
  }

  /**
   * Removes a {@link SetLog} entry.
   *
   * @param setLog set log entry to remove
   * @return true if the operation was successful; otherwise false
   */
  public boolean removeSetLog(SetLog setLog) {
    if (setLogs == null) {
      return false;
    }

    return setLogs.remove(setLog);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    var that = (ExerciseLog) o;
    return Objects.equals(id, that.id) && Objects.equals(comment, that.comment) && Objects.equals(workoutLog, that.workoutLog)
        && Objects.equals(setLogs, that.setLogs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, comment, workoutLog, setLogs);
  }

  @Override
  public String toString() {
    return "ExerciseLog{"
        + "id=" + id
        + ", comment='" + comment + '\''
        + ", workoutLog=" + workoutLog
        + ", setLogs=" + setLogs
        + ", position=" + position
        + ", exercise=" + exercise
        + '}';
  }
}
