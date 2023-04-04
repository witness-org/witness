package com.witness.server.unit.repository;

import com.witness.server.unit.BaseUnitTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
abstract class BaseRepositoryTest extends BaseUnitTest {
  @PersistenceContext
  protected EntityManager entityManager;

  /**
   * Sets a database sequence to a given value.
   *
   * @param value        new value of sequence
   * @param sequenceName sequence whose value should be changed
   */
  protected void setSequenceValue(int value, String sequenceName) {
    entityManager.createNativeQuery("ALTER SEQUENCE %s RESTART WITH %d".formatted(sequenceName, value)).executeUpdate();
  }

  /**
   * Sets multiple database sequences to a given value.
   *
   * @param value         new value of the sequences
   * @param sequenceNames sequences whose values should be changed
   */
  protected void setSequenceValues(int value, String... sequenceNames) {
    for (var sequenceName : sequenceNames) {
      setSequenceValue(value, sequenceName);
    }
  }
}
