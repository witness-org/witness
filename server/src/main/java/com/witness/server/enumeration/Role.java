package com.witness.server.enumeration;

import java.util.Arrays;

public enum Role {
  ADMIN("ROLE_ADMIN"),
  PREMIUM("ROLE_PREMIUM");

  private final String identifier;

  Role(String identifier) {
    this.identifier = identifier;
  }

  public String identifier() {
    return identifier;
  }

  /**
   * Creates a {@link Role} from a {@link Role#identifier()} string.
   *
   * @param identifier the identifier
   * @return a {@link Role} that corresponds to the identifier {@code identifier}
   */
  public static Role fromIdentifier(String identifier) {
    return Arrays.stream(values())
        .filter(r -> r.identifier().equals(identifier))
        .findFirst()
        .orElse(null);
  }
}
