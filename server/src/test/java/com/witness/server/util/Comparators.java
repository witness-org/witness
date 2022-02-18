package com.witness.server.util;

import com.witness.server.web.controller.WorkoutLogController;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
   * This comparator is specific to tests of the {@link WorkoutLogController#getLoggingDays(ZonedDateTime, ZonedDateTime)} endpoint method. Owing to
   * its return type of {@link Map} with {@link ZonedDateTime} as keys, asserting the equality of maps returned by the production code to expected
   * maps from test data sources is a bit difficult, especially due to potentially diverging timezone offsets.
   * </p>
   *
   * <p>
   * This comparator solves this problem by completely disregarding the timezone information and comparing corresponding {@link LocalDate} instances
   * only. This is acceptable since logging days are specific to days by definition and therefore, timezone information does not matter anyway. Edge
   * test cases where the timezone offset of a workout that was logged around midnight changes the day depending on the client's location might need
   * special treatment, but are not covered by automated tests at the moment.
   * </p>
   *
   * <p>
   * It first compares the keys of a {@link Map.Entry}, i.e. the {@link ZonedDateTime} instances (based on their {@link LocalDate}s). If they are
   * not considered equal, the {@link LocalDate} comparator's result is returned. If they are equal, the result of comparing the {@link Integer}
   * values of the {@link Map.Entry} is returned.
   * </p>
   */
  public static Comparator<Map.Entry<ZonedDateTime, Integer>> LOGGING_DAY_COMPARATOR = (o1, o2) -> {
    var keysComparison = Comparator.comparing(ZonedDateTime::toLocalDate).compare(o1.getKey(), o2.getKey());
    return keysComparison != 0 ? keysComparison : Integer.compare(o1.getValue(), o2.getValue());
  };

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
   * instances with the same elements.
   *
   * @param instance the instance that should be inspected and potentially altered
   * @param clazz    the class of {@code instance}
   * @param <T>      the type of {@code instance}
   */
  @SuppressWarnings({"unchecked", "rawtypes"}) // due to Java's type erasure, there is no better way (generics information is gone at runtime)
  private static <T> void replacePersistentBags(T instance, Class<T> clazz) {
    if (instance == null) {
      return;
    }

    ReflectionUtils.doWithFields(clazz, field -> {
      ReflectionUtils.makeAccessible(field);
      var value = field.get(instance);
      if (field.getType().equals(List.class) && value instanceof PersistentBag) {
        field.set(instance, new ArrayList<>((PersistentBag) value));
      }
    });
  }
}
