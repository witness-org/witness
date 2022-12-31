package com.witness.server.entity.workout;

import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "reps_set_log")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class RepsSetLog extends SetLog {
  
  @Column(name = "reps", nullable = false)
  @NotNull
  @Min(1)
  private Integer reps;

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
    var that = (RepsSetLog) o;
    return Objects.equals(reps, that.reps);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), reps);
  }
}
