package me.sirikon.buletina.configuration;

import com.google.common.base.Strings;
import me.sirikon.buletina.errors.InitializationError;

import javax.inject.Singleton;

@Singleton
public class Configuration {

  private static final String ENV_KEY_PREFIX = "BULETINA_";

  private final Integer port;
  private final String baseURL;
  private final String jwtSecret;

  public Configuration() {
    this.port = Integer.parseInt(requireEnvironmentVariable("PORT"));
    this.baseURL = requireEnvironmentVariable("BASE_URL");
    this.jwtSecret = requireEnvironmentVariable("JWT_SECRET");
  }

  public Integer getPort() { return port; }
  public String getBaseURL() { return baseURL; }
  public String getJwtSecret() { return jwtSecret; }

  private static String requireEnvironmentVariable(final String key) {
    final var envKey = ENV_KEY_PREFIX + key;
    final var value = System.getenv(envKey);
    if (Strings.isNullOrEmpty(value)) {
      throw new InitializationError("Environment variale '" + envKey + "' is required");
    }
    return value;
  }

}
