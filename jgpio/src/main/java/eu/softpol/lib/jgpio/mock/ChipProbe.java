/*
 * Copyright 2024-2026 SOFT-POL
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
package eu.softpol.lib.jgpio.mock;

/// Access to a mocked GPIO chip, used by tests for setup and verification.
///
/// Unlike [eu.softpol.lib.jgpio.Chip], this object is not affected by closing the chip.
public interface ChipProbe {

  String name();

  String label();

  /// Returns the probe of the line with the given offset.
  ///
  /// @throws IllegalArgumentException if no such line was configured
  LineProbe line(int offset);

  /// Returns the probe of the first line with the given name.
  ///
  /// @throws IllegalArgumentException if no such line was configured
  LineProbe line(String name);
}
