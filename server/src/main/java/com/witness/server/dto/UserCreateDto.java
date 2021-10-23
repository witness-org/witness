package com.witness.server.dto;

import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.Sex;
import com.witness.server.validation.EmailStrict;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "Represents a user create request.")
public class UserCreateDto {
  @NotBlank
  @Length(min = 3, max = 256)
  @Schema(description = "The username of the user.", example = "user123")
  private String username;

  @EmailStrict
  @NotBlank
  @Length(min = 3, max = 256)
  @Schema(description = "The email address of the user.", example = "user123@example.com")
  private String email;

  @Schema(description = "The role of the user.", example = "PREMIUM")
  private Role role;

  @NotNull
  @Schema(description = "The sex of the user.", example = "FEMALE")
  private Sex sex;

  @NotNull
  @Schema(description = "The height of the user in cm.", example = "183")
  private Long height;

  @NotBlank
  @Length(min = 6)
  @Schema(description = "The password for the user.", example = "superSecurePassword")
  private String password;
}
