package eu.softpol.lib.jgpio;

import java.io.Closeable;

/// Input line session, must be closed after use
///
/// The methods of this class should not be called after [Chip#close()].
public interface LineInputSession extends Closeable {

  void setBias(Bias bias);

  /// Read input line signal level
  ///
  /// @return signal level: `true`(HIGH) or `false`(LOW)
  boolean read();

  @Override
  void close();

}
