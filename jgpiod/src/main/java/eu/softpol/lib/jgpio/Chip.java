package eu.softpol.lib.jgpio;

import java.io.Closeable;
import java.util.Optional;

/// This class represents GPIO controller chip
public interface Chip extends Closeable {

  /// Get chip name
  String name();

  /// Get chip label
  String label();

  /// Get number of lines in this chip
  int countLines();

  /// Find line with offset
  ///
  /// @param offset non-negative offset
  /// @return line with specified offset or empty
  Optional<Line> findLine(int offset);

  /// Get line by offset
  ///
  /// @param offset non-negative offset
  /// @return line with specified offset
  /// @throws JgpioException when line not found
  default Line getLine(int offset) {
    return findLine(offset)
        .orElseThrow(() -> new JgpioException("Cannot find line with offset " + offset));
  }

  /// Find first line with given name
  ///
  /// @param name line name
  /// @return line with specified name or empty
  Optional<Line> findLine(String name);

  /// Get first line with given name or throw exception when line not found
  ///
  /// @param name line name
  /// @return line with specified name
  /// @throws JgpioException when line not found
  default Line getLine(String name) {
    return findLine(name)
        .orElseThrow(() -> new JgpioException("Cannot find line with name '" + name + "'"));
  }

  @Override
  void close();
}
