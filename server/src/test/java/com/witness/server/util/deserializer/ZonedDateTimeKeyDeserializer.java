package com.witness.server.util.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * A {@link KeyDeserializer} implementation that supports the mapping of JSON content field names into Java {@link Map} keys of type
 * {@link ZonedDateTime}.
 */
public class ZonedDateTimeKeyDeserializer extends KeyDeserializer {
  @Override
  public Object deserializeKey(String key, DeserializationContext ctx) throws IOException {
    return ZonedDateTime.parse(key);
  }
}