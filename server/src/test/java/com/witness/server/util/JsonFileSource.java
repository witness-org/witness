package com.witness.server.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * References a test method argument that ought to be deserialized from JSON, based on a class path. Is meant to be used as argument for the
 * {@link JsonFileSources#parameters()} property.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFileSource {

  /**
   * The path to the class path resource representing the JSON file source.
   */
  String value();

  /**
   * The type to deserialize the contents of {@link JsonFileSource#value()} into.
   *
   * @return {@link JsonFileSource#value()}'s target type
   */
  Class<?> type() default Object.class;

  /**
   * If {@link JsonFileSource#type()} is an array type, this property determines whether the array represented by the JSON file pointed to by
   * {@link JsonFileSource#value()} should be passed to the test method as a {@link List} type instead of array. If {@link JsonFileSource#type()} is
   * not an array type, the value of this property is ignored.
   *
   * @return {@code true} if, under the condiction that {@link JsonFileSource#type()} is an array type, the JSON file represented by this
   *     {@link JsonFileSource} should be passed as a list instead of an array of {@link JsonFileSource#type()} objects to the respective test method.
   */
  boolean arrayToList() default false;
}
