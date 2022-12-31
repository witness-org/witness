package com.witness.server.entity.workout;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "time_set_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
public class TimeSetLog extends SetLog {

  @Column(name = "seconds", nullable = false)
  @NotNull
  @Min(1)
  private Integer seconds;

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
    var that = (TimeSetLog) o;
    return Objects.equals(seconds, that.seconds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), seconds);
  }
}
