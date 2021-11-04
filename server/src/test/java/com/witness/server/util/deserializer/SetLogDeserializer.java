package com.witness.server.util.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.witness.server.entity.workout.RepsSetLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.TimeSetLog;
import java.io.IOException;
import java.util.Map;

public class SetLogDeserializer extends StdDeserializer<SetLog> {
  private static final ObjectMapper defaultDeserializer = new ObjectMapper();
  private static final Map<String, Class<? extends SetLog>> propertyTypeMappings = Map.of(
      "reps", RepsSetLog.class,
      "seconds", TimeSetLog.class
  );

  public SetLogDeserializer() {
    this(null);
  }

  protected SetLogDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public SetLog deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
    var objectNode = jp.getCodec().readTree(jp);
    var targetType = getSetLogType(jp, objectNode);
    return defaultDeserializer.treeToValue(objectNode, targetType);
  }

  private Class<? extends SetLog> getSetLogType(JsonParser jp, TreeNode objectNode) throws JsonParseException {
    // if the node contains a property known to uniquely identify a subtype, its associated type will be used as deserialization target
    for (var propertyTypeMapping : propertyTypeMappings.entrySet()) {
      if (objectNode.get(propertyTypeMapping.getKey()) != null) {
        return propertyTypeMapping.getValue();
      }
    }

    throw new JsonParseException(jp, "Could not determine concrete (derived) type of 'SetLog' property based on its JSON tree.");
  }
}
