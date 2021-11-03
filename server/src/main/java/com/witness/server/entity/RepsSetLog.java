package com.witness.server.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
@ToString
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
