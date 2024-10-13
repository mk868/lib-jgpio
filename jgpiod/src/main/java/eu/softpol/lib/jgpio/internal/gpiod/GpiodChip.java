package eu.softpol.lib.jgpio.internal.gpiod;

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNegative;
import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;
import static eu.softpol.lib.jgpio.internal.FFMUtil.isNull;
import static eu.softpol.lib.jgpio.internal.FFMUtil.toNonNullString;

import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.Line;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod.gpiod_h;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

public class GpiodChip implements Chip {

  protected static final String CONSUMER_NAME = "JGPIO";

  /// struct gpiod_chip pointer
  private final MemorySegment chipPtr;

  private boolean closed = false;

  @SuppressWarnings("java:S117")
  private GpiodChip(MemorySegment chipPtr) {
    this.chipPtr = chipPtr;
  }

  public static GpiodChip openByName(String name) {
    try (var arena = Arena.ofConfined()) {
      var namePtr = arena.allocateFrom(name, StandardCharsets.US_ASCII);
      var chipPtr = gpiod_h.gpiod_chip_open_by_name(namePtr);
      if (isNull(chipPtr)) {
        throw new JgpioException(
            "Cannot open chip with name '%s', no resource or lack of permissions".formatted(name));
      }
      return new GpiodChip(chipPtr);
    }
  }

  public static GpiodChip openByNumber(int number) {
    var chipPtr = gpiod_h.gpiod_chip_open_by_number(number);
    if (isNull(chipPtr)) {
      throw new JgpioException(
          "Cannot open chip with number %d, no resource or lack of permissions".formatted(number));
    }
    return new GpiodChip(chipPtr);
  }

  public static GpiodChip openByLabel(String label) {
    try (var arena = Arena.ofConfined()) {
      var labelPtr = arena.allocateFrom(label, StandardCharsets.US_ASCII);
      var chipPtr = gpiod_h.gpiod_chip_open_by_label(labelPtr);
      if (isNull(chipPtr)) {
        throw new JgpioException(
            "Cannot open chip with label '%s', no resource or lack of permissions"
                .formatted(label));
      }
      return new GpiodChip(chipPtr);
    }
  }

  public static GpiodChip openByPath(Path path) {
    try (var arena = Arena.ofConfined()) {
      var pathPtr = arena.allocateFrom(path.toAbsolutePath().toString(), StandardCharsets.US_ASCII);
      var chipPtr = gpiod_h.gpiod_chip_open(pathPtr);
      if (isNull(chipPtr)) {
        throw new JgpioException(
            "Cannot open chip with path '%s', no resource or lack of permissions".formatted(path));
      }
      return new GpiodChip(chipPtr);
    }
  }

  @Override
  public String name() {
    throwWhenChipClosed();
    return toNonNullString(gpiod_h.gpiod_chip_name(chipPtr));
  }

  @Override
  public String label() {
    throwWhenChipClosed();
    return toNonNullString(gpiod_h.gpiod_chip_label(chipPtr));
  }

  @Override
  public int countLines() {
    throwWhenChipClosed();
    return gpiod_h.gpiod_chip_num_lines(chipPtr);
  }

  @Override
  public Optional<Line> findLine(int offset) {
    throwWhenChipClosed();
    checkNonNegative(offset, "offset");
    var linePtr = gpiod_h.gpiod_chip_get_line(chipPtr, offset);
    if (isNull(linePtr)) {
      return Optional.empty();
    }
    return Optional.of(new GpiodLine(this, linePtr));
  }

  @Override
  public Optional<Line> findLine(String name) {
    throwWhenChipClosed();
    checkNonNull(name, "name");
    try (var arena = Arena.ofConfined()) {
      var namePtr = arena.allocateFrom(name, StandardCharsets.US_ASCII);
      var linePtr = gpiod_h.gpiod_chip_find_line(chipPtr, namePtr);
      if (isNull(linePtr)) {
        return Optional.empty();
      }
      return Optional.of(new GpiodLine(this, linePtr));
    }
  }

  @Override
  public void close() {
    throwWhenChipClosed();
    if (!closed) {
      closed = true;
      gpiod_h.gpiod_chip_close(chipPtr);
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

  boolean isClosed() {
    return closed;
  }

  private void throwWhenChipClosed() {
    if (closed) {
      throw new IllegalStateException("Chip has been closed");
    }
  }
}
