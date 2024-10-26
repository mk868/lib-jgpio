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

/// An interface that represents immutable information of a GPIO chip.
///
/// An instance of this interface can be considered a snapshot of the chip's state. No IO actions
/// are performed when calling {@link ChipInfo#name()}, {@link ChipInfo#label()} or
/// {@link ChipInfo#countLines()} methods.
public interface ChipInfo {

  /// Gets the name of the GPIO chip.
  ///
  /// @return the name of the chip
  String name();

  /// Gets the label of the GPIO chip.
  ///
  /// @return the label of the chip
  String label();

  /// Gets the number of lines associated with the GPIO chip.
  ///
  /// @return the number of lines
  int countLines();

  /// Opens the GPIO chip represented by this ChipInfo interface.
  ///
  /// @return a Chip instance that allows interaction with the GPIO controller chip
  /// @see Jgpio#openChipByName(String)
  Chip open();

}
