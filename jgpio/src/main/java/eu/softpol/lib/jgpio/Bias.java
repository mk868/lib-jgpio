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

/// Enum representing input line bias.
///
/// The Bias defines the possible bias resistors that can be applied to an input line.
public enum Bias {
  /// Bias is disabled, the line is in a high impedance state.
  HIGH_IMPEDANCE,
  /// The internal pull-up resistor is active on the line.
  PULL_UP,
  /// The internal pull-down resistor is active on the line.
  PULL_DOWN;
}
