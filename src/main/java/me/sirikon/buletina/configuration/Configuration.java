package me.sirikon.buletina.configuration;

import javax.inject.Singleton;

@Singleton
public class Configuration {

  private final String jwtSecret;

  public Configuration() {
    this.jwtSecret = "secret";
  }

  public String getJwtSecret() {
    return jwtSecret;
  }

}
