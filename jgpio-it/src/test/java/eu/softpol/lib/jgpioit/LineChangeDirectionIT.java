package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestPin;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class LineChangeDirectionIT {

  static final List<TestPin> PINS = List.of(
      Defs.UNCONNECTED_PIN
  );

  @ParameterizedTest
  @FieldSource("PINS")
  void should_change_line_direction(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
    ) {
      var line = chip.getLine(pin.lineOffset());

      try (var _ = line.openAsInput()) {
        assertThat(line.direction())
            .isEqualTo(Direction.INPUT);
      }

      assertThat(line.direction())
          .isEqualTo(Direction.INPUT);

      try (var _ = line.openAsOutput()) {
        assertThat(line.direction())
            .isEqualTo(Direction.OUTPUT);
      }

      assertThat(line.direction())
          .isEqualTo(Direction.OUTPUT);
    }
  }
}
