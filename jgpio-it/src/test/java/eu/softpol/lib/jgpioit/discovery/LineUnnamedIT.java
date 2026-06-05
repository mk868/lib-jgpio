package eu.softpol.lib.jgpioit.discovery;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.Defs;
import eu.softpol.lib.jgpioit.support.TestPin;
import eu.softpol.lib.jgpioit.support.annotation.AnyLibgpiodIT;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

@AnyLibgpiodIT
class LineUnnamedIT {

  static final List<TestPin> PINS = List.of(Defs.PIN_WITHOUT_NAME);

  @ParameterizedTest
  @FieldSource("PINS")
  void should_unnamed_line_name_return_null(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var line = chip.getLine(pin.lineOffset());

      assertThat(line.name())
          .as("Unnamed line name")
          .isNull();
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_find_unnamed_line_by_offset(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var lineOpt = chip.findLine(pin.lineOffset());

      assertThat(lineOpt)
          .as("Optional for unnamed line by offset")
          .isPresent();
      assertThat(lineOpt.get().name())
          .as("Unnamed line name")
          .isNull();
    }
  }
}
