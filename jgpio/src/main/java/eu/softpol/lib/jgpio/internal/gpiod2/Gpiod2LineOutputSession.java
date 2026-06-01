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

import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.LineOutputSession;
import eu.softpol.lib.jgpio.OutputMode;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod2.gpiod_h;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Objects;

public class Gpiod2LineOutputSession extends Gpiod2LineSession implements LineOutputSession {

  private static final Logger logger = System.getLogger(Gpiod2LineOutputSession.class.getName());

  private boolean value;

  public Gpiod2LineOutputSession(Gpiod2Chip chip, int offset, OutputMode outputMode) {
    super(
        chip,
        offset,
        Objects.requireNonNullElse(outputMode.consumer(), Gpiod2Chip.CONSUMER_NAME),
        new Settings(Direction.OUTPUT, null, outputMode.driveMode(), outputMode.initialValue())
    );
    this.value = Boolean.TRUE.equals(outputMode.initialValue());
    logger.log(Level.DEBUG, "Line requested");
  }

  @Override
  public void setDriveMode(DriveMode driveMode) {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    reconfigureRequest(new Settings(Direction.OUTPUT, null, driveMode, value));
  }

  @Override
  public void write(boolean value) {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    logger.log(Level.DEBUG, "Write {0}", value);
    var intValue = value ? gpiod_h.GPIOD_LINE_VALUE_ACTIVE() : gpiod_h.GPIOD_LINE_VALUE_INACTIVE();
    if (gpiod_h.gpiod_line_request_set_value(requestPtr, offset, intValue) < 0) {
      throw new JgpioException("Cannot write value");
    }
    this.value = value;
  }

  @Override
  public boolean isClosed() {
    return super.isClosed();
  }
}
