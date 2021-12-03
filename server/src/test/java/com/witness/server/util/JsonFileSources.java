package com.witness.server.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * Provides the capability of supplying class instances as test parameters which are deserialized from physical JSON files in the class path.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(JsonFileArgumentsProvider.class)
public @interface JsonFileSources {

  /**
   * {@link JsonFileSource} instances, each representing one JSON file, corresponding to one test method argument.
   */
  JsonFileSource[] parameters();

  /**
   * Determines whether arguments represented by {@link JsonFileSources#parameters()} should be unwrapped into separate test executions. For example,
   * if this property is {@code false}, the following test method signature would be valid:
   *
   * <pre>
   * &#64;ParameterizedTest
   * &#64;JsonFileSources(parameters = {
   *   &#64;JsonFileSource(value = "list1.json", type = SomeDto[].class),
   *   &#64;JsonFileSource(value = "list2.json", type = SomeEntity[].class),
   * })
   * void dtoToEntity(SomeDto[] dtos, SomeEntity[] entities) {
   *   for (var i = 0; i < entities.length; i++) {
   *     assertThat(mapper.dtoToEntity(dtos[i])).isEqualTo(entities.get(i));
   *   }
   * }
   * </pre>
   * <p>
   * On the other hand, if it is {@code true}, the very same input files yield the following method signature where each pair of list elements is
   * unwrapped in a parameter pair, representing a separate test execution.
   * </p>
   *
   * <pre>
   * &#64;ParameterizedTest
   * &#64;JsonFileSources(unwrapArrays = true, parameters = {
   *   &#64;JsonFileSource(value = "list1.json", type = SomeDto[].class),
   *   &#64;JsonFileSource(value = "list2.json", type = SomeEntity[].class),
   * })
   * void dtoToEntity(SomeDto dto, SomeEntity entity) {
   *   assertThat(mapper.dtoToEntity(dto)).isEqualTo(entity);
   * }
   * </pre>
   * <p>
   * In order for this feature to be able to work, three conditions have to be satisfied:
   * </p>
   * <ol>
   *   <li>{@code unwrapArrays} must be {@code true}</li>
   *   <li>all arguments must be arrays (i.e. the {@link JsonFileSource#arrayToList()} property must be false for all sources)</li>
   *   <li>all argument arrays must have the same length</li>
   * </ol>
   *
   * @return {@code true} if list arguments should be unwrapped, otherwise {@code false}.
   */
  boolean unwrapArrays() default false;
}
