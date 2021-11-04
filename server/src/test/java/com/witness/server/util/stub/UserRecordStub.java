package com.witness.server.util.stub;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A JSON-deserializable representation of {@link com.google.firebase.auth.UserRecord} for test purposes. The original class does not provide a
 * default constructor and is not easily extensible via inheritance due do its base constructor that requires complex arguments, making it
 * non-eligible for default deserialization using Jackson.
 */
@Data
@NoArgsConstructor
public class UserRecordStub {
  private String uid;
  private String displayName;
  private String email;
  private boolean emailVerified;
  private String photoUrl;
}
