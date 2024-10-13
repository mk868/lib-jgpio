package eu.softpol.lib.jgpioexamples;

import eu.softpol.lib.jgpio.Jgpio;

public class Blink {

  public static void main(final String[] args) throws InterruptedException {
    try (
        var chip = Jgpio.getInstance()
            .openChipByLabel("pinctrl-rp1"); // list available chips using `gpiodetect`
        var gpio14 = chip.getLine("GPIO14") // list available lines using `gpioinfo`
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
