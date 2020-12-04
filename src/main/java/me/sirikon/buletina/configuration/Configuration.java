package me.sirikon.buletina.configuration;

import com.google.common.base.Strings;
import me.sirikon.buletina.errors.InitializationError;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class Configuration {

  private static final String ENV_KEY_PREFIX = "BULETINA_";

  private final Integer port;
  private final String baseURL;
  private final String jwtSecret;
  private final String databaseUrl;
  private final String smtpServer;
  private final Integer smtpPort;
  private final String smtpSender;
  private final String smtpUsername;
  private final String smtpPassword;
  private final boolean debugTemplates;

  public Configuration() {
    this.port = Integer.parseInt(requireEnvironmentVariable("PORT"));
    this.baseURL = requireEnvironmentVariable("BASE_URL");
    this.jwtSecret = requireEnvironmentVariable("JWT_SECRET");
    this.databaseUrl = requireEnvironmentVariable("DATABASE_URL");
    this.smtpServer = requireEnvironmentVariable("SMTP_SERVER");
    this.smtpPort = Integer.parseInt(getEnvironmentVariable("SMTP_PORT", "25"));
    this.smtpSender = requireEnvironmentVariable("SMTP_SENDER");
    this.smtpUsername = requireEnvironmentVariable("SMTP_USERNAME");
    this.smtpPassword = requireEnvironmentVariable("SMTP_PASSWORD");
    this.debugTemplates = getEnvironmentVariable("DEBUG_TEMPLATES", "false").equals("true");
  }

  public Integer getPort() { return port; }
  public String getBaseURL() { return baseURL; }
  public String getJwtSecret() { return jwtSecret; }
  public String getDatabaseUrl() { return databaseUrl; }
  public String getSmtpServer() { return smtpServer; }
  public Integer getSmtpPort() { return smtpPort; }
  public String getSmtpSender() { return smtpSender; }
  public String getSmtpUsername() { return smtpUsername; }
  public String getSmtpPassword() { return smtpPassword; }
  public boolean getDebugTemplates() { return debugTemplates; }

  private static Optional<String> getEnvironmentVariable(final String key) {
    final var value = System.getenv(buildEnvKey(key));
    return Strings.isNullOrEmpty(value)
        ? Optional.empty()
        : Optional.of(value);
  }

  private static String getEnvironmentVariable(final String key, final String defaultValue) {
    return getEnvironmentVariable(key).orElse(defaultValue);
  }

  private static String requireEnvironmentVariable(final String key) {
    return getEnvironmentVariable(key)
        .orElseThrow(() -> new InitializationError("Environment variable '" + buildEnvKey(key) + "' is required"));
  }

  private static String buildEnvKey(final String key) {
    return ENV_KEY_PREFIX + key;
  }

}
