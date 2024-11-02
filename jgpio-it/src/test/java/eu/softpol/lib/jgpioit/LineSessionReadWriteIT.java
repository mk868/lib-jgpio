package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TwoPins;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class LineSessionReadWriteIT {

  static final List<TwoPins> CONNECTED_PINS = List.of(
      Defs.CONNECTED_PINS
  );

  @ParameterizedTest
  @FieldSource("CONNECTED_PINS")
  void should_read_same_value_as_wrote(TwoPins twoPins) {
    var outputPin = twoPins.pin1();
    var inputPin = twoPins.pin2();

    var jgpio = Jgpio.getInstance();
    try (var outputChip = jgpio.openChipByName(outputPin.chipName());
        var outputLine = outputChip.getLine(outputPin.lineOffset())
            .openAsOutput();
        var inputChip = jgpio.openChipByName(inputPin.chipName());
        var inputLine = inputChip.getLine(inputPin.lineOffset())
            .openAsInput();
    ) {
      outputLine.write(false);
      assertThat(inputLine.read())
          .as("Input line level")
          .isFalse();

      outputLine.write(true);
      assertThat(inputLine.read())
          .as("Input line level")
          .isTrue();
    }
  }
}
