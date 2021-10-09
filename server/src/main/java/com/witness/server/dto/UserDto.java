package com.witness.server.dto;

import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.Sex;
import com.witness.server.validation.EmailStrict;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserDto {
  @NotNull
  private Long id;

  @NotBlank
  private String firebaseId;

  @NotBlank
  @Length(min = 3, max = 256)
  private String username;

  @EmailStrict
  @NotBlank
  @Length(min = 3, max = 256)
  private String email;

  private Role role;

  @NotNull
  private Sex sex;

  @PastOrPresent
  @NotNull
  private ZonedDateTime createdAt;

  @PastOrPresent
  @NotNull
  private ZonedDateTime modifiedAt;

  @NotNull
  private Long height;
}
