package com.witness.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representation of data about a Firebase user, represents the default claims of a Firebase JWT.
 */
@Data
@Schema(description = "Represents a user registered in Firebase.")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseUser implements Serializable {
  @Serial
  private static final long serialVersionUID = 576_965_975_003_360_774L;

  @Schema(description = "The Firebase ID of the user.", example = "aXusKTIbLYSKc8wnQJeOz8c3JsT2")
  private String uid;

  @Schema(description = "The name of the user.", example = "user123")
  private String name;

  @Schema(description = "The email address of the user.", example = "user123@example.com")
  private String email;

  @Schema(description = "An indication whether the email address of the user has been verified.", example = "true")
  private boolean isEmailVerified;

  @Schema(description = "The issuer of the user.", example = "issuer")
  private String issuer;

  @Schema(description = "A picture of the user.", example = "xxx")
  private String picture;
}
