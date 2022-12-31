package com.witness.server.entity.user;

import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.Sex;
import com.witness.server.validation.EmailStrict;
import java.time.ZonedDateTime;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
  @SequenceGenerator(name = "user_id_generator", sequenceName = "user_id_sequence")
  @Column(name = "id", nullable = false)
  @NotNull
  private Long id;

  @Column(name = "firebase_id", nullable = false, unique = true)
  @NotBlank
  private String firebaseId;

  @Column(name = "username", nullable = false, length = 256, unique = true)
  @NotBlank
  @Length(min = 3, max = 256)
  private String username;

  @Column(name = "email", nullable = false, length = 256, unique = true)
  @EmailStrict
  @NotBlank
  @Length(min = 3, max = 256)
  private String email;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(name = "sex", nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Sex sex;

  @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  @PastOrPresent
  @NotNull
  private ZonedDateTime createdAt;

  @Column(name = "modified_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  @PastOrPresent
  @NotNull
  private ZonedDateTime modifiedAt;

  @Column(name = "height", nullable = false)
  @NotNull
  @Positive
  private Long height;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var user = (User) o;
    return Objects.equals(id, user.id) && Objects.equals(firebaseId, user.firebaseId)
        && Objects.equals(username, user.username) && Objects.equals(email, user.email) && role == user.role && sex == user.sex
        && Objects.equals(createdAt, user.createdAt) && Objects.equals(modifiedAt, user.modifiedAt)
        && Objects.equals(height, user.height);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firebaseId, username, email, role, sex, createdAt, modifiedAt, height);
  }
}
