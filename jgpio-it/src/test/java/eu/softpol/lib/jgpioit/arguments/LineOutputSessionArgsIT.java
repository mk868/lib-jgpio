package eu.softpol.lib.jgpioit.arguments;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.Defs;
import eu.softpol.lib.jgpioit.support.annotation.AnyLibgpiodIT;
import org.junit.jupiter.api.Test;

@AnyLibgpiodIT
public class LineOutputSessionArgsIT {

  @Test
  void should_setDriveMode_throwWhenDriveModeIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();
    var pin = Defs.PIN_WITH_NAME;
    try (var chip = jgpio.openChipByName(pin.chip().name());
        var session = chip.getLine(pin.lineOffset())
            .openAsOutput()
    ) {

      // WHEN-THEN
      assertThatThrownBy(() -> session.setDriveMode(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("driveMode");
    }
  }
}
