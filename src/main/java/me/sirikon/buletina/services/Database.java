package me.sirikon.buletina.services;

import me.sirikon.buletina.errors.InitializationError;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class Database {

  private final Connection connection;

  public Database() {
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:data.db");
    } catch (final SQLException t) {
      throw new InitializationError("Failed to initialize SQLite database connection", t);
    }
    ensureTable();
  }

  private void ensureTable() {
    try {
      connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS subscriptions (
            	id INTEGER PRIMARY KEY AUTOINCREMENT,
            	email VARCHAR(320) NOT NULL UNIQUE,
            	cancellation_token VARCHAR(64) NOT NULL
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
