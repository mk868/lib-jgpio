package eu.softpol.lib.jgpioit.arguments;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.Defs;
import eu.softpol.lib.jgpioit.support.annotation.AnyLibgpiodIT;
import org.junit.jupiter.api.Test;

@AnyLibgpiodIT
public class LineInputSessionArgsIT {

  @Test
  void should_setBias_throwWhenBiasIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();
    var pin = Defs.PIN_WITH_NAME;
    try (var chip = jgpio.openChipByName(pin.chip().name());
        var session = chip.getLine(pin.lineOffset())
            .openAsInput()
    ) {

      // WHEN-THEN
      assertThatThrownBy(() -> session.setBias(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("bias");
    }
  }
}
