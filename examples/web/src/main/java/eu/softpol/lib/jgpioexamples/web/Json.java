package eu.softpol.lib.jgpioexamples.web;

/// Just enough JSON writing for this example, to keep it free of dependencies.
final class Json {

  private Json() {
  }

  /// Returns the value as a JSON literal: `null`, a boolean, a number or a string.
  static String json(final Object value) {
    return switch (value) {
      case null -> "null";
      case Boolean _, Number _ -> value.toString();
      case Enum<?> e -> quote(e.name());
      default -> quote(value.toString());
    };
  }

  private static String quote(final String s) {
    var sb = new StringBuilder(s.length() + 2).append('"');
    for (var c : s.toCharArray()) {
      switch (c) {
        case '"' -> sb.append("\\\"");
        case '\\' -> sb.append("\\\\");
        default -> {
          if (c < 0x20) {
            sb.append("\\u%04x".formatted((int) c));
          } else {
            sb.append(c);
          }
        }
      }
    }
    return sb.append('"').toString();
  }

}
