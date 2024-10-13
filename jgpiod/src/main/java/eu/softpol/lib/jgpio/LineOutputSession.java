package eu.softpol.lib.jgpio;

import java.io.Closeable;

/// Output line session, must be closed after use
///
/// The methods of this class should not be called after [Chip#close()].
public interface LineOutputSession extends Closeable {

  /// Write a new value of output line
  ///
  /// @param value signal level: `true`(HIGH) or `false`(LOW)
  void write(boolean value);

  @Override
  void close();

}
