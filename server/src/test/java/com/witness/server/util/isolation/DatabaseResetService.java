package com.witness.server.util.isolation;

/**
 * Provides routines for restoring a clean database state, i.e. resetting database objects such as tables and sequences. May be used to conduct
 * cleanup tasks between tests. Implementations are generally vendor-specific. Their documentation must indicate vendors for which they have
 * verified support.
 */
public interface DatabaseResetService {
  /**
   * Resets the database in order to provide a clean starting state for a test case execution. This typically involves clearing the rows of all tables
   * and resetting sequence values. Refer to the implementations' documentation to gain vendor-specific information on the rest process.
   */
  void resetDatabase();
}
