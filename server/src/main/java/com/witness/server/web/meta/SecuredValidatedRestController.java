package com.witness.server.web.meta;

import com.witness.server.configuration.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Defines a REST controller to be secured by HTTP bearer token authentication. This leads to the possibility of providing a bearer token
 * when sending requests via Swagger UI.
 * </p>
 * <p>
 * Furthermore, JSR 380 constraint annotations for two types of request parameters - {@link RequestParam} and {@link PathVariable} - are
 * automatically validated when processing incoming requests.
 * </p>
 * <p>
 * Note that for the third type of request parameter, {@link RequestBody}, the corresponding method parameter additionally has to be decorated
 * with the {@link Valid} annotation for validation to be performed, for instance {@code void create(@Valid @RequestBody dto)}.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_BEARER_TOKEN)
@Validated
public @interface SecuredValidatedRestController {
}
