package com.witness.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.util.converter.ArgumentConverter;
import com.witness.server.util.converter.NoOpArgumentConverter;
import com.witness.server.util.deserializer.SetLogDeserializer;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ReflectionUtils;

/**
 * A {@link ArgumentsProvider} and {@link AnnotationConsumer} for the {@link JsonFileSources} annotation.
 */
@Slf4j
public class JsonFileArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<JsonFileSources> {
  private static final ObjectMapper objectMapper = new ObjectMapper()
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .registerModule(new SimpleModule("CUSTOM_DESERIALIZERS")
          .addDeserializer(SetLog.class, new SetLogDeserializer()))
      .registerModule(new JavaTimeModule());

  private JsonFileSource[] files;
  private boolean unwrap;

  @Override
  public void accept(JsonFileSources jsonFileSources) {
    files = jsonFileSources.parameters();
    unwrap = jsonFileSources.unwrapArrays();
  }

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
    var parameters = new Object[files.length];

    for (var i = 0; i < files.length; i++) {
      var file = files[i];
      var jsonStream = new ClassPathResource(file.value()).getInputStream();

      var deserializedObject = getDeserializedObject(file, jsonStream, file.converter());
      if (file.arrayToList() && file.type().isArray()) {
        deserializedObject = Arrays.stream((Object[]) deserializedObject).collect(Collectors.toList());
      }

      parameters[i] = deserializedObject;
    }

    if (shouldUnwrap(parameters, context)) {
      // See documentation of JsonFileSources for a concrete example on what is described below.
      // We transpose the two-dimensional matrix of input-arrays in order to prepare multiple test case executions with simple arguments instead of
      // one execution with array arguments. For example, the test input of three array arguments [[1a,2a,3a,4a],[1b,2b,3b,4b],[1c,2b,3c,4c]]
      // is transformed such that array elements with the same index give rise to one test case execution. More specifically, instead of a
      // single-element stream with three four-element array arguments, we supply a four-element stream with three simple arguments to the respective
      // test method ([[1a,1b,1c],[2a,2b,2c],[3a,3b,3c],[4a,4b,4c]]). Particularly, the test will be executed with inputs (1a,1b,1c), (2a,2b,2c) etc.
      var transposedArguments = transposeParameterMatrix(parameters);
      var unwrappedArguments = Arrays.stream(transposedArguments).map(args -> Arguments.of((Object[]) args)).toArray(Arguments[]::new);
      return Stream.of(unwrappedArguments);
    } else {
      return Stream.of(Arguments.of(parameters));
    }
  }

  private Object getDeserializedObject(JsonFileSource file, InputStream jsonStream, Class<? extends ArgumentConverter<?, ?>> argumentConverter)
      throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    var converter = ReflectionUtils.accessibleConstructor(argumentConverter).newInstance();
    var noOpConverter = converter instanceof NoOpArgumentConverter;
    var sourceType = noOpConverter ? file.type() : converter.intermediateClass(); // JSON deserialization target type
    var targetType = noOpConverter ? file.type() : converter.targetClass(); // method return type

    if (!targetType.equals(file.type())) {
      log.error("The specified test argument converter is not NoOpArgumentConverter, but its target class differs from the class specified by the "
          + "\"type\" argument of the @JsonFileSource annotation. Supplying arguments to the test will fail due to incompatible types.");
    }

    var intermediateObject = objectMapper.readValue(jsonStream, sourceType);
    return converter.toConcreteInstance(intermediateObject);
  }

  private boolean shouldUnwrap(Object[] parameters, ExtensionContext context) {
    // Three conditions need to hold in order for unwrapping to happen:

    // 1) the annotation property must be set accordingly
    if (!unwrap) {
      return false;
    }

    var testMethod = context.getTestMethod().isPresent() ? context.getTestMethod().get().getName() : "<unknown>";
    var errorMessage = "Test \"" + testMethod + "\" is annotated with @JsonFileSources and \"unwrapArrays=true\", but {}. Will not unwrap, test "
        + "will fail. Please examine your data sources and @JsonFileSource annotations.";

    // 2) all arguments must be arrays
    var allArrays = Arrays.stream(parameters).allMatch(param -> param.getClass().isArray());
    if (!allArrays) {
      log.error(errorMessage, "not all arguments specified are arrays");
      return false;
    }

    // 3) all arrays must have the same length
    // we compare the length of the first array with the length of all other arrays (and therefore do not need to examine the first element)
    var sameLengthArrays = Arrays.stream(parameters).skip(1).allMatch(param -> ((Object[]) param).length == ((Object[]) parameters[0]).length);
    if (!sameLengthArrays) {
      log.error(errorMessage, "not all argument arrays have the same length");
      return false;
    }

    return true;
  }


  private static Object[] transposeParameterMatrix(Object[] parameters) {
    var rows = parameters.length;
    var cols = ((Object[]) parameters[0]).length;

    var grid = new Object[cols];
    for (var j = 0; j < cols; j++) {
      grid[j] = new Object[rows];
    }

    for (var i = 0; i < rows; i++) {
      for (var j = 0; j < cols; j++) {
        ((Object[]) grid[j])[i] = ((Object[]) parameters[i])[j];
      }
    }

    return grid;
  }
}
