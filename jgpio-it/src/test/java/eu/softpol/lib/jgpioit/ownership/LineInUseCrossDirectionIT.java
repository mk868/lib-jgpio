package eu.softpol.lib.jgpioit.ownership;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpioit.Defs;
import eu.softpol.lib.jgpioit.support.TestPin;
import eu.softpol.lib.jgpioit.support.annotation.AnyLibgpiodIT;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

@AnyLibgpiodIT
class LineInUseCrossDirectionIT {

  static final List<TestPin> PINS = List.of(Defs.UNCONNECTED_PIN);

  @ParameterizedTest
  @FieldSource("PINS")
  void should_throw_when_opening_as_output_while_input_active(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
        var _ = chip.getLine(pin.lineOffset()).openAsInput()) {

      var line = chip.getLine(pin.lineOffset());
      assertThatThrownBy(line::openAsOutput)
          .isInstanceOf(JgpioException.class);
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_throw_when_opening_as_input_while_output_active(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
        var _ = chip.getLine(pin.lineOffset()).openAsOutput()) {

      var line = chip.getLine(pin.lineOffset());
      assertThatThrownBy(line::openAsInput)
          .isInstanceOf(JgpioException.class);
    }
  }
}
