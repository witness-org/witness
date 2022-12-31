package com.witness.server.entity.workout;

import com.witness.server.entity.exercise.Exercise;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
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
  @ToString.Exclude
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
