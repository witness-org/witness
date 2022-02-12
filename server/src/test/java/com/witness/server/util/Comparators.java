package com.witness.server.util;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Entity;
import org.hibernate.collection.internal.PersistentBag;
import org.springframework.util.ReflectionUtils;

/**
 * Provides custom comparators to be used for AssertJ assertions.
 */
public final class Comparators {
  private Comparators() {
  }

  /**
   * <p>
   * When comparing time-related information that includes timezones ({@link ZonedDateTime}) between test data, e.g. deserialized from JSON with
   * the {@link JsonFileSource} annotation, and data returned from production code, there are two challenges:
   * </p>
   *
   * <ol>
   *   <li>
   *      The information returned from production code might have different timezone offsets than the static JSON test data.
   *   </li>
   *   <li>
   *     The precision of information returned from production code might be lower (or higher) than the static JSON test data. Typically, databases
   *     store date-time information only up to a specific decimal place.
   *   </li>
   * </ol>
   *
   * <p>
   * This comparator solves both of these problems. Since mocking/overriding (default) timezones used in production might not be the best approach,
   * it compares the equivalent {@link java.time.Instant} representations of the two given {@link ZonedDateTime} objects. That way, we compare
   * the specific point in time, regardless of timezone, e.g. 14 o'clock in Vienna is considered equal to 12 o'clock in UTC time, which is our
   * intention. Furthermore, the {@link java.time.Instant}s are truncated to a precision of {@link ChronoUnit#MILLIS} to avoid assertion failures
   * that stem from lower precision provided by the persistence layer.
   * </p>
   */
  public static Comparator<? super ZonedDateTime> ZONED_DATE_TIME_COMPARATOR =
      Comparator.comparing(o -> o.toInstant().truncatedTo(ChronoUnit.MILLIS));

  /**
   * <p>
   * Hibernate's {@link org.hibernate.collection.internal.PersistentBag} breaks the {@link List} contract in its {@code equals} implementation.
   * Therefore, assertions that verify equality of database objects with manually created objects fail if they have {@link List} members.
   * This problem is avoided by replacing the value of {@link List} members with {@link ArrayList} instances.
   * </p>
   *
   * <p>
   * This intrusive {@link Comparator} instance dynamically inspects the given element via reflection and replaces {@link PersistentBag} instances
   * with {@link ArrayList} instances if their static type is {@link List}.
   * </p>
   *
   * @param clazz the class of elements compared by this comparator
   * @param <T>   the type of elements compared by this comparator
   * @return an intrusive comparator that replaced fields with static type {@link List} and dynamic type {@link PersistentBag} with instances of
   *     {@link ArrayList}.
   */
  @SuppressWarnings({"unchecked", "rawtypes"}) // due to Java's type erasure, there is no better way (generics information is gone at runtime)
  public static <T> Comparator<T> getEntityComparator(Class<T> clazz) {
    return (entity1, entity2) -> {
      replacePersistentBags(entity1, clazz);
      replacePersistentBags(entity2, clazz);

      if (entity1 instanceof Comparable && entity2 instanceof Comparable) {
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
   * instances with the same elements. Recursively does the same for fields which are annotated with {@link Entity}.
   *
   * @param instance the instance that should be inspected and potentially altered
   * @param clazz    the class of {@code instance}
   */
  @SuppressWarnings({"unchecked", "rawtypes"}) // due to Java's type erasure, there is no better way (generics information is gone at runtime)
  private static void replacePersistentBags(Object instance, Class<?> clazz) {
    if (instance == null) {
      return;
    }

    ReflectionUtils.doWithFields(clazz, field -> {
      ReflectionUtils.makeAccessible(field);
      var value = field.get(instance);
      if (field.getType().equals(List.class) && value instanceof PersistentBag) {
        field.set(instance, new ArrayList<>((PersistentBag) value));
      } else if (field.getType().getAnnotation(Entity.class) != null) {
        replacePersistentBags(value, field.getType());
      }
    });
  }
}
