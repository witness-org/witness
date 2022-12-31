package com.witness.server.entity.workout;

import com.witness.server.enumeration.ResistanceBand;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
public abstract class SetLog extends Set {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "set_log_id_generator")
  @SequenceGenerator(name = "set_log_id_generator", sequenceName = "set_log_id_sequence")
  @Column(name = "id", nullable = false)
  @NotNull
  protected Long id;

  @Column(name = "weight_g", nullable = false)
  @NotNull
  protected Long weightG;

  @Enumerated(EnumType.STRING)
  @ElementCollection(targetClass = ResistanceBand.class)
  @Builder.Default
  @Setter(AccessLevel.NONE)
  @NotNull
  protected List<ResistanceBand> resistanceBands = new ArrayList<>();

  @ManyToOne(targetEntity = ExerciseLog.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "exercise_log", nullable = false)
  @NotNull
  @ToString.Exclude
  protected ExerciseLog exerciseLog;

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
    var setLog = (SetLog) o;
    return Objects.equals(id, setLog.id) && Objects.equals(weightG, setLog.weightG) && Objects.equals(resistanceBands, setLog.resistanceBands)
        && Objects.equals(exerciseLog, setLog.exerciseLog);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, weightG, resistanceBands, exerciseLog);
  }
}
