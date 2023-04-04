package com.witness.server.util.isolation.impl;

import com.witness.server.util.isolation.DatabaseResetService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of {@link DatabaseResetService} which is specific to the H2 database engine.
 */
@Service
@Slf4j
@Qualifier("H2")
public class H2DatabaseResetService implements DatabaseResetService {
  @Autowired
  private EntityManager entityManager;

  /**
   * Resets the H2 database in order to provide a clean starting state for a test case execution. This method follows a four-step plan:
   * <ol>
   *   <li>Turn off referential integrity checks in order to freely modify the data.</li>
   *   <li>Truncate all tables, i.e. clear data from all tables.</li>
   *   <li>Reset all sequences, i.e. set the value of all sequences to 1.</li>
   *   <li>Turn referential integrity checks back on.</li>
   * </ol>
   */
  @Transactional
  public void resetDatabase() {
    log.debug("Disabling referential integrity.");
    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

    truncateTables();
    resetSequences();

    log.debug("Re-enabling referential integrity.");
    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
  }

  private void truncateTables() {
    log.debug("Truncating tables.");
    var tables = entityManager
        .createNativeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'")
        .getResultList();

    for (var tableName : tables) {
      log.trace("Truncating table '{}'", tableName);
      entityManager
          .createNativeQuery("TRUNCATE TABLE " + tableName.toString())
          .executeUpdate();
    }
  }

  private void resetSequences() {
    log.debug("Resetting sequences.");
    var sequences = entityManager
        .createNativeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'")
        .getResultList();

    for (var sequenceName : sequences) {
      log.trace("Resetting sequence '{}'", sequenceName);
      entityManager
          .createNativeQuery("ALTER SEQUENCE " + sequenceName.toString() + " RESTART WITH 1")
          .executeUpdate();
    }
  }

}
