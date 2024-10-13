package eu.softpol.lib.jgpio.internal.gpiod;

import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.LineOutputSession;
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

  @Override
  public void write(boolean value) {
    throwWhenChipClosed();
    throwWhenLineSessionClosed();
    logger.log(Level.DEBUG, "Write {0}", value);
    if (gpiod_h.gpiod_line_set_value(linePtr, value ? 1 : 0) != 0) {
      throw new JgpioException("Cannot write value");
    }
  }

}
