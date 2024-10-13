package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestPin;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class LineByOffsetIT {

  static List<TestPin> PINS = List.of(
      Defs.PIN_WITH_NAME,
      Defs.PIN_WITHOUT_NAME,
      new TestPin(Defs.CHIPS.getFirst(), Defs.CHIPS.getFirst().countLines() - 1, null)
  );

  @ParameterizedTest
  @FieldSource("PINS")
  void should_getLine_return_line(TestPin pin) {
    final var chipName = pin.chipName();
    final var lineOffset = pin.lineOffset();
    final var lineName = pin.lineName();

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(chipName);
    ) {
      var line = chip.getLine(lineOffset);

      assertThat(line.offset())
          .isEqualTo(lineOffset);
      assertThat(line.name())
          .isEqualTo(lineName);
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_findLine_return_line(TestPin pin) {
    final var chipName = pin.chipName();
    final var lineOffset = pin.lineOffset();
    final var lineName = pin.lineName();

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(chipName);
    ) {
      var lineOpt = chip.findLine(lineOffset);
      assertThat(lineOpt)
          .isPresent();
      var line = lineOpt.get();

      assertThat(line.offset())
          .isEqualTo(lineOffset);
      assertThat(line.name())
          .isEqualTo(lineName);
    }
  }

}
