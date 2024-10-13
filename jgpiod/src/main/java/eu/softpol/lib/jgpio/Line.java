package eu.softpol.lib.jgpio;

import org.jspecify.annotations.Nullable;

/// Chip line definition
///
/// The methods of this class should not be called after [Chip#close()].
public interface Line {

  /// Get line offset
  ///
  /// @return non-negative offset number
  int offset();

  /// get line name
  ///
  /// @return line name or null
  public @Nullable String name();

  /// get the name of the consumer which current uses the line, the value may not be null only when
  /// [#isUsed()] returns `true`.
  ///
  /// @return consumer name
  public @Nullable String consumer();

  /// Get the currently defined line direction
  Direction direction();

  /// check if the line is in use
  ///
  /// @return true when in use
  boolean isUsed();

  /// Request line for reading
  ///
  /// @return line session, must be closed after use
  LineInputSession openAsInput();

  /// Request line for reading
  ///
  /// @param bias input line bias value
  /// @return line session, must be closed after use
  LineInputSession openAsInput(Bias bias);

  /// Request line for writing
  ///
  /// @return line session, must be closed after use
  LineOutputSession openAsOutput();

}
