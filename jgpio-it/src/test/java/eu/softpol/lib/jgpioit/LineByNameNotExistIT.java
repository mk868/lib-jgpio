package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestChip;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class LineByNameNotExistIT {


  static final List<TestPinWithInvalidName> NON_EXISTING_PINS = List.of(
      new TestPinWithInvalidName(Defs.CHIPS.getFirst(), "invalid"),
      new TestPinWithInvalidName(Defs.CHIPS.getFirst(), "abc"),
      new TestPinWithInvalidName(Defs.PIN_WITH_NAME.chip(),
          Defs.PIN_WITH_NAME.lineName().toLowerCase())
  );

  public record TestPinWithInvalidName(
      TestChip chip,
      String lineName
  ) {

    public String chipName() {
      return chip.name();
    }
  }

  @ParameterizedTest
  @FieldSource("NON_EXISTING_PINS")
  void should_findLine_return_empty(TestPinWithInvalidName pin) {
    final var chipName = pin.chipName();
    final var lineName = pin.lineName();

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(chipName);
    ) {
      var lineOpt = chip.findLine(lineName);

      assertThat(lineOpt)
          .isEmpty();
    }
  }

  @ParameterizedTest
  @FieldSource("NON_EXISTING_PINS")
  void should_getLine_throw(TestPinWithInvalidName pin) {
    final var chipName = pin.chipName();
    final var lineName = pin.lineName();

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(chipName);
    ) {
      assertThatThrownBy(() -> chip.getLine(lineName))
          .hasMessageContaining("name")
          .hasMessageContaining(lineName);
    }
  }

}
