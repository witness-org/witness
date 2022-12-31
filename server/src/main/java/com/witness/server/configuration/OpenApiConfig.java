package com.witness.server.configuration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.witness.server.enumeration.ServerError;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes beans that are related to the configuration of springdoc, the plugin that generates an OpenAPI 3.0 specification of the
 * rest API exposed by this application which is consumed by Swagger, based on special annotations.
 *
 * @see io.swagger.v3.oas.annotations
 */
@Configuration
public class OpenApiConfig {
  public static final String SECURITY_SCHEME_BEARER_TOKEN = "bearerAuth";
  public static final String SECURITY_SCHEME_NONE = "none";

  private static final List<Function<PathItem, Operation>> OPERATION_GETTERS = Arrays.asList(
      PathItem::getGet, PathItem::getPost, PathItem::getDelete, PathItem::getHead,
      PathItem::getOptions, PathItem::getPatch, PathItem::getPut);

  /**
   * <p>
   * Defines an OpenAPI 3.0 specification with general metadata and a security scheme for the REST API exposed by this application. The security
   * scheme defined by this bean makes that consumers of this metadata (e.g. Swagger UI) understand that it is secured using bearer authentication.
   * More specifically, the {@link SecurityRequirement} annotation with a reference to the scheme identified by
   * {@link OpenApiConfig#SECURITY_SCHEME_BEARER_TOKEN} on a REST controller leads to a padlock in the UI and an opportunity to provide a bearer
   * token such that requests sent via Swagger UI are properly authenticated.
   * </p>
   * <p>
   * Note that these metadata are purely for OpenAPI specification purposes and do not influence the actual security implementation
   * (request filters or the like).
   * </p>
   */
  @Bean
  public OpenAPI serverApi() {
    return new OpenAPI()
        .components(new Components())
        .info(new Info()
            .title("witness public API")
            .version("v1")
            .description("API exposed by witness server application.")
            .termsOfService("https://www.witness.io/tos")
            .contact(new Contact()
                .name("development team")
                .email("admin@witness.com"))
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
        .externalDocs(new ExternalDocumentation()
            .description("Find witness on the Web")
            .url("https://www.witness.io/"))
        .schemaRequirement(SECURITY_SCHEME_BEARER_TOKEN,
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer"));
  }

  /**
   * <p>
   * Customizes the {@link OpenAPI} object generated by springdoc in two ways.
   * </p>
   * <p>
   * Firstly, it overrides the globally defined bearer token security scheme specified by {@link OpenApiConfig#serverApi()} and identified by
   * {@link OpenApiConfig#SECURITY_SCHEME_BEARER_TOKEN} which, by default, applies to all methods in a controller decorated with the corresponding
   * {@link SecurityRequirement} annotation. If an operation contains an additional reference to the security scheme identified by
   * {@link OpenApiConfig#SECURITY_SCHEME_NONE}, the latter one overrides the bearer scheme, clearing all {@link SecurityRequirement} definitions for
   * this operation, essentially defining it to be accessible without any means of authentication. If an operation does not contain such a reference,
   * it is kept untouched.
   * </p>
   * <p>
   * Secondly, it applies a natural ordering to the schema definitions which are generally found at the bottom of Swagger UI.
   * </p>
   *
   * @return a possibly modified version of the {@link OpenAPI} object populated by springdoc with removed security requirements operations that are
   *     decorated with the {@link OpenApiConfig#SECURITY_SCHEME_NONE} scheme as well as security schemes that are sorted according to their natural
   *     ordering.
   */
  @Bean
  public OpenApiCustomizer getApiCustomizer() {
    return openApi -> {
      openApi.getPaths().forEach(OpenApiConfig::clearSecuritySchemeOfPublicMethods);
      openApi.getPaths().forEach(OpenApiConfig::addDefaultUnauthorizedResponseForNonPublicMethods);
      openApi.getPaths().forEach(OpenApiConfig::setMediaTypeJsonForApiResponses);
      openApi.getComponents().setSchemas(sortSchemas(openApi));
    };
  }

  @SuppressWarnings("rawtypes") // raw declaration of Schema type is dictated by external library
  private Map<String, Schema> sortSchemas(OpenAPI api) {
    return new TreeMap<>(api.getComponents().getSchemas());
  }

  private static void clearSecuritySchemeOfPublicMethods(String path, PathItem pathItem) {
    OPERATION_GETTERS.stream()
        .map(getter -> getter.apply(pathItem))
        .filter(OpenApiConfig::isPublicOperation)
        .forEach(operation -> operation.setSecurity(Collections.emptyList()));
  }

  private static void addDefaultUnauthorizedResponseForNonPublicMethods(String path, PathItem pathItem) {
    OPERATION_GETTERS.stream()
        .map(getter -> getter.apply(pathItem))
        .filter(OpenApiConfig::isProtectedOperation)
        .forEach(operation -> operation.getResponses().addApiResponse("401",
            new ApiResponse()
                .description("Unauthorized request to protected resource, Bearer token missing.")
                .content(
                    new Content().addMediaType(APPLICATION_JSON_VALUE,
                        new MediaType().example(Map.of(
                            "errorKey", ServerError.AUTHORIZATION_NOT_GRANTED.name(),
                            "message", "Unauthorized access of protected resource.",
                            "error", "UNAUTHORIZED",
                            "status", 401,
                            "timestamp", "2021-11-12T22:47:29.0767629+01:00"
                        )))
                )));
  }

  private static void setMediaTypeJsonForApiResponses(String path, PathItem pathItem) {
    OPERATION_GETTERS.stream()
        .map(getter -> getter.apply(pathItem))
        .filter(OpenApiConfig::isAccessibleOperation)
        .forEach(operation -> operation
            .getResponses().forEach((statusCode, apiResponse) -> {
              // if there is no response content or not exactly one media type, leave definition untouched
              if (apiResponse.getContent() == null || apiResponse.getContent().size() != 1) {
                return;
              }

              var responseContent = apiResponse.getContent().values().iterator().next();
              apiResponse.setContent(new Content().addMediaType(APPLICATION_JSON_VALUE, responseContent));
            })
        );
  }

  private static boolean isAccessibleOperation(Operation operation) {
    return operation != null;
  }

  private static boolean isPublicOperation(Operation operation) {
    if (!isAccessibleOperation(operation)) {
      return false;
    }

    var requirements = operation.getSecurity();
    return (requirements == null || requirements.isEmpty()
            || requirements.stream().anyMatch(requirement -> requirement.containsKey(SECURITY_SCHEME_NONE)));
  }

  private static boolean isProtectedOperation(Operation operation) {
    if (!isAccessibleOperation(operation)) {
      return false;
    }

    return !isPublicOperation(operation);
  }
}
