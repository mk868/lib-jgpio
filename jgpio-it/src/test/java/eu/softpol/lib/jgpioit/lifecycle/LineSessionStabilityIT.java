package eu.softpol.lib.jgpioit.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.Defs;
import eu.softpol.lib.jgpioit.support.TwoPins;
import eu.softpol.lib.jgpioit.support.annotation.AnyLibgpiodIT;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

@AnyLibgpiodIT
class LineSessionStabilityIT {

  static final List<TwoPins> CONNECTED_PINS = List.of(Defs.CONNECTED_PINS);

  private static final int ITERATIONS = 50;

  @ParameterizedTest
  @FieldSource("CONNECTED_PINS")
  void should_repeated_open_write_read_close_not_leak(TwoPins twoPins) {
    var outputPin = twoPins.pin1();
    var inputPin = twoPins.pin2();

    var jgpio = Jgpio.getInstance();
    for (int i = 0; i < ITERATIONS; i++) {
      try (var outputChip = jgpio.openChipByName(outputPin.chipName());
          var outputSession = outputChip.getLine(outputPin.lineOffset()).openAsOutput();
          var inputChip = jgpio.openChipByName(inputPin.chipName());
          var inputSession = inputChip.getLine(inputPin.lineOffset()).openAsInput()) {

        boolean value = (i % 2 == 0);
        outputSession.write(value);
        assertThat(inputSession.read())
            .as("Input line level at iteration %d", i)
            .isEqualTo(value);
      }
    }
  }
}
