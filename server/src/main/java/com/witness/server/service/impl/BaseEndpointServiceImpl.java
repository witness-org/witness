package com.witness.server.service.impl;

import com.witness.server.entity.User;
import com.witness.server.exception.DataAccessException;
import com.witness.server.service.UserService;

public abstract class BaseEndpointServiceImpl {

  private final UserService userService;

  protected BaseEndpointServiceImpl(UserService userService) {
    this.userService = userService;
  }

  protected User getUser(String firebaseId) throws DataAccessException {
    return userService.findByFirebaseId(firebaseId);
  }
}
