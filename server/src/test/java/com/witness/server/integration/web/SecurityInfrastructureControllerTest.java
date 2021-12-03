package com.witness.server.integration.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.integration.infrastructure.test.MessageDto;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class SecurityInfrastructureControllerTest extends BaseControllerIntegrationTest {
  @Override
  String getEndpointUrl() {
    return "security-infrastructure";
  }

  @ParameterizedTest
  @CsvSource(value = {"NONE,401,null", "REGULAR,200,'Hello, World!'", "PREMIUM,200,'Hello, World!'",
      "ADMIN,200,'Hello, World!'"}, nullValues = "null")
  void message_authenticationModes_return200ForRegularUsersAndAboveOtherwise401(
      @AggregateWith(EndpointTestSpecificationAggregator.class) EndpointTestSpecification specification) {
    var response = get(specification.getAuthMode(), requestUrl(), MessageDto.class);

    assertThat(response.getStatusCode()).isEqualTo(specification.getExpectedStatus());
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEqualTo(specification.getContainedContent());
  }

  @Test
  void message_validMessage_returnMessageContainingName() {
    var params = toMultiValueMap(Map.of("name", "user"));
    var response = exchange(TestAuthentication.REGULAR, requestUrl(), HttpMethod.GET, params, MessageDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).contains("user");
  }

  @Test
  void message_invalidMessage_return400() {
    var params = toMultiValueMap(Map.of("name", "u"));
    var response = exchange(TestAuthentication.REGULAR, requestUrl(), HttpMethod.GET, params, MessageDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @CsvSource({"NONE,401,{", "REGULAR,403,{", "PREMIUM,200,premium", "ADMIN,403,{"})
  void getPremiumData_authenticationModes_return200AndContentForPremiumUsersOnlyOtherwise401And403(
      @AggregateWith(EndpointTestSpecificationAggregator.class) EndpointTestSpecification specification) {
    var response = get(specification.getAuthMode(), requestUrl("premiumData"), String.class);

    assertThat(response.getStatusCode()).isEqualTo(specification.getExpectedStatus());
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).contains(specification.getContainedContent());
  }

  @ParameterizedTest
  @CsvSource({"NONE,401,{", "REGULAR,403,{", "PREMIUM,403,{", "ADMIN,200,admin"})
  void getAdminData_authenticationModes_return200AndContentForAdminUsersOnlyOtherwise401And403(
      @AggregateWith(EndpointTestSpecificationAggregator.class) EndpointTestSpecification specification) {
    var response = get(specification.getAuthMode(), requestUrl("adminData"), String.class);

    assertThat(response.getStatusCode()).isEqualTo(specification.getExpectedStatus());
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).contains(specification.getContainedContent());
  }

  @ParameterizedTest
  @CsvSource({"NONE,401,{", "REGULAR,403,{", "PREMIUM,200,premiumOrAdmin", "ADMIN,200,premiumOrAdmin"})
  void getPremiumOrAdminData_authenticationModes_return200AndContentForPremiumAndAdminUsersOnlyOtherwise401And403(
      @AggregateWith(EndpointTestSpecificationAggregator.class) EndpointTestSpecification specification) {
    var response = get(specification.getAuthMode(), requestUrl("premiumOrAdminData"), String.class);

    assertThat(response.getStatusCode()).isEqualTo(specification.getExpectedStatus());
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).contains(specification.getContainedContent());
  }

  @ParameterizedTest
  @EnumSource(TestAuthentication.class)
  void getPublicData_authenticationModes_alwaysReturn200AndContent(TestAuthentication authMode) {
    var response = get(authMode, requestUrl("public"), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotBlank();
  }

  @Test
  void createMessage_validMessage_return200AndCreatedMessage() {
    var payload = new MessageDto(250L, "AtLeast10Characters");
    var response = exchange(TestAuthentication.REGULAR, requestUrl(), HttpMethod.POST, payload, MessageDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(payload);
  }

  @Test
  void createMessage_invalidMessage_return400() {
    var payload = new MessageDto(1L, "TooShort");
    var response = exchange(TestAuthentication.REGULAR, requestUrl(), HttpMethod.POST, payload, MessageDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Data
  @AllArgsConstructor
  static class EndpointTestSpecification {
    private TestAuthentication authMode;
    private HttpStatus expectedStatus;
    private String containedContent;
  }

  static class EndpointTestSpecificationAggregator implements ArgumentsAggregator {
    @Override
    public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
      return new EndpointTestSpecification(
          TestAuthentication.valueOf(accessor.getString(0)),
          HttpStatus.valueOf(accessor.getInteger(1)),
          accessor.getString(2)
      );
    }
  }
}
