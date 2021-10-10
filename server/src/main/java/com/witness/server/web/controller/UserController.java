package com.witness.server.web.controller;

import com.witness.server.dto.UserCreateDto;
import com.witness.server.dto.UserDto;
import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.ServerError;
import com.witness.server.exception.AccessDeniedException;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataCreationException;
import com.witness.server.exception.DataModificationException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.mapper.UserMapper;
import com.witness.server.model.Credentials;
import com.witness.server.model.FirebaseUser;
import com.witness.server.service.FirebaseService;
import com.witness.server.service.SecurityService;
import com.witness.server.service.UserService;
import com.witness.server.validation.EmailStrict;
import com.witness.server.web.meta.PublicApi;
import com.witness.server.web.meta.RequiresAdmin;
import com.witness.server.web.meta.SecuredValidatedRestController;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@SecuredValidatedRestController
@RequestMapping("user")
public class UserController {
  private final SecurityService securityService;
  private final FirebaseService firebaseService;
  private final UserService userService;
  private final UserMapper userMapper;

  @Autowired
  public UserController(SecurityService securityService, FirebaseService firebaseService, UserService userService, UserMapper userMapper) {
    this.securityService = securityService;
    this.firebaseService = firebaseService;
    this.userService = userService;
    this.userMapper = userMapper;
  }

  @PostMapping("register")
  @ResponseStatus(HttpStatus.CREATED)
  @PublicApi
  public UserDto registerUser(@Valid @RequestBody UserCreateDto user, Authentication authentication)
      throws DataCreationException, DataNotFoundException, DataModificationException, AccessDeniedException {
    var entity = userMapper.createDtoToEntity(user);
    var roles = securityService.extractRoles(authentication);

    if (entity.getRole() != null && (roles.isEmpty() || !roles.get().contains(Role.ADMIN))) {
      throw new AccessDeniedException("Only admins are allowed to set the role of new users.", ServerError.INSUFFICIENT_PRIVILEGES);
    }

    var createdEntity = userService.createUser(entity, user.getPassword());
    return userMapper.entityToDto(createdEntity);
  }

  @GetMapping("findById")
  @RequiresAdmin
  public UserDto findById(Long id) throws DataAccessException {
    var user = userService.findById(id);
    return userMapper.entityToDto(user);
  }

  @GetMapping("byEmail")
  @RequiresAdmin
  public UserDto findByEmail(@RequestParam @EmailStrict String email) throws DataAccessException {
    var user = userService.findByEmail(email);
    return userMapper.entityToDto(user);
  }

  @PatchMapping("setRole")
  @RequiresAdmin
  public UserDto setRole(@RequestParam String uid, @RequestParam Role role) throws DataAccessException {
    var modifiedUser = userService.setRole(uid, role);
    return userMapper.entityToDto(modifiedUser);
  }

  @PatchMapping("removeRole")
  @RequiresAdmin
  public UserDto removeRole(@RequestParam String uid) throws DataAccessException {
    var modifiedUser = userService.removeRole(uid);
    return userMapper.entityToDto(modifiedUser);
  }

  @PostMapping("revokeRefreshTokens")
  public void revokeRefreshTokens() throws DataModificationException {
    var currentUser = securityService.getCurrentUser();
    firebaseService.revokeRefreshTokens(currentUser.getUid());
  }

  @GetMapping("current")
  public FirebaseUser getCurrent() {
    return securityService.getCurrentUser();
  }

  @GetMapping("credentials")
  public Credentials getCredentials() {
    return securityService.getCurrentCredentials();
  }

}
