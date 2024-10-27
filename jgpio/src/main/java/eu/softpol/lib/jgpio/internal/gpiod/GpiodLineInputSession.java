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

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.LineInputSession;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod.gpiod_h;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;

public class GpiodLineInputSession extends GpiodLineSession implements LineInputSession {

  private static final Logger logger = System.getLogger(GpiodLineInputSession.class.getName());

  public GpiodLineInputSession(GpiodChip chip, MemorySegment linePtr) {
    super(chip, linePtr);
    try (var arena = Arena.ofConfined()) {
      var consumerPtr = arena.allocateFrom(GpiodChip.CONSUMER_NAME, StandardCharsets.US_ASCII);
      if (gpiod_h.gpiod_line_request_input(linePtr, consumerPtr) != 0) {
        throw new JgpioException("JGPIO line request failed");
      }
    }
    logger.log(Level.DEBUG, "Line requested");
  }

  public GpiodLineInputSession(GpiodChip chip, MemorySegment linePtr, Bias bias) {
    super(chip, linePtr);
    int flags = toFlags(bias);
    try (var arena = Arena.ofConfined()) {
      var consumerPtr = arena.allocateFrom(GpiodChip.CONSUMER_NAME, StandardCharsets.US_ASCII);
      if (gpiod_h.gpiod_line_request_input_flags(linePtr, consumerPtr, flags) != 0) {
        throw new JgpioException("JGPIO line request failed");
      }
    }
    logger.log(Level.DEBUG, "Line requested");
  }

  @Override
  public void setBias(Bias bias) {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    int flags = toFlags(bias);
    logger.log(Level.DEBUG, "Set bias to {0}", bias);
    if (gpiod_h.gpiod_line_set_flags(linePtr, flags) != 0) {
      throw new JgpioException("Cannot set bias");
    }
  }

  @Override
  public boolean read() {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    var result = gpiod_h.gpiod_line_get_value(linePtr);
    if (result < 0) {
      throw new JgpioException("Cannot read value");
    }
    return result == 1;
  }

  private int toFlags(Bias bias) {
    return switch (bias) {
      case HIGH_IMPEDANCE -> gpiod_h.GPIOD_LINE_REQUEST_FLAG_BIAS_DISABLE();
      case PULL_DOWN -> gpiod_h.GPIOD_LINE_REQUEST_FLAG_BIAS_PULL_DOWN();
      case PULL_UP -> gpiod_h.GPIOD_LINE_REQUEST_FLAG_BIAS_PULL_UP();
    };
  }
}
