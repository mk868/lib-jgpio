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

import static eu.softpol.lib.jgpio.internal.FFMUtil.isNull;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod2.gpiod_h;
import java.io.Closeable;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import org.jspecify.annotations.Nullable;

public abstract class Gpiod2LineSession implements Closeable {

  private static final Logger logger = System.getLogger(Gpiod2LineSession.class.getName());

  protected final Gpiod2Chip chip;
  protected final int offset;
  protected final MemorySegment requestPtr;

  protected boolean closed = false;

  protected Gpiod2LineSession(Gpiod2Chip chip, int offset, Settings settings) {
    this.chip = chip;
    this.offset = offset;
    this.requestPtr = createRequest(chip, offset, settings);
    chip.registerSession(this);
  }

  private static MemorySegment createRequest(Gpiod2Chip chip, int offset, Settings settings) {
    MemorySegment reqCfgPtr = null;
    MemorySegment settingsPtr = null;
    MemorySegment lineCfgPtr = null;

    try (var arena = Arena.ofConfined()) {
      reqCfgPtr = gpiod_h.gpiod_request_config_new();
      if (isNull(reqCfgPtr)) {
        throw new JgpioException("JGPIO line request failed: cannot allocate request config");
      }

      var consumerPtr = arena.allocateFrom(Gpiod2Chip.CONSUMER_NAME, StandardCharsets.US_ASCII);
      gpiod_h.gpiod_request_config_set_consumer(reqCfgPtr, consumerPtr);

      settingsPtr = gpiod_h.gpiod_line_settings_new();
      if (isNull(settingsPtr)) {
        throw new JgpioException("JGPIO line request failed: cannot allocate line settings");
      }

      applySettings(settingsPtr, settings);

      lineCfgPtr = gpiod_h.gpiod_line_config_new();
      if (isNull(lineCfgPtr)) {
        throw new JgpioException("JGPIO line request failed: cannot allocate line config");
      }

      int[] offsets = {offset};
      var offsetsPtr = arena.allocateFrom(ValueLayout.JAVA_INT, offsets);
      if (gpiod_h.gpiod_line_config_add_line_settings(lineCfgPtr, offsetsPtr, 1, settingsPtr) < 0) {
        throw new JgpioException("JGPIO line request failed: cannot add line settings");
      }

      var request = chip.requestLines(reqCfgPtr, lineCfgPtr);
      if (isNull(request)) {
        throw new JgpioException("JGPIO line request failed: cannot request line");
      }
      return request;
    } finally {
      if (lineCfgPtr != null && !isNull(lineCfgPtr)) {
        gpiod_h.gpiod_line_config_free(lineCfgPtr);
      }
      if (settingsPtr != null && !isNull(settingsPtr)) {
        gpiod_h.gpiod_line_settings_free(settingsPtr);
      }
      if (reqCfgPtr != null && !isNull(reqCfgPtr)) {
        gpiod_h.gpiod_request_config_free(reqCfgPtr);
      }
    }
  }

  private static void applySettings(MemorySegment settingsPtr, Settings settings) {
    var intDirection = switch (settings.direction) {
      case INPUT -> gpiod_h.GPIOD_LINE_DIRECTION_INPUT();
      case OUTPUT -> gpiod_h.GPIOD_LINE_DIRECTION_OUTPUT();
    };
    gpiod_h.gpiod_line_settings_set_direction(settingsPtr, intDirection);
    if (settings.bias != null) {
      var intBias = switch (settings.bias) {
        case HIGH_IMPEDANCE -> gpiod_h.GPIOD_LINE_BIAS_DISABLED();
        case PULL_UP -> gpiod_h.GPIOD_LINE_BIAS_PULL_UP();
        case PULL_DOWN -> gpiod_h.GPIOD_LINE_BIAS_PULL_DOWN();
      };
      gpiod_h.gpiod_line_settings_set_bias(settingsPtr, intBias);
    }
    if (settings.driveMode != null) {
      var intDrive = switch (settings.driveMode) {
        case PUSH_PULL -> gpiod_h.GPIOD_LINE_DRIVE_PUSH_PULL();
        case OPEN_DRAIN, OPEN_DRAIN_PULL_UP -> gpiod_h.GPIOD_LINE_DRIVE_OPEN_DRAIN();
        case OPEN_SOURCE, OPEN_SOURCE_PULL_DOWN -> gpiod_h.GPIOD_LINE_DRIVE_OPEN_SOURCE();
      };
      var intBias = switch (settings.driveMode) {
        case PUSH_PULL, OPEN_DRAIN, OPEN_SOURCE -> gpiod_h.GPIOD_LINE_BIAS_DISABLED();
        case OPEN_DRAIN_PULL_UP -> gpiod_h.GPIOD_LINE_BIAS_PULL_UP();
        case OPEN_SOURCE_PULL_DOWN -> gpiod_h.GPIOD_LINE_BIAS_PULL_DOWN();
      };
      gpiod_h.gpiod_line_settings_set_drive(settingsPtr, intDrive);
      gpiod_h.gpiod_line_settings_set_bias(settingsPtr, intBias);
    }
  }

  @Override
  public void close() {
    if (closed) {
      throw new JgpioException("The line input session has already closed");
    }
    closed = true;
    logger.log(Level.DEBUG, "Line release");
    gpiod_h.gpiod_line_request_release(requestPtr);
    chip.unregisterSession(this);
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

  protected void reconfigureRequest(Settings settings) {
    MemorySegment settingsPtr = null;
    MemorySegment lineCfgPtr = null;

    try (var arena = Arena.ofConfined()) {
      settingsPtr = gpiod_h.gpiod_line_settings_new();
      if (isNull(settingsPtr)) {
        throw new JgpioException("JGPIO request reconfigure failed: cannot allocate line settings");
      }

      lineCfgPtr = gpiod_h.gpiod_line_config_new();
      if (isNull(lineCfgPtr)) {
        throw new JgpioException("JGPIO request reconfigure failed: cannot allocate line config");
      }

      applySettings(settingsPtr, settings);

      int[] offsets = {offset};
      var offsetsPtr = arena.allocateFrom(ValueLayout.JAVA_INT, offsets);
      if (gpiod_h.gpiod_line_config_add_line_settings(lineCfgPtr, offsetsPtr, 1, settingsPtr) < 0) {
        throw new JgpioException(
            "JGPIO request reconfigure failed: cannot add line settings to config");
      }

      if (gpiod_h.gpiod_line_request_reconfigure_lines(requestPtr, lineCfgPtr) < 0) {
        throw new JgpioException("JGPIO request reconfigure failed");
      }
    } finally {
      if (lineCfgPtr != null && !isNull(lineCfgPtr)) {
        gpiod_h.gpiod_line_config_free(lineCfgPtr);
      }
      if (settingsPtr != null && !isNull(settingsPtr)) {
        gpiod_h.gpiod_line_settings_free(settingsPtr);
      }
    }
  }

  protected record Settings(
      Direction direction,
      @Nullable Bias bias,
      @Nullable DriveMode driveMode
  ) {

  }
}
