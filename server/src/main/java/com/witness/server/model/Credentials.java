package com.witness.server.model;

import com.google.firebase.auth.FirebaseToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Provides access to both the text representation of a Firebase ID token (JWT) and its decoded representation as {@link FirebaseToken} instance.
 */
@Data
@AllArgsConstructor
@Schema(description = "Represents the text representation of a Firebase ID token and its decoded representation.")
public class Credentials {
  @Schema(description = "The decoded token.")
  private FirebaseToken decodedToken;

  @Schema(description = "The Firebase ID token.",
      example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ")
  private String idToken;
}
