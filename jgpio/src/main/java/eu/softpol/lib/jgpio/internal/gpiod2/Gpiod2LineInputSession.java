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

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.LineInputSession;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod2.gpiod_h;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public class Gpiod2LineInputSession extends Gpiod2LineSession implements LineInputSession {

  private static final Logger logger = System.getLogger(Gpiod2LineInputSession.class.getName());

  public Gpiod2LineInputSession(Gpiod2Chip chip, int offset) {
    super(chip, offset, new Settings(Direction.INPUT, null, null));
    logger.log(Level.DEBUG, "Line requested");
  }

  public Gpiod2LineInputSession(Gpiod2Chip chip, int offset, Bias bias) {
    super(chip, offset, new Settings(Direction.INPUT, bias, null));
    logger.log(Level.DEBUG, "Line requested");
  }

  @Override
  public void setBias(Bias bias) {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    reconfigureRequest(new Settings(Direction.INPUT, bias, null));
  }

  @Override
  public boolean read() {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    var result = gpiod_h.gpiod_line_request_get_value(requestPtr, offset);
    if (result < 0) {
      throw new JgpioException("Cannot read value");
    }
    return result == gpiod_h.GPIOD_LINE_VALUE_ACTIVE();
  }

  @Override
  public boolean isClosed() {
    return super.isClosed();
  }
}
