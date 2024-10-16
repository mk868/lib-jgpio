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

import org.jspecify.annotations.Nullable;

/// Chip line definition
///
/// The methods of this class should not be called after [Chip#close()].
public interface Line {

  /// Get line offset
  ///
  /// @return non-negative offset number
  int offset();

  /// get line name
  ///
  /// @return line name or null
  public @Nullable String name();

  /// get the name of the consumer which current uses the line, the value may not be null only when
  /// [#isUsed()] returns `true`.
  ///
  /// @return consumer name
  public @Nullable String consumer();

  /// Get the currently defined line direction
  Direction direction();

  /// check if the line is in use
  ///
  /// @return true when in use
  boolean isUsed();

  /// Request line for reading
  ///
  /// @return line session, must be closed after use
  LineInputSession openAsInput();

  /// Request line for reading
  ///
  /// @param bias input line bias value
  /// @return line session, must be closed after use
  LineInputSession openAsInput(Bias bias);

  /// Request line for writing
  ///
  /// @return line session, must be closed after use
  LineOutputSession openAsOutput();

}
