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
package eu.softpol.lib.jgpio.internal.gpiod2;

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;
import static eu.softpol.lib.jgpio.internal.FFMUtil.toNullableString;

import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.InputMode;
import eu.softpol.lib.jgpio.Line;
import eu.softpol.lib.jgpio.LineInputSession;
import eu.softpol.lib.jgpio.LineOutputSession;
import eu.softpol.lib.jgpio.OutputMode;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod2.gpiod_h;
import org.jspecify.annotations.Nullable;

public class Gpiod2Line implements Line {

  private final Gpiod2Chip chip;
  private final int offset;
  private @Nullable String name;

  public Gpiod2Line(Gpiod2Chip chip, int offset) {
    this.chip = chip;
    this.offset = offset;
  }

  @Override
  public int offset() {
    throwWhenChipClosed();
    return offset;
  }

  @Override
  public @Nullable String name() {
    throwWhenChipClosed();
    if (name == null) {
      try (var lineInfo = chip.getLineInfo(offset)) {
        name = toNullableString(gpiod_h.gpiod_line_info_get_name(lineInfo.ptr()));
      }
    }
    return name;
  }

  @Override
  public @Nullable String consumer() {
    throwWhenChipClosed();
    try (var lineInfo = chip.getLineInfo(offset)) {
      return toNullableString(gpiod_h.gpiod_line_info_get_consumer(lineInfo.ptr()));
    }
  }

  @Override
  public Direction direction() {
    throwWhenChipClosed();
    try (var lineInfo = chip.getLineInfo(offset)) {
      var direction = gpiod_h.gpiod_line_info_get_direction(lineInfo.ptr());
      if (direction == gpiod_h.GPIOD_LINE_DIRECTION_INPUT()) {
        return Direction.INPUT;
      } else {
        return Direction.OUTPUT;
      }
    }
  }

  @Override
  public boolean isUsed() {
    throwWhenChipClosed();
    try (var lineInfo = chip.getLineInfo(offset)) {
      return gpiod_h.gpiod_line_info_is_used(lineInfo.ptr());
    }
  }

  @Override
  public LineInputSession openAsInput(InputMode inputMode) {
    checkNonNull(inputMode, "inputMode");
    throwWhenChipClosed();
    return new Gpiod2LineInputSession(chip, offset, inputMode);
  }

  @Override
  public LineOutputSession openAsOutput(OutputMode outputMode) {
    checkNonNull(outputMode, "outputMode");
    throwWhenChipClosed();
    return new Gpiod2LineOutputSession(chip, offset, outputMode);
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
