package com.witness.server.dto;

import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.Sex;
import com.witness.server.validation.EmailStrict;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserCreateDto {
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

  @NotNull
  private Long height;

  @NotBlank
  @Length(min = 6)
  private String password;
}
