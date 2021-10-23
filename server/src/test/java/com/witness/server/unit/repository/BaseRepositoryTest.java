package com.witness.server.unit.repository;

import com.witness.server.unit.BaseUnitTest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.collection.internal.PersistentBag;
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
    for (String sequenceName : sequenceNames) {
      setSequenceValue(value, sequenceName);
    }
  }

  /**
   * <p>
   * Hibernate's {@link org.hibernate.collection.internal.PersistentBag} breaks the {@link List} contract in its {@code equals} implementation.
   * * Therefore, assertions that verify equality of database objects with manually created objects fail if they have {@link List} members.
   * * This problem is avoided by replaces the value of {@link List} members with {@link ArrayList} instances.
   * </p>
   *
   * <p>
   * This intrusive {@link Comparator} instance dynamically inspects the given element via reflection and replaces {@link PersistentBag} instances
   * with {@link ArrayList} instance if their static type is {@link List}.
   * </p>
   *
   * @param clazz the class of elements compared by this comparator
   * @param <T>   the type of elements compared by this comparator
   * @return an intrusive comparator that replaced fields with static type {@link List} and dynamic type {@link PersistentBag} with instances of
   *     {@link ArrayList}.
   */
  protected static <T> Comparator<T> getEntityComparator(Class<T> clazz) {
    return (entity1, entity2) -> {
      replacePersistentBags(entity1, clazz);
      replacePersistentBags(entity2, clazz);

      if (entity1 instanceof Comparable && entity2 instanceof Comparable) {
        //noinspection rawtypes,unchecked (due to Java's type erasure, there is no better way -> generics information is gone at runtime)
        return ((Comparable) entity1).compareTo(entity2);
      }

      // If the elements are not comparable, then there is no sensible implementation that respects the Comparable contract. Therefore,
      // return 0 if the elements are equal, 1 if they are not equal and -1 if the equals-implementation is not symmetric.
      if (entity1.equals(entity2)) {
        return entity2.equals(entity1) ? 0 : -1;
      } else {
        return entity2.equals(entity1) ? -1 : 1;
      }
    };
  }

  /**
   * Replaces field instances with static type {@link List} and dynamic type {@link PersistentBag} in a given instance by {@link ArrayList}
   * instances with the same elements.
   *
   * @param instance the instance that should be inspected and potentially altered
   * @param clazz    the class of {@code instance}
   * @param <T>      the type of {@code instance}
   */
  private static <T> void replacePersistentBags(T instance, Class<T> clazz) {
    if (instance == null) {
      return;
    }

    try {
      for (var field : getAllFields(clazz)) {
        field.setAccessible(true);
        var value = field.get(instance);
        if (field.getType().equals(List.class) && value instanceof PersistentBag) {
          //noinspection rawtypes,unchecked (due to Java's type erasure, there is no better way -> generics information is gone at runtime)
          field.set(instance, new ArrayList<>((PersistentBag) value));
        }
      }
    } catch (IllegalAccessException e) {
      // cannot set value, ignore exception
      // test could fail due to a violated equals-assertion owing to PersistentBag's breach of contract => needs manual fixing
    }
  }

  /**
   * Retrieves all fields, even private ones, from a type and its super types.
   *
   * @param type the type whose declared fields should be determined
   * @return all fields of {@code type}, even private ones and the ones declared in super types
   */
  private static <T> List<Field> getAllFields(Class<T> type) {
    var fields = new ArrayList<Field>();
    Class<?> clazz = type;
    while (clazz != Object.class) {
      fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    }
    return fields;
  }
}
