package com.witness.server.entity.exercise;

import com.witness.server.entity.user.User;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_exercise")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
public class UserExercise extends Exercise {

  @ManyToOne(targetEntity = User.class)
  @JoinColumn(name = "created_by_id", nullable = false)
  @NotNull
  private User createdBy;

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
