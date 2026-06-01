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

import eu.softpol.lib.jgpio.internal.OutputModeImpl.BuilderImpl;
import org.jspecify.annotations.Nullable;

/// Configuration used when opening a GPIO line as an output.
///
/// An output mode describes optional settings that should be applied when a line is requested for
/// output. Settings left unspecified use their documented defaults.
///
/// Instances are immutable. Use {@link #builder()} to create a new instance or {@link #toBuilder()}
/// to create a modified copy.
///
/// @see Line#openAsOutput(OutputMode)
/// @see DriveMode
public interface OutputMode {

  /// Returns the consumer name associated with the line request.
  ///
  /// The consumer name is used for diagnostics and may be visible in system tools such as
  /// `gpioinfo`. If not specified, the library default consumer name is used.
  ///
  /// @return the consumer name, or {@code null} if not specified
  @Nullable String consumer();

  /// Returns the output drive mode.
  ///
  /// If not specified, the backend or system default drive mode is used.
  ///
  /// @return the drive mode, or {@code null} if not specified
  @Nullable DriveMode driveMode();

  /// Returns the configured initial output value for the line.
  ///
  /// This value determines whether the line is set to a high or low state when it is requested as
  /// an output. If not configured, the effective initial value is {@code false}.
  ///
  /// @return the configured initial value, or {@code null} if not configured
  @Nullable Boolean initialValue();

  /// Creates a builder initialized with this mode's settings.
  ///
  /// @return a builder containing the current settings
  Builder toBuilder();

  /// Builder for {@link OutputMode}.
  ///
  /// All settings are optional. Unspecified settings use library, backend, or system defaults.
  interface Builder {

    /// Sets the consumer name associated with the line request.
    ///
    /// The consumer name is used for diagnostics and may be visible in system tools such as
    /// `gpioinfo`.
    ///
    /// @param consumer the consumer name
    /// @return this builder
    Builder consumer(String consumer);

    /// Clears the consumer name.
    ///
    /// After calling this method, the library default consumer name is used.
    ///
    /// @return this builder
    Builder clearConsumer();

    /// Sets the output drive mode.
    ///
    /// @param driveMode the drive mode
    /// @return this builder
    Builder driveMode(DriveMode driveMode);

    /// Clears the output drive mode.
    ///
    /// After calling this method, the backend or system default drive mode is used.
    ///
    /// @return this builder
    Builder clearDriveMode();

    /// Sets the initial output value.
    ///
    /// @param initialValue the initial value
    /// @return this builder
    Builder initialValue(boolean initialValue);

    /// Clears the configured initial output value.
    ///
    /// After calling this method, no initial value is configured. When opening a line as an output,
    /// the effective initial value defaults to {@code false}.
    ///
    /// @return this builder
    Builder clearInitialValue();

    /// Builds an immutable output mode.
    ///
    /// @return the created output mode
    OutputMode build();
  }

  /// Creates a new builder for {@link OutputMode}.
  ///
  /// @return a new output mode builder
  static OutputMode.Builder builder() {
    return new BuilderImpl();
  }
}
