package eu.softpol.lib.jgpioit.discovery;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpioit.support.annotation.AnyLibgpiodIT;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

@AnyLibgpiodIT
class JgpioOpenChipNotExistIT {

  @Test
  void should_openChipByName_throw_for_nonexistent_chip() {
    var jgpio = Jgpio.getInstance();

    assertThatThrownBy(() -> jgpio.openChipByName("gpiochip9999"))
        .isInstanceOf(JgpioException.class);
  }

  @Test
  void should_openChipByPath_throw_for_nonexistent_chip() {
    var jgpio = Jgpio.getInstance();

    assertThatThrownBy(() -> jgpio.openChipByPath(Path.of("/dev/gpiochip9999")))
        .isInstanceOf(JgpioException.class);
  }

  @Test
  void should_openChipByLabel_throw_for_nonexistent_chip() {
    var jgpio = Jgpio.getInstance();

    assertThatThrownBy(() -> jgpio.openChipByLabel("invalid-label"))
        .isInstanceOf(JgpioException.class);
  }

  @Test
  void should_openChipByNumber_throw_for_nonexistent_chip() {
    var jgpio = Jgpio.getInstance();

    assertThatThrownBy(() -> jgpio.openChipByNumber(9999))
        .isInstanceOf(JgpioException.class);
  }
}
