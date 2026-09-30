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
package eu.softpol.lib.jgpio.internal.mock;

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;

import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.LineOutputSession;
import eu.softpol.lib.jgpio.OutputMode;
import eu.softpol.lib.jgpio.mock.MockLineRequest;

public class MockLineOutputSession extends MockLineSession implements LineOutputSession {

  public MockLineOutputSession(MockChip chip, MockLineProbe line, OutputMode outputMode) {
    super(chip, line, outputMode.consumer());
    var driveMode = outputMode.driveMode();
    if (driveMode != null) {
      line.setDriveMode(driveMode);
    }
    // like a real request, the line is always driven: to the initial value, or low if not set
    var initialValue = Boolean.TRUE.equals(outputMode.initialValue());
    try {
      line.writeOutputValue(initialValue);
    } catch (RuntimeException e) {
      // the caller never receives this session, so release the line
      close();
      throw e;
    }
  }

  @Override
  protected MockLineRequest request() {
    return MockLineRequest.asOutput(consumer());
  }

  @Override
  public void setDriveMode(DriveMode driveMode) {
    checkNonNull(driveMode, "driveMode");
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    line.setDriveMode(driveMode);
  }

  @Override
  public void write(boolean value) {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    line.writeOutputValue(value);
  }

  @Override
  public boolean isClosed() {
    return super.isClosed();
  }

  @Override
  public void close() {
    super.close();
  }
}
