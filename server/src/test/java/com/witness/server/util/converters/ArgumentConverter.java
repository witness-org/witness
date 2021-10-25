package com.witness.server.util.converters;

import com.witness.server.util.JsonFileArgumentsProvider;
import com.witness.server.util.JsonFileSource;

/**
 * <p>
 * Defines the contract for types to be used as converters in conjunction with the {@link JsonFileSource} annotation. Some types might not be easily
 * deserializable via Jackson, e.g. there is no default constructor, the classes are final (i.e. not extensible via inheritance) or not directly
 * compatible. It might be infeasible to change the respective classes (due to domain constraints or since they stem from an external library).
 * </p>
 * <p>
 * This type provides an extension point to still be able to deserialize instances of such classes for test purposes. By creating or reusing
 * intermediate objects (e.g. manually created types, or stubs created with Mockito, which is also able to stub final classes),
 * {@link JsonFileArgumentsProvider} is able to deserialize such an intermediate object and internally convert it to an object of the target type via
 * an {@link ArgumentConverter} implementation. This abstracts the complexities of stubbing otherwise incompatible test objects away from the
 * developer writing test with such a need.
 * </p>
 * <p>
 * <b>Note for implementers:</b> An implementation of {@link ArgumentConverter} must provide a default constructor without arguments. Otherwise,
 * {@link JsonFileArgumentsProvider} is not able to instantiate the converter.
 * </p>
 *
 * @param <T> the target type of the object (typically value of the {@link JsonFileSource#type()})
 * @param <U> the source type of the intermediate object
 */
public abstract class ArgumentConverter<T, U> {
  /**
   * Converts an intermediate object to an instance of the target type.
   *
   * @param intermediate the intermediate object, must be of the class denoted by {@link ArgumentConverter#intermediateClass()} (type {@code U}).
   * @return an instance of the class denoted by {@link ArgumentConverter#targetClass()} (type {@code T}) that corresponds to its alternative
   *     representation given by {@code intermediate}.
   * @throws ClassCastException if {@code intermediate} is not an instance {@link ArgumentConverter#intermediateClass()}, i.e. if the cast
   *                            {@code (U)intermediate} fails
   */
  @SuppressWarnings("unchecked") // Due to the generic way this method is used and Java's type erasure, there is no better way. See documentation.
  public T toConcreteInstance(Object intermediate) throws ClassCastException {
    return toConcreteInstanceInternal((U) intermediate);
  }

  /**
   * Actual, package-private, implementation of {@link ArgumentConverter#toConcreteInstance(Object)} with type-safe parameter.
   *
   * @param intermediate the intermediate object to convert
   * @return the resulting instance of the target type
   */
  abstract T toConcreteInstanceInternal(U intermediate);

  /**
   * Represents the class of intermediate objects consumed by this {@link ArgumentConverter} instance.
   *
   * @return the class corresponding to the generic type parameter {@code U}.
   */
  public abstract Class<U> intermediateClass();

  /**
   * Represents the target class of objects created by this {@link ArgumentConverter} instance.
   *
   * @return the class corresponding to the generic type parameter {@code T}.
   */
  public abstract Class<T> targetClass();
}