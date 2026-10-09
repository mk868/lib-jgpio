package eu.softpol.lib.jgpioexamples.testing;

import eu.softpol.lib.jgpio.Jgpio;
import java.time.Duration;

/// Blinks the LED connected to `GPIO14`.
public class Blink {

  private final Jgpio jgpio;
  private final Duration interval;

  public Blink(final Jgpio jgpio, final Duration interval) {
    this.jgpio = jgpio;
    this.interval = interval;
  }

  public void blink(final int times) throws InterruptedException {
    try (
        var chip = jgpio.openChipByLabel("pinctrl-rp1");
        var gpio14 = chip.getLine("GPIO14")
            .openAsOutput()
    ) {
      for (var i = 0; i < times; i++) {
        gpio14.write(true);
        Thread.sleep(interval);
        gpio14.write(false);
        Thread.sleep(interval);
      }
    }
  }

  public static void main(final String[] args) throws InterruptedException {
    new Blink(Jgpio.getInstance(), Duration.ofMillis(500))
        .blink(10);
  }

}
