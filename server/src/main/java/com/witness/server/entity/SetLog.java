package com.witness.server.entity;

import com.witness.server.enumeration.ResistanceBand;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
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

  @Column(name = "weight", nullable = false)
  @NotNull
  protected Long weightKg;

  @Enumerated(EnumType.STRING)
  @ElementCollection(targetClass = ResistanceBand.class)
  @Builder.Default
  @NotNull
  protected List<ResistanceBand> resistanceBands = new ArrayList<>();

  @ManyToOne(targetEntity = ExerciseLog.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "exercise_log", nullable = false)
  @NotNull
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
    return Objects.equals(id, setLog.id) && Objects.equals(weightKg, setLog.weightKg) && Objects.equals(resistanceBands, setLog.resistanceBands)
        && Objects.equals(exerciseLog, setLog.exerciseLog);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, weightKg, resistanceBands, exerciseLog);
  }
}
