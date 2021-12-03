package com.witness.server.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.witness.server.mapper.FirebaseMapper;
import com.witness.server.model.FirebaseUser;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import com.witness.server.util.converter.FirebaseTokenArgumentConverter;
import com.witness.server.util.converter.UserRecordArgumentConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;

class FirebaseMapperTest extends BaseUnitTest {
  private static final String DATA_ROOT = "data/unit/mapper/firebase-mapper-test/";
  private final FirebaseMapper mapper = Mappers.getMapper(FirebaseMapper.class);

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FirebaseToken1.json", type = FirebaseToken.class, converter = FirebaseTokenArgumentConverter.class),
      @JsonFileSource(value = DATA_ROOT + "FirebaseUser1.json", type = FirebaseUser.class)
  })
  void tokenToUser(FirebaseToken token, FirebaseUser user) {
    assertThat(mapper.tokenToUser(token)).isEqualTo(user);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserRecord1.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class),
      @JsonFileSource(value = DATA_ROOT + "FirebaseUser2.json", type = FirebaseUser.class)
  })
  void recordToUser(UserRecord record, FirebaseUser user) {
    assertThat(mapper.recordToUser(record)).isEqualTo(user);
  }
}



