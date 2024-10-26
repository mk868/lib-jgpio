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

import java.io.Closeable;

/// The output line session represents the line requested for writing. The interface provides
/// methods responsible for managing the output line.
///
/// The methods of this interface should not be called after [Chip#close()].
public interface LineOutputSession extends Closeable {

  /// Writes the new value of the output line.
  ///
  /// @param value the signal level: `true` if the output line needs to be high, `false` if the
  ///              output line needs to be low
  void write(boolean value);

  /// Closes the line output session, releasing any associated resources.
  ///
  /// Once this method is called, further interactions with the session are not allowed and will
  /// result with [IllegalStateException].
  @Override
  void close();

}
