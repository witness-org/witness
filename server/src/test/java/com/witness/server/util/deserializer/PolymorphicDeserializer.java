package com.witness.server.util.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * When deserializing polymorphic types (i.e. types within an arbitrary subtype hierarchy), there are several ways to make Jackson aware of the
 * specific target (sub-)type to deserialize - for instance, using a designated type {@code @JsonTypeInfo} and {@code @JsonSubTypes} annotations.
 * This is what we use in the production code for polymorphic DTOs. However, during tests we also deserialize polymorphic entity classes from JSON
 * (which does not happen in production). Since production code should not be amended only for test purposes ({@code @JsonTypeInfo} and
 * {@code @JsonSubType} annotation on entity classes do not really make sense), we use custom deserializers that enable us to deserialize to the
 * desired entity (sub-)type based on the presence of discriminating properties that identify the target (sub-)type.
 *
 * @param <T> the most general type of the subtype hierarchy to be supported by this deserializer, might be an abstract class
 */
public abstract class PolymorphicDeserializer<T> extends StdDeserializer<T> {
  @SuppressWarnings("checkstyle:MatchXpath") // false-positive "use var" warning
  private static final ObjectMapper defaultDeserializer = new ObjectMapper();

  public PolymorphicDeserializer() {
    this(null);
  }

  protected PolymorphicDeserializer(Class<?> vc) {
    super(vc);
  }

  /**
   * A list of {@link DeserializationDiscriminator} instances that determines the concrete class of the (subtype of the) type parameter {@code T} to
   * deserialize a target entity to. During deserialization, this list is iterated in order and the first item for which all properties defined by
   * {@link DeserializationDiscriminator#getDiscriminatingProperties()} are present in the JSON object determines the target class, as denoted by
   * {@link DeserializationDiscriminator#getTargetType()}. Disjunctive discriminators (e.g. "Target type X if property A or B is present") can be
   * resolved by multiple list entries (e.g. [{properties: A, targetType: X, properties: B, targetType: X}]). Precedences are defined by order of
   * insertion into the list.
   *
   * @return a list of {@link DeserializationDiscriminator} that defines which JSON properties uniquely identify target deserializes types
   */
  abstract List<DeserializationDiscriminator<T>> getDiscriminators();

  @Override
  public T deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
    var jsonObject = jp.getCodec().readTree(jp);
    var targetType = getTargetType(jp, jsonObject);
    return defaultDeserializer.treeToValue(jsonObject, targetType);
  }

  private Class<? extends T> getTargetType(JsonParser jp, TreeNode jsonObject) throws JsonParseException {
    for (var discriminator : getDiscriminators()) {
      if (propertiesPresent(jsonObject, discriminator.getDiscriminatingProperties())) {
        return discriminator.getTargetType();
      }
    }

    throw new JsonParseException(jp, "Unable to determine target deserialization type of JSON object tree based on DeserializationDiscriminators.");
  }

  private boolean propertiesPresent(TreeNode jsonObject, String... propertyNames) {
    return Arrays.stream(propertyNames).allMatch(property -> jsonObject.get(property) != null);
  }
}
