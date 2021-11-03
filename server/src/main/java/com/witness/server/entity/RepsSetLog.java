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
@Table(name = "reps_set_log")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RepsSetLog extends SetLog {

  @Builder(toBuilder = true)
  public RepsSetLog(Integer position, Integer rpe, Long id, Long weightKg, List<ResistanceBand> resistanceBands, ExerciseLog exerciseLog,
                    Integer reps) {
    super(position, rpe, id, weightKg, resistanceBands, exerciseLog);
    this.reps = reps;
  }

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
