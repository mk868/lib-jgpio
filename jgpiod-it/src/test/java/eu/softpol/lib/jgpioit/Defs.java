package eu.softpol.lib.jgpioit;

import eu.softpol.lib.jgpioit.util.TestChip;
import eu.softpol.lib.jgpioit.util.TestPin;
import eu.softpol.lib.jgpioit.util.TwoPins;
import java.util.List;

// describes the test platform
public class Defs {

  private static final TestChip CHIP_GPIOCHIP0 = new TestChip(
      "gpiochip0",
      "7022000.pinctrl",
      64);
  private static final TestChip CHIP_GPIOCHIP1 = new TestChip(
      "gpiochip1",
      "300b000.pinctrl",
      256);

  // chips in the system
  public static final List<TestChip> CHIPS = List.of(
      CHIP_GPIOCHIP0,
      CHIP_GPIOCHIP1
  );

  public static final TwoPins CONNECTED_PINS = new TwoPins(
      new TestPin(CHIP_GPIOCHIP1, 96 + 21, "PD21"),
      new TestPin(CHIP_GPIOCHIP1, 96 + 22, "PD22")
  );

  // floating pin
  public static final TestPin UNCONNECTED_PIN = new TestPin(CHIP_GPIOCHIP1, 64 + 9, "PC09");

  public static final TestPin PIN_WITHOUT_NAME = new TestPin(CHIP_GPIOCHIP1, 0, null);
  public static final TestPin PIN_WITH_NAME = new TestPin(CHIP_GPIOCHIP1, 64 + 9, "PC09");

}
