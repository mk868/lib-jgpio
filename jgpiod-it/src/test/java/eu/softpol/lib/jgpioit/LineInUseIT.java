package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpioit.util.TestPin;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class LineInUseIT {

  static List<TestPin> PINS = List.of(Defs.UNCONNECTED_PIN);

  @ParameterizedTest
  @FieldSource("PINS")
  void should_throw_when_requesting_busy_line(TestPin pin) {
    final var chipName = pin.chipName();
    final var lineOffset = pin.lineOffset();

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(chipName);
        var _ = chip.getLine(lineOffset)
            .openAsInput();
    ) {
      var line = chip.getLine(lineOffset);
      assertThatThrownBy(line::openAsInput)
          .isInstanceOf(JgpioException.class);
    }
  }
}
