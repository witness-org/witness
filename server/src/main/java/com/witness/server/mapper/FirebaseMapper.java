package com.witness.server.mapper;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.witness.server.model.FirebaseUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class FirebaseMapper {
  public abstract FirebaseUser tokenToUser(FirebaseToken token);

  @Mapping(source = "displayName", target = "name")
  @Mapping(source = "photoUrl", target = "picture")
  @Mapping(target = "issuer", ignore = true) // UserRecord does not hold information about a JWT, therefore there can be no issuer.
  public abstract FirebaseUser recordToUser(UserRecord userRecord);
}
