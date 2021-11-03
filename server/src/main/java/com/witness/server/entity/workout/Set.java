package com.witness.server.entity.workout;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
public abstract class Set {

  @Column(name = "position", nullable = false)
  @NotNull
  @Min(1)
  protected Integer position;

  @Column(name = "rpe")
  @Min(0)
  @Max(10)
  protected Integer rpe;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var set = (Set) o;
    return Objects.equals(position, set.position) && Objects.equals(rpe, set.rpe);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, rpe);
  }
}
