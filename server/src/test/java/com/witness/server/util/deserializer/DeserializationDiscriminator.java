package com.witness.server.util.deserializer;

import lombok.Data;

/**
 * <p>
 * Holds information for a {@link PolymorphicDeserializer} implementation on how to decide for a concrete target class in a given subtype hierarchy.
 * The deserializer decides for a concrete class based on the presence of certain properties. Example:
 * </p>
 *
 * <pre>
 * abstract class SuperType { protected String A; }
 * class ConcreteType1 extends SuperType { private Boolean B; }
 * class ConcreteType2 extends SuperType { private Integer C; }
 * </pre>
 *
 * <p>
 * The two discriminators {@code {targetType: ConcreteType1, discriminatingProperties: [B]}} and
 * {@code {targetType: ConcreteType2, discriminatingProperties: [C]}} provide a {@link PolymorphicDeserializer} implementation with generic type
 * parameter {@code SuperType} with enough information to be able to correctly ascertain the specific subtype to deserialize to.
 * </p>
 *
 * @param <T> common supertype of the concrete classes to be identified by this discriminator, might be an abstract class.
 */
@Data
public class DeserializationDiscriminator<T> {
  private final Class<? extends T> targetType;
  private final String[] discriminatingProperties;

  public DeserializationDiscriminator(Class<? extends T> targetType, String... discriminatingProperties) {
    this.targetType = targetType;
    this.discriminatingProperties = discriminatingProperties;
  }

  public DeserializationDiscriminator(Class<? extends T> targetType, String discriminatingProperty) {
    this(targetType, new String[] {discriminatingProperty});
  }
}
