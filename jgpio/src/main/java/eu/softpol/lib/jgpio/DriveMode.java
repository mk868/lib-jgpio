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

/// Enum representing the drive modes for a GPIO line.
///
/// The DriveMode defines the configuration of the GPIO line's output driver.
public enum DriveMode {
  /// Drive mode where the GPIO line is actively driven to both high and low states.
  PUSH_PULL,
  /// Drive mode where the GPIO line is set to open-drain. In this mode, the GPIO can either drive
  /// the line low or leave it floating.
  OPEN_DRAIN,
  /// Drive mode where the GPIO line is set to open-drain with an internal pull-up resistor. In this
  /// mode, the GPIO can drive the line low, or use the pull-up resistor to bring the line to a high
  /// state.
  OPEN_DRAIN_PULL_UP,
  /// Drive mode where the GPIO line is set to open-source. In this mode, the GPIO can either drive
  /// the line high or leave it floating.
  OPEN_SOURCE,
  /// Drive mode where the GPIO line is set to open-source with an internal pull-down resistor. In
  /// this mode, the GPIO can drive the line high, or use the pull-down resistor to bring the line
  /// to a low state.
  OPEN_SOURCE_PULL_DOWN;
}
