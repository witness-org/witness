package com.witness.server.util.stub;

import com.google.firebase.auth.FirebaseToken;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A JSON-deserializable representation of {@link FirebaseToken} for test purposes. The original class is final and does not provide a default
 * constructor, making it non-eligible for default deserialization using Jackson.
 */
@Data
@NoArgsConstructor
public class FirebaseTokenStub {
  private String uid;
  private String name;
  private String email;
  private boolean emailVerified;
  private String issuer;
  private String picture;
}