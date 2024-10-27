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

/// Represents the direction of a GPIO line.
public enum Direction {
  /// Represents the direction of a GPIO line set to input mode.
  ///
  /// When a GPIO line is configured as INPUT, it is used to read electrical signals from external
  /// devices. This mode is typically used for sensors, switches, or other input devices.
  INPUT,
  /// Represents the direction of a GPIO line set to output mode.
  ///
  /// When a GPIO line is configured as OUTPUT, it is used to send electrical signals to external
  /// devices. This mode is typically used for LEDs, actuators, or other output devices.
  OUTPUT;
}
