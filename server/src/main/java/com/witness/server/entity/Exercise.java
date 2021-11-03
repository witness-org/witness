package com.witness.server.entity;

import com.witness.server.enumeration.LoggingType;
import com.witness.server.enumeration.MuscleGroup;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
