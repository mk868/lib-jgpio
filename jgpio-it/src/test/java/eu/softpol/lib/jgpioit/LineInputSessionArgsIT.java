package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.annotation.AnyLibgpiodIT;
import org.junit.jupiter.api.Test;

@AnyLibgpiodIT
public class LineInputSessionArgsIT {

  @Test
  void should_setBias_throwWhenBiasIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();
    var pin = Defs.PIN_WITH_NAME;
    try (var chip = jgpio.openChipByName(pin.chip().name())) {
      var line = chip.getLine(pin.lineOffset());
      try (var session = line.openAsInput()) {

        // WHEN-THEN
        assertThatThrownBy(() -> session.setBias(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("bias");
      }
    }
  }
}
