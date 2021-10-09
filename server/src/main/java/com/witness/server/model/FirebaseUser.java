package com.witness.server.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class FirebaseUser implements Serializable {
  @Serial
  private static final long serialVersionUID = 576_965_975_003_360_774L;

  private String uid;
  private String name;
  private String email;
  private boolean isEmailVerified;
  private String issuer;
  private String picture;
}
