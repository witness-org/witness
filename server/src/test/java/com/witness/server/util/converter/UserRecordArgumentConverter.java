package com.witness.server.util.converter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.UserRecord;
import com.witness.server.util.stub.UserRecordStub;

/**
 * Implementation of {@link ArgumentConverter} for {@link UserRecord} and {@link UserRecordStub} instances.
 */
public class UserRecordArgumentConverter extends ArgumentConverter<UserRecord, UserRecordStub> {
  @Override
  UserRecord toConcreteInstanceInternal(UserRecordStub intermediate) {
    var record = mock(UserRecord.class);
    when(record.getUid()).thenReturn(intermediate.getUid());
    when(record.getDisplayName()).thenReturn(intermediate.getDisplayName());
    when(record.getEmail()).thenReturn(intermediate.getEmail());
    when(record.isEmailVerified()).thenReturn(intermediate.isEmailVerified());
    when(record.getPhotoUrl()).thenReturn(intermediate.getPhotoUrl());
    return record;
  }

  @Override
  public Class<UserRecordStub> intermediateClass() {
    return UserRecordStub.class;
  }

  @Override
  public Class<UserRecord> targetClass() {
    return UserRecord.class;
  }
}
