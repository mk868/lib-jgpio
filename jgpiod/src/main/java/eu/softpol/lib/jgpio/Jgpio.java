package eu.softpol.lib.jgpio;

import eu.softpol.lib.jgpio.internal.gpiod.GpiodJgpio;
import java.nio.file.Path;
import java.util.List;
import org.jspecify.annotations.Nullable;

/// Entry point for the JGPIO library
///
/// @see Jgpio#getInstance() for the default Jgpio implementation
public interface Jgpio {

  /// Open chip by name
  ///
  /// @param name chip name, for example `gpiochip1`
  /// @return chip session, must be closed after use
  /// @throws JgpioException when chip not found
  Chip openChipByName(String name);

  /// Open chip by label
  ///
  /// @param label chip label
  /// @return chip session, must be closed after use
  /// @throws JgpioException when chip not found
  Chip openChipByLabel(String label);

  /// Open chip by path
  ///
  /// @param path chip path in the filesystem
  /// @return chip session, must be closed after use
  /// @throws JgpioException when chip not found
  Chip openChipByPath(Path path);

  /// Open chip by number
  ///
  /// @param number chip number
  /// @return chip session, must be closed after use
  /// @throws JgpioException when chip not found
  Chip openChipByNumber(int number);

  /// List all chips in the system
  ///
  /// @return list of chips
  List<ChipInfo> getChips();

  /// Get native library version
  ///
  /// @return version string
  public @Nullable String version();


  /// Get Jgpio instance
  ///
  /// @return Jgpio instance
  static Jgpio getInstance() {
    return new GpiodJgpio();
  }

}
