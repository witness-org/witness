package com.witness.server.entity.exercise;

import com.witness.server.enumeration.LoggingType;
import com.witness.server.enumeration.MuscleGroup;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "exercise")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Exercise {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exercise_id_generator")
  @SequenceGenerator(name = "exercise_id_generator", sequenceName = "exercise_id_sequence")
  @Column(name = "id", nullable = false)
  @NotNull
  private Long id;

  @Column(name = "name", nullable = false, length = 256)
  @NotBlank
  @Length(min = 1, max = 256)
  private String name;

  @Column(name = "description", length = 1024)
  private String description;

  @Enumerated(EnumType.STRING)
  @ElementCollection(targetClass = MuscleGroup.class)
  @NotNull
  @NotEmpty
  private List<MuscleGroup> muscleGroups;

  @Enumerated(EnumType.STRING)
  @ElementCollection(targetClass = LoggingType.class)
  @NotNull
  @NotEmpty
  private List<LoggingType> loggingTypes;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var exercise = (Exercise) o;
    return Objects.equals(id, exercise.id) && Objects.equals(name, exercise.name)
        && Objects.equals(description, exercise.description) && Objects.equals(muscleGroups, exercise.muscleGroups)
        && Objects.equals(loggingTypes, exercise.loggingTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, muscleGroups, loggingTypes);
  }
}
