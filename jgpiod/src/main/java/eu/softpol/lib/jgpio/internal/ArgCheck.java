package eu.softpol.lib.jgpio.internal;

import org.jspecify.annotations.Nullable;

public class ArgCheck {

  private ArgCheck() {
  }

  public static void checkNonNegative(int num, String argName) {
    if (num < 0) {
      throw new IllegalArgumentException("Argument '%s' must be >= 0".formatted(argName));
    }
  }

  public static void checkNonNull(@Nullable Object obj, String argName) {
    if (obj == null) {
      throw new IllegalArgumentException("Argument '%s' must not be null".formatted(argName));
    }
  }
}
