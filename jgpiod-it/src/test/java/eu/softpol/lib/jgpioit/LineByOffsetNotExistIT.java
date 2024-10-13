package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestChip;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class LineByOffsetNotExistIT {

  static List<TestPinWithInvalidOffset> NON_EXISTING_PINS = List.of(
      new TestPinWithInvalidOffset(Defs.CHIPS.getFirst(), Defs.CHIPS.getFirst().countLines()),
      new TestPinWithInvalidOffset(Defs.CHIPS.getFirst(), Defs.CHIPS.getFirst().countLines() + 10)
  );

  public record TestPinWithInvalidOffset(
      TestChip chip,
      int lineOffset
  ) {

    public String chipName() {
      return chip.name();
    }
  }

  @ParameterizedTest
  @FieldSource("NON_EXISTING_PINS")
  void should_findLine_return_empty(TestPinWithInvalidOffset pin) {
    final var chipName = pin.chipName();
    final var lineOffset = pin.lineOffset();

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(chipName);
    ) {
      var lineOpt = chip.findLine(lineOffset);

      assertThat(lineOpt)
          .isEmpty();
    }
  }

  @ParameterizedTest
  @FieldSource("NON_EXISTING_PINS")
  void should_getLine_throw(TestPinWithInvalidOffset pin) {
    final var chipName = pin.chipName();
    final var lineOffset = pin.lineOffset();

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(chipName);
    ) {
      assertThatThrownBy(() -> chip.getLine(lineOffset))
          .hasMessageContaining("offset")
          .hasMessageContaining("" + lineOffset);
    }
  }

}
