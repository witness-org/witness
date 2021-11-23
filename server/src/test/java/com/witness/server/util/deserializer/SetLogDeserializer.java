package com.witness.server.util.deserializer;

import com.witness.server.entity.workout.RepsSetLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.TimeSetLog;
import java.util.List;

/**
 * Concrete implementation of {@link PolymorphicDeserializer} which supports deserialization of the {@link SetLog} subtypes {@link RepsSetLog} and
 * {@link TimeSetLog}.
 */
public class SetLogDeserializer extends PolymorphicDeserializer<SetLog> {
  @Override
  List<DeserializationDiscriminator<SetLog>> getDiscriminators() {
    return List.of(
        new DeserializationDiscriminator<>(RepsSetLog.class, "reps"),
        new DeserializationDiscriminator<>(TimeSetLog.class, "seconds")
    );
  }
}
