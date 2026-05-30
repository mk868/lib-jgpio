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

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNegative;
import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;
import static eu.softpol.lib.jgpio.internal.FFMUtil.isNull;
import static eu.softpol.lib.jgpio.internal.FFMUtil.toNonNullString;

import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.Line;
import eu.softpol.lib.jgpio.internal.CloseablePtr;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod2.gpiod_h;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Gpiod2Chip implements Chip {

  protected static final String CONSUMER_NAME = "JGPIO";

  private final List<Gpiod2LineSession> activeSessions = new ArrayList<>();
  private final MemorySegment chipPtr;
  private final MemorySegment chipInfoPtr;

  private boolean closed = false;

  private Gpiod2Chip(MemorySegment chipPtr, MemorySegment chipInfoPtr) {
    this.chipPtr = chipPtr;
    this.chipInfoPtr = chipInfoPtr;
  }

  public static Gpiod2Chip openByPath(Path path) {
    try (var arena = Arena.ofConfined()) {
      var pathPtr = arena.allocateFrom(path.toAbsolutePath().toString(), StandardCharsets.US_ASCII);
      var chipPtr = gpiod_h.gpiod_chip_open(pathPtr);
      if (isNull(chipPtr)) {
        throw new JgpioException(
            "Cannot open chip with path '%s', no resource or lack of permissions".formatted(path));
      }
      var chipInfoPtr = gpiod_h.gpiod_chip_get_info(chipPtr);
      if (isNull(chipInfoPtr)) {
        gpiod_h.gpiod_chip_close(chipPtr);
        throw new JgpioException(
            "Cannot open chip with path '%s', no resource or lack of permissions".formatted(path));
      }
      return new Gpiod2Chip(chipPtr, chipInfoPtr);
    }
  }

  @Override
  public String name() {
    throwWhenChipClosed();
    return toNonNullString(gpiod_h.gpiod_chip_info_get_name(chipInfoPtr));
  }

  @Override
  public String label() {
    throwWhenChipClosed();
    return toNonNullString(gpiod_h.gpiod_chip_info_get_label(chipInfoPtr));
  }

  @Override
  public int countLines() {
    throwWhenChipClosed();
    return (int) gpiod_h.gpiod_chip_info_get_num_lines(chipInfoPtr);
  }

  @Override
  public Optional<Line> findLine(int offset) {
    throwWhenChipClosed();
    checkNonNegative(offset, "offset");
    if (offset >= countLines()) {
      return Optional.empty();
    }
    return Optional.of(new Gpiod2Line(this, offset));
  }

  @Override
  public Optional<Line> findLine(String name) {
    throwWhenChipClosed();
    checkNonNull(name, "name");
    try (var arena = Arena.ofConfined()) {
      var namePtr = arena.allocateFrom(name, StandardCharsets.US_ASCII);
      var offset = gpiod_h.gpiod_chip_get_line_offset_from_name(chipPtr, namePtr);
      if (offset < 0) {
        return Optional.empty();
      }
      return Optional.of(new Gpiod2Line(this, offset));
    }
  }

  @Override
  public void close() {
    throwWhenChipClosed();
    if (closed) {
      return;
    }
    closed = true;
    var sessionsToClose = new ArrayList<>(activeSessions);
    activeSessions.clear();

    RuntimeException closeFailure = null;
    for (var session : sessionsToClose) {
      try {
        session.close();
      } catch (RuntimeException e) {
        if (closeFailure == null) {
          closeFailure = e;
        } else {
          closeFailure.addSuppressed(e);
        }
      }
    }

    gpiod_h.gpiod_chip_info_free(chipInfoPtr);
    gpiod_h.gpiod_chip_close(chipPtr);

    if (closeFailure != null) {
      throw closeFailure;
    }
  }

  @Override
  public String toString() {
    if (closed) {
      return "GpiodChip(closed)";
    }
    return "GpiodChip{" +
           "name='" + name() + '\'' +
           ", label='" + label() + '\'' +
           '}';
  }

  protected CloseablePtr getLineInfo(int offset) {
    throwWhenChipClosed();
    checkNonNegative(offset, "offset");
    var lineInfoPtr = gpiod_h.gpiod_chip_get_line_info(chipPtr, offset);
    if (isNull(lineInfoPtr)) {
      throw new IllegalStateException("Cannot get line info");
    }
    return new CloseablePtr() {
      @Override
      public MemorySegment ptr() {
        return lineInfoPtr;
      }

      @Override
      public void close() {
        gpiod_h.gpiod_line_info_free(lineInfoPtr);
      }
    };
  }

  protected MemorySegment requestLines(MemorySegment reqCfgPtr, MemorySegment lineCfgPtr) {
    return gpiod_h.gpiod_chip_request_lines(chipPtr, reqCfgPtr, lineCfgPtr);
  }

  boolean isClosed() {
    return closed;
  }

  void registerSession(Gpiod2LineSession session) {
    activeSessions.add(session);
  }

  void unregisterSession(Gpiod2LineSession session) {
    activeSessions.remove(session);
  }

  private void throwWhenChipClosed() {
    if (closed) {
      throw new IllegalStateException("Chip has been closed");
    }
  }
}
