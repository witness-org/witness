package com.witness.server.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Exposes beans to the ApplicationContext that are related to the interaction with the Firebase authentication server.
 */
@Configuration
public class FirebaseConfig {
  private final SecurityProperties securityProps;

  @Autowired
  public FirebaseConfig(SecurityProperties securityProps) {
    this.securityProps = securityProps;
  }

  /**
   * Provides a {@link FirebaseApp} instance to the ApplicationContext.
   *
   * @return the {@link FirebaseApp} instance configured to use
   * @throws IOException if accessing the private key file used to establish a connection to the Firebase project fails
   */
  @Primary
  @Bean
  public FirebaseApp getFirebaseApp() throws IOException {
    var privateKeyStream = securityProps.getFirebaseServiceAccountKey().getInputStream();
    var options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(privateKeyStream))
        .build();

    if (FirebaseApp.getApps().isEmpty()) {
      FirebaseApp.initializeApp(options);
    }

    return FirebaseApp.getInstance();
  }

  /**
   * Provides a {@link FirebaseApp} to the ApplicationContext.
   *
   * @return the {@link FirebaseAuth} instance configured to use
   * @throws IOException if accessing the required {@link FirebaseApp} fails
   * @see FirebaseConfig#getFirebaseApp()
   */
  @Bean
  public FirebaseAuth getAuth() throws IOException {
    return FirebaseAuth.getInstance(getFirebaseApp());
  }
}
