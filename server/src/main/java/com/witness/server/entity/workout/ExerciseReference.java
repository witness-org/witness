package com.witness.server.entity.workout;

import com.witness.server.entity.exercise.Exercise;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString
public abstract class ExerciseReference {
  
  @Column(name = "position", nullable = false)
  @NotNull
  @Min(1)
  protected Integer position;

  @ManyToOne(targetEntity = Exercise.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "exercise_id", nullable = false)
  @NotNull
  protected Exercise exercise;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var that = (ExerciseReference) o;
    return Objects.equals(position, that.position) && Objects.equals(exercise, that.exercise);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, exercise);
  }
}
