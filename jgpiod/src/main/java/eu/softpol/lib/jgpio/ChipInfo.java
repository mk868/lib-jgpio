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

/// Immutable information about chip
public interface ChipInfo {

  /// Get chip name
  ///
  /// @return Chip name
  String name();

  /// Get chip label
  ///
  /// @return chip label
  String label();

  /// Count lines
  ///
  /// @return number of lines
  int countLines();

  /// Open chip
  ///
  /// @return chip object
  /// @see Jgpio#openChipByName(String)
  Chip open();

}
