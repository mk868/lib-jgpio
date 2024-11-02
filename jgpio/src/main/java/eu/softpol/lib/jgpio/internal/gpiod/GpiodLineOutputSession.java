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

import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.LineOutputSession;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod.gpiod_h;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;

public class GpiodLineOutputSession extends GpiodLineSession implements LineOutputSession {

  private static final Logger logger = System.getLogger(GpiodLineOutputSession.class.getName());

  public GpiodLineOutputSession(GpiodChip chip, MemorySegment linePtr) {
    super(chip, linePtr);
    try (var arena = Arena.ofConfined()) {
      var consumerPtr = arena.allocateFrom(GpiodChip.CONSUMER_NAME, StandardCharsets.US_ASCII);
      if (gpiod_h.gpiod_line_request_output(this.linePtr, consumerPtr, 0) != 0) {
        throw new JgpioException("JGPIO line request failed");
      }
    }
    logger.log(Level.DEBUG, "Line requested");
  }

  public GpiodLineOutputSession(GpiodChip chip, MemorySegment linePtr, DriveMode driveMode) {
    super(chip, linePtr);
    int flags = toFlags(driveMode);
    try (var arena = Arena.ofConfined()) {
      var consumerPtr = arena.allocateFrom(GpiodChip.CONSUMER_NAME, StandardCharsets.US_ASCII);
      if (gpiod_h.gpiod_line_request_output_flags(this.linePtr, consumerPtr, flags, 0) != 0) {
        throw new JgpioException("JGPIO line request failed");
      }
    }
    logger.log(Level.DEBUG, "Line requested");
  }

  @Override
  public void setDriveMode(DriveMode driveMode) {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    int flags = toFlags(driveMode);
    logger.log(Level.DEBUG, "Set driveMode to {0}", driveMode);
    if (gpiod_h.gpiod_line_set_flags(linePtr, flags) != 0) {
      throw new JgpioException("Cannot set driveMode");
    }
  }

  @Override
  public void write(boolean value) {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    logger.log(Level.DEBUG, "Write {0}", value);
    if (gpiod_h.gpiod_line_set_value(linePtr, value ? 1 : 0) != 0) {
      throw new JgpioException("Cannot write value");
    }
  }

  private int toFlags(DriveMode driveMode) {
    return switch (driveMode) {
      case PUSH_PULL -> 0;
      case OPEN_DRAIN -> gpiod_h.GPIOD_LINE_REQUEST_FLAG_OPEN_DRAIN();
      case OPEN_DRAIN_PULL_UP -> gpiod_h.GPIOD_LINE_REQUEST_FLAG_OPEN_DRAIN()
          + gpiod_h.GPIOD_LINE_REQUEST_FLAG_BIAS_PULL_UP();
      case OPEN_SOURCE -> gpiod_h.GPIOD_LINE_REQUEST_FLAG_OPEN_SOURCE();
      case OPEN_SOURCE_PULL_DOWN -> gpiod_h.GPIOD_LINE_REQUEST_FLAG_OPEN_SOURCE()
          + gpiod_h.GPIOD_LINE_REQUEST_FLAG_BIAS_PULL_DOWN();
    };
  }
}
