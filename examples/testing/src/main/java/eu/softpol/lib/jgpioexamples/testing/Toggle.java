package eu.softpol.lib.jgpioexamples.testing;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.LineInputSession;
import eu.softpol.lib.jgpio.LineOutputSession;

/// Lights the LED connected to `GPIO14` while the button connected to `GPIO18` is pressed.
public class Toggle implements AutoCloseable {

  private final Chip chip;
  private final LineOutputSession led;
  private final LineInputSession button;

  public Toggle(final Jgpio jgpio) {
    chip = jgpio.openChipByLabel("pinctrl-rp1");
    try {
      led = chip.getLine("GPIO14")
          .openAsOutput();
      button = chip.getLine("GPIO18")
          .openAsInput(Bias.PULL_UP);
    } catch (RuntimeException e) {
      chip.close();
      throw e;
    }
  }

  public void update() {
    led.write(!button.read());
  }

  @Override
  public void close() {
    chip.close();
  }

  public static void main(final String[] args) throws InterruptedException {
    try (var toggle = new Toggle(Jgpio.getInstance())) {
      while (true) {
        toggle.update();
        Thread.sleep(100);
      }
    }
  }

}
