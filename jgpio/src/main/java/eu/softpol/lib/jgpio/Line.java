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
package eu.softpol.lib.jgpio;

import org.jspecify.annotations.Nullable;

/// Interface representing a GPIO line.
///
/// The methods of this class should not be called after [Chip#close()].
public interface Line {

  /// Retrieves the offset of the GPIO line.
  ///
  /// @return the offset of the GPIO line
  int offset();

  /// Retrieves the name of the GPIO line.
  ///
  /// @return the name of the GPIO line, or null if the line has no name
  @Nullable String name();

  /// Retrieves the consumer of the GPIO line.
  ///
  /// @return the name of the consumer using the line, or null if the line is not being used or if
  /// the consumer is not defined
  @Nullable String consumer();

  /// Retrieves the current direction of the GPIO line.
  ///
  /// @return the direction of the GPIO line
  Direction direction();

  /// Checks if the GPIO line is currently in use.
  ///
  /// @return the `true` if the line is in use,`false` otherwise
  boolean isUsed();

  /// Opens the GPIO line as an input.
  ///
  /// @return the session for the input line, which must be closed after use
  default LineInputSession openAsInput() {
    return openAsInput(InputMode.builder().build());
  }

  /// Opens the GPIO line as an input with the specified bias.
  ///
  /// @param bias the desired input line bias
  /// @return the session for the input line, which must be closed after use
  default LineInputSession openAsInput(Bias bias) {
    return openAsInput(InputMode.builder().bias(bias).build());
  }

  /// Opens the GPIO line as an input with the specified input mode.
  ///
  /// @param inputMode the configuration to apply when requesting the line for input
  /// @return the session for the input line, which must be closed after use
  LineInputSession openAsInput(InputMode inputMode);

  /// Opens the GPIO line as an output.
  ///
  /// @return the session for the output line, which must be closed after use
  default LineOutputSession openAsOutput() {
    return openAsOutput(OutputMode.builder().build());
  }

  /// Opens the GPIO line as an output with the specified drive mode.
  ///
  /// @param driveMode the drive mode to be set for the GPIO line
  /// @return the session for the output line, which must be closed after use
  default LineOutputSession openAsOutput(DriveMode driveMode) {
    return openAsOutput(OutputMode.builder().driveMode(driveMode).build());
  }

  /// Opens the GPIO line as an output with the specified output mode.
  ///
  /// @param outputMode the configuration to apply when requesting the line for output
  /// @return the session for the output line, which must be closed after use
  LineOutputSession openAsOutput(OutputMode outputMode);

}
