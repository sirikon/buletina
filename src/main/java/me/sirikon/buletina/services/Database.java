package me.sirikon.buletina.services;

import me.sirikon.buletina.configuration.Configuration;
import me.sirikon.buletina.errors.InitializationError;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class Database {

  private final Configuration configuration;
  private final Connection connection;

  @Inject
  public Database(final Configuration configuration) {
    this.configuration = configuration;

    registerPostgreSQLDriver();
    connection = connect();
    ensureTable();
  }

  private void registerPostgreSQLDriver() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (final ClassNotFoundException t) {
      throw new InitializationError("Failed to initialize PostgreSQL Driver", t);
    }
  }

  private Connection connect() {
    try {
      return DriverManager.getConnection(configuration.getDatabaseUrl());
    } catch (final SQLException t) {
      throw new InitializationError("Failed to initialize PostgreSQL database connection", t);
    }
  }

  private void ensureTable() {
    try {
      connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS subscriptions (
              id SERIAL,
              email VARCHAR(320) NOT NULL UNIQUE,
              cancellation_token VARCHAR(64) NOT NULL,
              PRIMARY KEY (id)
            );
          """).execute();
    } catch (final SQLException t) {
      throw new RuntimeException(t);
    }
  }

  public void insertSubscription(final String email, final String cancellationToken) {
    try {
      final var st = connection.prepareStatement("""
            INSERT INTO subscriptions
              (email, cancellation_token) VALUES (?,?)
            ON CONFLICT (email) DO NOTHING
          """);
      st.setString(1, email);
      st.setString(2, cancellationToken);
      st.execute();
    } catch (final SQLException t) {
      throw new RuntimeException(t);
    }
  }

}
