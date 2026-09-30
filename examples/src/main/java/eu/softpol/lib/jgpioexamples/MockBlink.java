package eu.softpol.lib.jgpioexamples;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.mock.JgpioMock;

/// [Blink] running on the mocked backend, no GPIO hardware needed.
public class MockBlink {

  public static void main(final String[] args) throws InterruptedException {
    // simulated hardware: a Raspberry Pi 5 like chip with an LED on GPIO14
    var mock = JgpioMock.builder()
        .chip("gpiochip0", c -> c
            .label("pinctrl-rp1")
            .line(14, "GPIO14"))
        .build();
    var led = mock.chip("gpiochip0").line("GPIO14");
    led.setWriteCallback(on -> System.out.println("LED " + (on ? "on" : "off")));

    blink(mock.jgpio());

    System.out.println("LED left " + (led.outputValue() ? "on" : "off")
                       + ", line released: " + !led.isRequested());
  }

  /// Same code as in [Blink], only [Jgpio] is passed in.
  private static void blink(final Jgpio jgpio) throws InterruptedException {
    try (
        var chip = jgpio.openChipByLabel("pinctrl-rp1");
        var gpio14 = chip.getLine("GPIO14")
            .openAsOutput()
    ) {
      for (var i = 0; i < 10; i++) {
        gpio14.write(true);
        Thread.sleep(500);
        gpio14.write(false);
        Thread.sleep(500);
      }
    }
  }

}
