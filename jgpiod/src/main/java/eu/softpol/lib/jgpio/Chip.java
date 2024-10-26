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

import java.io.Closeable;
import java.util.Optional;

/// Represents a GPIO controller chip with methods to retrieve information about the chip and its
/// lines.
///
/// The chip object must be closed after use.
public interface Chip extends Closeable {

  /// Retrieves the name of the GPIO controller chip.
  ///
  /// @return the name of the chip.
  String name();

  /// Retrieves the label of the GPIO controller chip.
  ///
  /// @return the label of the chip.
  String label();

  /// Retrieves the number of lines associated with the GPIO controller chip.
  ///
  /// @return the number of lines
  int countLines();

  /// Finds a line by its offset.
  ///
  /// @param offset the non-negative offset
  /// @return an optional containing the line with the specified offset, or an empty optional if no
  /// line exists at the given offset
  Optional<Line> findLine(int offset);

  /// Gets a line by its offset.
  ///
  /// @param offset the non-negative offset
  /// @return the line with the specified offset
  /// @throws JgpioException if no line exists at the given offset
  default Line getLine(int offset) {
    return findLine(offset)
        .orElseThrow(() -> new JgpioException("Cannot find line with offset " + offset));
  }

  /// Finds the first line with the specified name.
  ///
  /// @param name the name of the line
  /// @return an optional containing the line with the specified name, or an empty optional if no
  /// line exists with the given name
  Optional<Line> findLine(String name);

  /// Gets the first line with the specified name or throw an exception when line not found
  ///
  /// @param name the name of the line
  /// @return the line with the specified name
  /// @throws JgpioException if no line exists with the given name
  default Line getLine(String name) {
    return findLine(name)
        .orElseThrow(() -> new JgpioException("Cannot find line with name '" + name + "'"));
  }

  /// Closes the GPIO controller chip, releasing any resources associated with it.
  ///
  /// This method must be called when the chip is no longer needed to ensure proper cleanup of
  /// resources. Once this method is called, further interactions with the chip or its lines are not
  /// allowed and will result with {@link IllegalStateException}.
  @Override
  void close();
}
