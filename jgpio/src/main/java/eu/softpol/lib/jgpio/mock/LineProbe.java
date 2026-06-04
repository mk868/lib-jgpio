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

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.DriveMode;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

/// Access to a mocked GPIO line, used by tests to drive inputs and verify outputs.
///
/// Unlike [eu.softpol.lib.jgpio.Line], this object is not affected by closing the chip.
public interface LineProbe {

  int offset();

  @Nullable String name();

  /// Sets the signal seen by input sessions on this line.
  void setInputLevel(InputLevel level);

  /// Returns the value this line was last driven to, `false` initially.
  ///
  /// Opening an output session drives the line to its initial value, `false` if none is set;
  /// each write updates it. The value is kept after the session is closed.
  boolean outputValue();

  default boolean isRequested() {
    return request().isPresent();
  }

  /// Use to get information about the current request
  Optional<MockLineRequest> request();

  /// Returns the bias currently configured on this line, [Bias#HIGH_IMPEDANCE] initially.
  ///
  /// Updated when an input session is opened with a bias or changes it. Opening a session without
  /// a bias keeps the current one. The value is kept after the session is closed.
  Bias bias();

  /// Returns the drive mode currently configured on this line, [DriveMode#PUSH_PULL] initially.
  ///
  /// Updated when an output session is opened with a drive mode or changes it. Opening a session
  /// without a drive mode keeps the current one. The value is kept after the session is closed.
  DriveMode driveMode();

  /// Simulate line request by another application
  void setExternallyRequested(MockLineRequest request);

  /// Clear line request by another application
  void clearExternallyRequested();

  /// Sets the callback called with each value written to this line.
  ///
  /// Called after every successful [eu.softpol.lib.jgpio.LineOutputSession#write(boolean)] and when
  /// an output session is opened, with its initial value (`false` if none is set). It runs
  /// synchronously on the thread of the code under test. The probe state is updated before the
  /// callback is called, so an exception thrown by the callback is propagated to the code under
  /// test, but the written value remains visible through [#outputValue()]. When thrown on open,
  /// the open fails and the line stays free. Replaces any previously set write callback and stays
  /// registered after the chip is closed.
  void setWriteCallback(Consumer<Boolean> callback);

  /// Removes the write callback.
  void clearWriteCallback();

  /// Sets the callback supplying the input level on each read of this line.
  ///
  /// While set, it takes precedence over [#setInputLevel(InputLevel)]. The returned level goes
  /// through the same bias handling, so [InputLevel#HIGH_IMPEDANCE] follows the line's bias. It
  /// runs synchronously on the thread of the code under test; an exception thrown by the callback
  /// is propagated to it. Replaces any previously set read callback and stays registered after the
  /// chip is closed.
  void setReadCallback(Supplier<InputLevel> callback);

  /// Removes the read callback, reads use the level set by [#setInputLevel(InputLevel)] again.
  void clearReadCallback();

}
