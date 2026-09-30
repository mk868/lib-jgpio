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

import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.InputMode;
import eu.softpol.lib.jgpio.Line;
import eu.softpol.lib.jgpio.LineInputSession;
import eu.softpol.lib.jgpio.LineOutputSession;
import eu.softpol.lib.jgpio.OutputMode;
import eu.softpol.lib.jgpio.internal.JgpioExceptions;
import eu.softpol.lib.jgpio.mock.MockLineRequest;
import org.jspecify.annotations.Nullable;

/// Mocked [Line] API, bound to the [MockChip] it was obtained from.
public class MockLine implements Line {

  private final MockChip chip;
  private final MockLineProbe line;

  public MockLine(MockChip chip, MockLineProbe line) {
    this.chip = chip;
    this.line = line;
  }

  @Override
  public int offset() {
    throwWhenChipClosed();
    return line.offset();
  }

  @Override
  public @Nullable String name() {
    throwWhenChipClosed();
    return line.name();
  }

  @Override
  public @Nullable String consumer() {
    throwWhenChipClosed();
    return line.request()
        .map(MockLineRequest::consumer)
        .orElse(null);
  }

  @Override
  public Direction direction() {
    throwWhenChipClosed();
    return line.request()
        .filter(MockLineRequest.AsOutput.class::isInstance)
        .map(_ -> Direction.OUTPUT)
        .orElse(Direction.INPUT);
  }

  @Override
  public boolean isUsed() {
    throwWhenChipClosed();
    return line.isRequested();
  }

  @Override
  public LineInputSession openAsInput(InputMode inputMode) {
    checkNonNull(inputMode, "inputMode");
    throwWhenChipClosed();
    return new MockLineInputSession(chip, line, inputMode);
  }

  @Override
  public LineOutputSession openAsOutput(OutputMode outputMode) {
    checkNonNull(outputMode, "outputMode");
    throwWhenChipClosed();
    return new MockLineOutputSession(chip, line, outputMode);
  }

  private void throwWhenChipClosed() {
    if (chip.isClosed()) {
      throw JgpioExceptions.chipForLineClosed();
    }
  }
}
