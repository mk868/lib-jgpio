package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.InputMode;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.OutputMode;
import eu.softpol.lib.jgpioit.annotation.AnyLibgpiodIT;
import org.junit.jupiter.api.Test;

@AnyLibgpiodIT
public class LineArgsIT {

  @Test
  void should_openAsInput_throwWhenBiasIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();
    var pin = Defs.PIN_WITH_NAME;
    try (var chip = jgpio.openChipByName(pin.chip().name())) {
      var line = chip.getLine(pin.lineOffset());

      // WHEN-THEN
      assertThatThrownBy(() -> line.openAsInput((Bias) null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("bias");
    }
  }

  @Test
  void should_openAsInput_throwWhenInputModeIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();
    var pin = Defs.PIN_WITH_NAME;
    try (var chip = jgpio.openChipByName(pin.chip().name())) {
      var line = chip.getLine(pin.lineOffset());

      // WHEN-THEN
      assertThatThrownBy(() -> line.openAsInput((InputMode) null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("inputMode");
    }
  }

  @Test
  void should_openAsOutput_throwWhenDriveModeIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();
    var pin = Defs.PIN_WITH_NAME;
    try (var chip = jgpio.openChipByName(pin.chip().name())) {
      var line = chip.getLine(pin.lineOffset());

      // WHEN-THEN
      assertThatThrownBy(() -> line.openAsOutput((DriveMode) null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("driveMode");
    }
  }

  @Test
  void should_openAsOutput_throwWhenOutputModeIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();
    var pin = Defs.PIN_WITH_NAME;
    try (var chip = jgpio.openChipByName(pin.chip().name())) {
      var line = chip.getLine(pin.lineOffset());

      // WHEN-THEN
      assertThatThrownBy(() -> line.openAsOutput((OutputMode) null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("outputMode");
    }
  }
}
