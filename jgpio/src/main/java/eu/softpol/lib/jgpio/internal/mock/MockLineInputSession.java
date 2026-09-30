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

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.InputMode;
import eu.softpol.lib.jgpio.LineInputSession;
import eu.softpol.lib.jgpio.mock.MockLineRequest;

public class MockLineInputSession extends MockLineSession implements LineInputSession {

  public MockLineInputSession(MockChip chip, MockLineProbe line, InputMode inputMode) {
    super(chip, line, inputMode.consumer());
    var bias = inputMode.bias();
    if (bias != null) {
      line.setBias(bias);
    }
  }

  @Override
  protected MockLineRequest request() {
    return MockLineRequest.asInput(consumer());
  }

  @Override
  public void setBias(Bias bias) {
    checkNonNull(bias, "bias");
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    line.setBias(bias);
  }

  @Override
  public boolean read() {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    var inputLevel = line.readInputLevel();
    return switch (inputLevel) {
      case HIGH -> true;
      case LOW -> false;
      case HIGH_IMPEDANCE -> switch (line.bias()) {
        case HIGH_IMPEDANCE, PULL_DOWN -> false;
        case PULL_UP -> true;
      };
    };
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
