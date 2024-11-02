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
package eu.softpol.lib.jgpio.internal.gpiod;

import static eu.softpol.lib.jgpio.internal.FFMUtil.toNullableString;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.Line;
import eu.softpol.lib.jgpio.LineInputSession;
import eu.softpol.lib.jgpio.LineOutputSession;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod.gpiod_h;
import java.lang.foreign.MemorySegment;
import org.jspecify.annotations.Nullable;

public class GpiodLine implements Line {

  private final GpiodChip chip;
  /// struct gpiod_line pointer
  private final MemorySegment linePtr;

  private @Nullable Integer offset;
  private @Nullable String name;

  public GpiodLine(GpiodChip chip, MemorySegment linePtr) {
    this.chip = chip;
    this.linePtr = linePtr;
  }

  @Override
  public int offset() {
    throwWhenChipClosed();
    if (offset == null) {
      offset = gpiod_h.gpiod_line_offset(linePtr);
    }
    return offset;
  }

  @Override
  public @Nullable String name() {
    throwWhenChipClosed();
    if (name == null) {
      name = toNullableString(gpiod_h.gpiod_line_name(linePtr));
    }
    return name;
  }

  @Override
  public @Nullable String consumer() {
    throwWhenChipClosed();
    return toNullableString(gpiod_h.gpiod_line_consumer(linePtr));
  }

  @Override
  public Direction direction() {
    throwWhenChipClosed();
    var direction = gpiod_h.gpiod_line_direction(linePtr);
    if (direction == gpiod_h.GPIOD_LINE_DIRECTION_INPUT()) {
      return Direction.INPUT;
    } else {
      return Direction.OUTPUT;
    }
  }

  @Override
  public boolean isUsed() {
    throwWhenChipClosed();
    return gpiod_h.gpiod_line_is_used(linePtr);
  }

  @Override
  public LineInputSession openAsInput() {
    throwWhenChipClosed();
    return new GpiodLineInputSession(chip, linePtr);
  }

  @Override
  public LineInputSession openAsInput(Bias bias) {
    throwWhenChipClosed();
    return new GpiodLineInputSession(chip, linePtr, bias);
  }

  @Override
  public LineOutputSession openAsOutput() {
    throwWhenChipClosed();
    return new GpiodLineOutputSession(chip, linePtr);
  }

  @Override
  public LineOutputSession openAsOutput(DriveMode driveMode) {
    throwWhenChipClosed();
    return new GpiodLineOutputSession(chip, linePtr, driveMode);
  }

  @Override
  public String toString() {
    if (chip.isClosed()) {
      return "GpiodLine(closed)";
    }
    return "GpiodLine{" +
        "chip=" + chip +
        ", offset=" + offset() +
        ", name='" + name() + '\'' +
        '}';
  }

  private void throwWhenChipClosed() {
    if (chip.isClosed()) {
      throw new IllegalStateException("Chip for this line has been closed");
    }
  }

}
