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
import com.witness.server.service.SecurityService;
import com.witness.server.service.UserService;
import com.witness.server.validation.EmailStrict;
import com.witness.server.web.meta.PublicApi;
import com.witness.server.web.meta.RequiresAdmin;
import com.witness.server.web.meta.SecuredValidatedRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@SecuredValidatedRestController
@RequestMapping("user")
@Tag(name = "Users", description = "This controller provides endpoint methods for operations regarding the user management.")
public class UserController {
  private final SecurityService securityService;
  private final UserService userService;
  private final UserMapper userMapper;

  @Autowired
  public UserController(SecurityService securityService, UserService userService, UserMapper userMapper) {
    this.securityService = securityService;
    this.userService = userService;
    this.userMapper = userMapper;
  }

  @PostMapping("register")
  @ResponseStatus(HttpStatus.CREATED)
  @PublicApi
  @Operation(summary = "Registers a new user. Setting a role for the user is only possible for administrators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "The user was successfully created."),
      @ApiResponse(responseCode = "400", description = "The user could not be created because the request body is invalid."),
      @ApiResponse(responseCode = "403",
          description = "The user could not be created because the requester does not have permission to set the role of the registered user."),
      @ApiResponse(responseCode = "404", description = "The user could not be created because the created Firebase user could not be found."),
      @ApiResponse(responseCode = "500",
          description = "The user could not be created because the corresponding Firebase user could not be created/modified.")
  })
  public UserDto registerUser(@Valid @RequestBody @Parameter(description = "The user that should be registered.") UserCreateDto user,
                              Authentication authentication) throws DataCreationException, DataNotFoundException, DataModificationException,
      AccessDeniedException {
    var entity = userMapper.createDtoToEntity(user);
    var roles = securityService.extractRoles(authentication);

    if (entity.getRole() != null && (roles.isEmpty() || !roles.get().contains(Role.ADMIN))) {
      throw new AccessDeniedException("Only admins are allowed to set the role of new users.", ServerError.INSUFFICIENT_PRIVILEGES);
    }

    var createdEntity = userService.createUser(entity, user.getPassword());
    return userMapper.entityToDto(createdEntity);
  }

  @GetMapping("findById/{id}")
  @RequiresAdmin
  @Operation(summary = "Fetches the user with the given ID. Only possible for admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The user was successfully fetched."),
      @ApiResponse(responseCode = "400", description = "The user could not be fetched because the request was invalid."),
      @ApiResponse(responseCode = "404", description = "There is no user with the requested ID."),
      @ApiResponse(responseCode = "500", description = "The user could not be fetched because an error occurred.")
  })
  public UserDto findById(@PathVariable(name = "id") @Positive
                          @Parameter(description = "The ID of the user to fetch. Must be greater than 0.") Long id) throws DataAccessException {
    var user = userService.findById(id);
    return userMapper.entityToDto(user);
  }

  @GetMapping("byEmail/{email}")
  @RequiresAdmin
  @Operation(summary = "Fetches the user with the given email address. Only possible for admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The user was successfully fetched."),
      @ApiResponse(responseCode = "400", description = "The user could not be fetched because the request was invalid."),
      @ApiResponse(responseCode = "404", description = "There is no user with the requested email address."),
      @ApiResponse(responseCode = "500", description = "The user could not be fetched because an error occurred.")
  })
  public UserDto findByEmail(
      @PathVariable(name = "email") @EmailStrict
      @Parameter(description = "The email address of the user to fetch.", example = "user123@example.com") String email)
      throws DataAccessException {
    var user = userService.findByEmail(email);
    return userMapper.entityToDto(user);
  }

  @PatchMapping("{id}/setRole")
  @RequiresAdmin
  @Operation(summary = "Updates the role for the user with the given Firebase ID. Only possible for admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The role of the user was successfully updated."),
      @ApiResponse(responseCode = "400", description = "The role could not be set because the request was invalid."),
      @ApiResponse(responseCode = "404", description = "There is no user with the provided Firebase ID."),
      @ApiResponse(responseCode = "500", description = "The user could not be fetched because an error occurred.")
  })
  public UserDto setRole(
      @PathVariable(name = "id") @NotBlank
      @Parameter(description = "The Firebase ID of the user whose role should be updated.", example = "aXusKTIbLYSKc8wnQJeOz8c3JsT2") String id,
      @RequestParam(name = "role") @NotNull
      @Parameter(description = "The role that should be set.", example = "ADMIN") Role role) throws DataAccessException {
    var modifiedUser = userService.setRole(id, role);
    return userMapper.entityToDto(modifiedUser);
  }

  @PatchMapping("{id}/removeRole")
  @RequiresAdmin
  @Operation(summary = "Removes the role for the user with the given Firebase ID. Only possible for admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The role of the user was successfully removed."),
      @ApiResponse(responseCode = "400", description = "The role could not be removed because the request was invalid."),
      @ApiResponse(responseCode = "404", description = "There is no user with the provided Firebase ID."),
      @ApiResponse(responseCode = "500", description = "The user could not be fetched because an error occurred.")
  })
  public UserDto removeRole(
      @PathVariable(name = "id") @NotBlank
      @Parameter(description = "The Firebase ID of the user whose role should be removed.", example = "aXusKTIbLYSKc8wnQJeOz8c3JsT2") String id)
      throws DataAccessException {
    var modifiedUser = userService.removeRole(id);
    return userMapper.entityToDto(modifiedUser);
  }
}
