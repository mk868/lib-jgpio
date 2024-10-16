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

/// Input line session, must be closed after use
///
/// The methods of this class should not be called after [Chip#close()].
public interface LineInputSession extends Closeable {

  void setBias(Bias bias);

  /// Read input line signal level
  ///
  /// @return signal level: `true`(HIGH) or `false`(LOW)
  boolean read();

  @Override
  void close();

}
