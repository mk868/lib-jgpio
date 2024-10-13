package eu.softpol.lib.jgpioexamples;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Jgpio;

public class Toggle {

  public static void main(final String[] args) throws InterruptedException {
    try (
        var chip = Jgpio.getInstance()
            .openChipByLabel("pinctrl-rp1"); // list available chips using `gpiodetect`
        var gpio14 = chip.getLine("GPIO14") // list available lines using `gpioinfo`
            .openAsOutput();
        var gpio18 = chip.getLine("GPIO18")
            .openAsInput(Bias.PULL_UP);
    ) {
      while (true) {
        gpio14.write(!gpio18.read());
        Thread.sleep(100);
      }
    }
  }

}
