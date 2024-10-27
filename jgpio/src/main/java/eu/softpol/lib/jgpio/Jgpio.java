/*
 * Copyright 2024 SOFT-POL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.softpol.lib.jgpio;

import eu.softpol.lib.jgpio.internal.gpiod.GpiodJgpio;
import java.nio.file.Path;
import java.util.List;
import org.jspecify.annotations.Nullable;

/// Entry point for the JGPIO library
///
/// @see Jgpio#getInstance() for the default Jgpio implementation
public interface Jgpio {

  /// Opens a GPIO chip by its name.
  ///
  /// @param name the name of the GPIO chip to open, for example `gpiochip1`
  /// @return a Chip instance, must be closed after use
  /// @throws JgpioException if the chip cannot be found
  Chip openChipByName(String name);

  /// Opens a GPIO chip by its label.
  ///
  /// @param label the label of the GPIO chip to open
  /// @return a Chip instance, must be closed after use
  /// @throws JgpioException if the chip cannot be found
  Chip openChipByLabel(String label);

  /// Opens a GPIO chip by its file system path.
  ///
  /// @param path the file system path to the GPIO chip
  /// @return a Chip instance, must be closed after use
  /// @throws JgpioException if the chip cannot be found
  Chip openChipByPath(Path path);

  /// Opens a GPIO chip by its number.
  ///
  /// @param number the number of the GPIO chip to open
  /// @return a Chip instance, must be closed after use
  /// @throws JgpioException if the chip cannot be found
  Chip openChipByNumber(int number);

  /// Retrieves a list of all available GPIO chips on the system.
  ///
  /// @return a list of {@link ChipInfo} instances representing the available GPIO chips
  /// @throws JgpioException if there is an error accessing the GPIO chips
  List<ChipInfo> getChips();

  /// Retrieves the version of the libgpiod library.
  ///
  /// @return a string representing the version
  public @Nullable String version();

  /// Provides the default implementation of the {@link Jgpio} interface.
  ///
  /// @return An instance of the {@link Jgpio} interface.
  static Jgpio getInstance() {
    return new GpiodJgpio();
  }

}
