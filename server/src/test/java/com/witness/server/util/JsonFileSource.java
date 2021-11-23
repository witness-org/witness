package com.witness.server.util;

import com.witness.server.util.converter.ArgumentConverter;
import com.witness.server.util.converter.NoOpArgumentConverter;
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

  /**
   * Provides the option of introducing a "conversion layer" between JSON deserialization and test argument provision. An instance of
   * {@link ArgumentConverter} may be used to convert the object deserialized from JSON to another type before supplying it to the test.
   * <p>
   * For example, {@code ExternalModel} might not be deserializable. Therefore, {@code ExternalModelStub} and {@code ExternalModelStubConverter} were
   * created for test purposes. A test using the converter along with the stub to get an instance of the target type can be found below.
   * </p>
   * <pre>
   *  &#64;ParameterizedTest
   *  &#64;JsonFileSources(parameters = {
   *    &#64;JsonFileSource(value = "ExternalModel1.json", type = ExternalModel.class, stubConverter = ExternalModelStubConverter.class)
   *  })
   *  void dtoToEntity(ExternalModel model) {
   *    // test code
   *  }
   * </pre>
   * <p>
   * The file {@code ExternalModel1.json} holds a representation of an {@code ExternalModelStub} instance. Upon supplying arguments to the test,
   * {@link com.witness.server.util.JsonFileArgumentsProvider} deserializes an {@code ExternalModelStub} instance, converts it to an
   * {@code ExternalModel} instance by invoking {@link ArgumentConverter#toConcreteInstance(Object)} and only then supplies it to the test case.
   * </p>
   * <p>
   * If no custom {@link ArgumentConverter} implementation is specified, {@link NoOpArgumentConverter} is used. It converts nothing and returns the
   * deserialized instances verbatim.
   * </p>
   *
   * @return the {@link NoOpArgumentConverter} to be used as conversion layer between JSON deserialization and test argument provision
   */
  Class<? extends ArgumentConverter<?, ?>> converter() default NoOpArgumentConverter.class;
}
