package com.witness.server.entity;

import com.witness.server.enumeration.LoggingType;
import com.witness.server.enumeration.MuscleGroup;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class UserExercise extends Exercise {

  @ManyToOne
  @NotNull
  private User createdBy;

  @Builder(builderMethodName = "userExerciseBuilder")
  public UserExercise(Long id, String name, String description, List<MuscleGroup> muscleGroups, List<LoggingType> loggingTypes, User createdBy) {
    super(id, name, description, muscleGroups, loggingTypes);
    this.createdBy = createdBy;
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
    var that = (UserExercise) o;
    return Objects.equals(createdBy, that.createdBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), createdBy);
  }
}
