package com.witness.server.util.converters;

/**
 * Default implementation of {@link ArgumentConverter}. It converts nothing and returns the passed instance verbatim.
 */
public class NoOpArgumentConverter extends ArgumentConverter<Object, Object> {
  @Override
  Object toConcreteInstanceInternal(Object intermediate) {
    return intermediate;
  }

  @Override
  public Class<Object> intermediateClass() {
    return Object.class;
  }

  @Override
  public Class<Object> targetClass() {
    return Object.class;
  }
}
