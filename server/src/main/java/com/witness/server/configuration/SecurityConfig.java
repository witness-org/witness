package com.witness.server.configuration;

import com.witness.server.enumeration.ServerError;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.service.SecurityService;
import com.witness.server.web.infrastructure.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Exposes beans that are related to security (CORS, authentication) and, in particular, achieves HTTP endpoint
 * protection by creating a {@link SecurityFilterChain} bean.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {
  private final SecurityProperties restSecProps;
  private final SecurityFilter tokenAuthenticationFilter;
  private final SecurityService securityService;

  @Autowired
  public SecurityConfig(SecurityProperties restSecProps, SecurityFilter tokenAuthenticationFilter, SecurityService securityService) {
    this.restSecProps = restSecProps;
    this.tokenAuthenticationFilter = tokenAuthenticationFilter;
    this.securityService = securityService;
  }

  /**
   * Provides an {@link AuthenticationEntryPoint} instance to the ApplicationContext. It is used when authentication checks using
   * {@link SecurityFilter} fail.
   *
   * @return the {@link AuthenticationEntryPoint} instance configured to use
   */
  @Bean
  public AuthenticationEntryPoint unauthorizedHandler() {
    return (request, response, exception) -> securityService.replyAuthenticationError(
        response, new AuthenticationException("Unauthorized access of protected resource.", ServerError.AUTHORIZATION_NOT_GRANTED)
    );
  }

  /**
   * Provides a {@link CorsConfigurationSource} instance to the ApplicationContext. It is used to secure the API's endpoints.
   *
   * @return the {@link CorsConfigurationSource} instance configured to use
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(restSecProps.getAllowedOrigins());
    corsConfig.setAllowedMethods(restSecProps.getAllowedMethods());
    corsConfig.setAllowedHeaders(restSecProps.getAllowedHeaders());

    var corsConfigSource = new UrlBasedCorsConfigurationSource();
    corsConfigSource.registerCorsConfiguration("/**", corsConfig);
    return corsConfigSource;
  }

  @Bean
  protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http.cors()
        .configurationSource(corsConfigurationSource()).and()
        .csrf().disable()
        .headers().frameOptions().sameOrigin().and()
        .formLogin().disable()
        .httpBasic().disable()
        .exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler()).and()
        .authorizeRequests()
        .antMatchers(restSecProps.getAllowedPublicApis().toArray(String[]::new)).permitAll()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .anyRequest().fullyAuthenticated().and()
        .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    return http.build();
  }
}
