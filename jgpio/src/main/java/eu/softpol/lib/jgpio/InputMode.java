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

import eu.softpol.lib.jgpio.internal.InputModeImpl.BuilderImpl;
import org.jspecify.annotations.Nullable;

/// Configuration used when opening a GPIO line as an input.
///
/// An input mode describes optional settings that should be applied when a line is requested for
/// input. Settings left unspecified use their documented defaults.
///
/// Instances are immutable. Use {@link #builder()} to create a new instance or {@link #toBuilder()}
/// to create a modified copy.
///
/// @see Line#openAsInput(InputMode)
/// @see Bias
public interface InputMode {

  /// Returns the consumer name associated with the line request.
  ///
  /// The consumer name is used for diagnostics and may be visible in system tools such as
  /// `gpioinfo`. If not specified, the library default consumer name is used.
  ///
  /// @return the consumer name, or {@code null} if not specified
  @Nullable String consumer();

  /// Returns the input bias configuration.
  ///
  /// If not specified, the backend or system default bias is used.
  ///
  /// @return the bias configuration, or {@code null} if not specified
  @Nullable Bias bias();

  /// Creates a builder initialized with this mode's settings.
  ///
  /// @return a builder containing the current settings
  Builder toBuilder();

  /// Builder for {@link InputMode}.
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

    /// Sets the input bias configuration.
    ///
    /// @param bias the bias configuration
    /// @return this builder
    Builder bias(Bias bias);

    /// Clears the input bias configuration.
    ///
    /// After calling this method, the backend or system default bias is used.
    ///
    /// @return this builder
    Builder clearBias();

    /// Builds an immutable input mode.
    ///
    /// @return the created input mode
    InputMode build();
  }

  /// Creates a new builder for {@link InputMode}.
  ///
  /// @return a new input mode builder
  static InputMode.Builder builder() {
    return new BuilderImpl();
  }
}
