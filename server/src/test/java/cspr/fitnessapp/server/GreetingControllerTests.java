package cspr.fitnessapp.server;

import static org.assertj.core.api.Assertions.assertThat;

import cspr.fitnessapp.server.dto.GreetingDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GreetingControllerTests {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void greetingShouldReturnDefaultMessage() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getUrl());

    HttpEntity<?> entity = new HttpEntity<>(headers);
    var response = restTemplate.exchange(
        builder.toUriString(),
        HttpMethod.GET,
        entity,
        GreetingDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEqualTo("Hello, World!");
    assertThat(response.getBody().getId()).isPositive();
  }

  @Test
  public void greetingShouldReturnSpecifiedName() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

    UriComponentsBuilder builder = UriComponentsBuilder
        .fromHttpUrl(getUrl())
        .queryParam("name", "CustomName");

    var entity = new HttpEntity<>(headers);
    var response = restTemplate.exchange(
        builder.toUriString(),
        HttpMethod.GET,
        entity,
        GreetingDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEqualTo("Hello, CustomName!");
    assertThat(response.getBody().getId()).isPositive();
  }

  private String getUrl() {
    return "http://localhost:" + port + "/" + "/greeting";
  }

}
