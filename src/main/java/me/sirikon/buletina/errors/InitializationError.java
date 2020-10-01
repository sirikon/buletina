package me.sirikon.buletina.errors;

public class InitializationError extends RuntimeException {
  public InitializationError(final String message) {
    super(message);
  }
  public InitializationError(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
