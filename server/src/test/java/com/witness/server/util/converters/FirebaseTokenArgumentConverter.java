package com.witness.server.util.converters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.FirebaseToken;
import com.witness.server.util.stubs.FirebaseTokenStub;

/**
 * Implementation of {@link ArgumentConverter} for {@link FirebaseToken} and {@link FirebaseTokenStub} instances.
 */
public class FirebaseTokenArgumentConverter extends ArgumentConverter<FirebaseToken, FirebaseTokenStub> {
  @Override
  public FirebaseToken toConcreteInstanceInternal(FirebaseTokenStub intermediate) {
    var token = mock(FirebaseToken.class);
    when(token.getUid()).thenReturn(intermediate.getUid());
    when(token.getName()).thenReturn(intermediate.getName());
    when(token.getEmail()).thenReturn(intermediate.getEmail());
    when(token.isEmailVerified()).thenReturn(intermediate.isEmailVerified());
    when(token.getIssuer()).thenReturn(intermediate.getIssuer());
    when(token.getPicture()).thenReturn(intermediate.getPicture());
    return token;
  }

  @Override
  public Class<FirebaseTokenStub> intermediateClass() {
    return FirebaseTokenStub.class;
  }

  @Override
  public Class<FirebaseToken> targetClass() {
    return FirebaseToken.class;
  }
}
