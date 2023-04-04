package com.witness.server.entity.workout;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "exercise_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
public class ExerciseLog extends ExerciseReference {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exercise_log_id_generator")
  @SequenceGenerator(name = "exercise_log_id_generator", sequenceName = "exercise_log_id_sequence")
  @Column(name = "id", nullable = false)
  @NotNull
  private Long id;

  @Column(name = "comment")
  @Length(max = 256)
  private String comment;

  @ManyToOne(targetEntity = WorkoutLog.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "workout_log", nullable = false)
  @NotNull
  @ToString.Exclude
  private WorkoutLog workoutLog;

  @OneToMany(targetEntity = SetLog.class, mappedBy = "exerciseLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @Setter(AccessLevel.NONE)
  @NotNull
  @Builder.Default
  @ToString.Exclude
  private List<SetLog> setLogs = new ArrayList<>();

  public void addSetLog(SetLog setLog) {
    setLogs.add(setLog);
    setLog.setExerciseLog(this);
  }

  public void addSetLog(int index, SetLog setLog) {
    setLogs.add(index, setLog);
    setLog.setExerciseLog(this);
  }

  public boolean removeSetLog(SetLog setLog) {
    if (setLogs.remove(setLog)) {
      setLog.setExerciseLog(null);
      return true;
    }

    return false;
  }

  public boolean removeSetLog(int index) {
    return removeSetLog(setLogs.get(index));
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
}
