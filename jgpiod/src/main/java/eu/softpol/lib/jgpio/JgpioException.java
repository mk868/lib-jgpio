package eu.softpol.lib.jgpio;

/// Base exception class
public class JgpioException extends RuntimeException {

  public JgpioException(String message) {
    super(message);
  }

  public JgpioException(String message, Throwable cause) {
    super(message, cause);
  }
}
