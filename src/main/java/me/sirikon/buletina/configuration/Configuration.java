package me.sirikon.buletina.configuration;

import com.google.common.base.Strings;

import javax.inject.Singleton;

@Singleton
public class Configuration {

  private final String jwtSecret;

  public Configuration() {
    this.jwtSecret = requireEnvironmentVariable("BULETINA_JWT_SECRET");
  }

  public String getJwtSecret() {
    return jwtSecret;
  }

  private static String requireEnvironmentVariable(final String key) {
    final var value = System.getenv(key);
    if (Strings.isNullOrEmpty(value)) {
      throw new RuntimeException("Environment variale '" + key + "' is required");
    }
    return value;
  }

}
