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
  private final String databaseUrl;
  private final String smtpServer;
  private final String smtpSender;
  private final String smtpUsername;
  private final String smtpPassword;

  public Configuration() {
    this.port = Integer.parseInt(requireEnvironmentVariable("PORT"));
    this.baseURL = requireEnvironmentVariable("BASE_URL");
    this.jwtSecret = requireEnvironmentVariable("JWT_SECRET");
    this.databaseUrl = requireEnvironmentVariable("DATABASE_URL");
    this.smtpServer = requireEnvironmentVariable("SMTP_SERVER");
    this.smtpSender = requireEnvironmentVariable("SMTP_SENDER");
    this.smtpUsername = requireEnvironmentVariable("SMTP_USERNAME");
    this.smtpPassword = requireEnvironmentVariable("SMTP_PASSWORD");
  }

  public Integer getPort() { return port; }
  public String getBaseURL() { return baseURL; }
  public String getJwtSecret() { return jwtSecret; }
  public String getDatabaseUrl() { return databaseUrl; }
  public String getSmtpServer() { return smtpServer; }
  public String getSmtpSender() { return smtpSender; }
  public String getSmtpUsername() { return smtpUsername; }
  public String getSmtpPassword() { return smtpPassword; }

  private static String requireEnvironmentVariable(final String key) {
    final var envKey = ENV_KEY_PREFIX + key;
    final var value = System.getenv(envKey);
    if (Strings.isNullOrEmpty(value)) {
      throw new InitializationError("Environment variable '" + envKey + "' is required");
    }
    return value;
  }

}
