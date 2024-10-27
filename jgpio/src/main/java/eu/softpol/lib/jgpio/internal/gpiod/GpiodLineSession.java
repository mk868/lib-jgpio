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
import eu.softpol.lib.jgpio.internal.ffm.libgpiod.gpiod_h;
import java.io.Closeable;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.foreign.MemorySegment;

public abstract class GpiodLineSession implements Closeable {

  private static final Logger logger = System.getLogger(GpiodLineSession.class.getName());

  protected final GpiodChip chip;
  /// struct gpiod_line pointer
  protected final MemorySegment linePtr;

  protected boolean closed = false;

  protected GpiodLineSession(GpiodChip chip, MemorySegment linePtr) {
    this.chip = chip;
    this.linePtr = linePtr;
  }

  @Override
  public void close() {
    if (closed) {
      throw new JgpioException("The line input session has already closed");
    }
    closed = true;
    if (chip.isClosed()) {
      logger.log(Level.WARNING, "Line release after chip close");
      // NOP, gpiod took care of releasing this line
    } else {
      logger.log(Level.DEBUG, "Line release");
      gpiod_h.gpiod_line_release(linePtr);
      gpiod_h.gpiod_line_update(linePtr);
    }
  }

  protected void throwWhenLineSessionClosed() {
    if (closed) {
      throw new IllegalStateException("Line Session has been closed");
    }
  }

  protected void throwWhenChipClosed() {
    if (chip.isClosed()) {
      throw new IllegalStateException("Chip for this line has been closed");
    }
  }

}
