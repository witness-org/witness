package com.witness.server.entity;

import com.witness.server.enumeration.ResistanceBand;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "time_set_log")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TimeSetLog extends SetLog {

  @Builder(toBuilder = true)
  public TimeSetLog(Integer position, Integer rpe, Long id, Long weightKg, List<ResistanceBand> resistanceBands, ExerciseLog exerciseLog,
                    Long seconds) {
    super(position, rpe, id, weightKg, resistanceBands, exerciseLog);
    this.seconds = seconds;
  }

  @Column(name = "seconds", nullable = false)
  @NotNull
  @Min(1)
  private Long seconds;

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
