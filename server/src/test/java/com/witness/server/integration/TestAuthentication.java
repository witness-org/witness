package com.witness.server.integration;

import com.witness.server.enumeration.Role;

/**
 * Determines which level of authentication for an integration test should be used.
 */
public enum TestAuthentication {
  /**
   * The test will be conducted as a user with role {@link Role#ADMIN}.
   */
  ADMIN,

  /**
   * The test will be conducted as a user with role {@link Role#PREMIUM}.
   */
  PREMIUM,

  /**
   * The test will be conducted as a regular user.
   */
  REGULAR,

  /**
   * The test will be conducted as if no authentication (token) had been provided.
   */
  NONE
}
