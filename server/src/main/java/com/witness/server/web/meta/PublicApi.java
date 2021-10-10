package com.witness.server.web.meta;

import com.witness.server.configuration.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that the REST operation represented by the target method may be accessed without authentication, i.e. one does not need to be a registered
 * user to invoke it. Note that this annotation should be used in conjunction with an entry in the "security.allowed-public-apis" enumeration (see
 * application.yml) - otherwise, the CORS configuration would not allow accessing the operation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SecurityRequirements(value = {@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NONE)})
public @interface PublicApi {
}
