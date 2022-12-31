package com.witness.server.dto.user;

import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.Sex;
import com.witness.server.validation.EmailStrict;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "Represents a persisted user.")
public class UserDto {
  @NotNull
  @Schema(description = "The ID of the user.", example = "1")
  private Long id;

  @NotBlank
  @Schema(description = "The Firebase ID of the user.", example = "aXusKTIbLYSKc8wnQJeOz8c3JsT2")
  private String firebaseId;

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

  @PastOrPresent
  @NotNull
  @Schema(description = "The date and time the user was created at.", example = "2021-10-08T14:15:55.3007597+02:00")
  private ZonedDateTime createdAt;

  @PastOrPresent
  @NotNull
  @Schema(description = "The date and time the user was last modified at.", example = "2021-10-08T14:15:55.3007597+02:00")
  private ZonedDateTime modifiedAt;

  @NotNull
  @Schema(description = "The height of the user in cm.", example = "183")
  private Long height;
}
