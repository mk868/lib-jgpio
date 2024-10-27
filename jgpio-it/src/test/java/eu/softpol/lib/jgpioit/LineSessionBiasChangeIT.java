package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TwoPins;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class LineSessionBiasChangeIT {

  static List<TwoPins> COUPLED_LINES = List.of(
      Defs.CONNECTED_PINS
  );

  @ParameterizedTest
  @FieldSource("COUPLED_LINES")
  void high_impedance_line_should_detect_bias_change_on_coupled_line(TwoPins coupledLines) {
    var inputWithBiasPin = coupledLines.pin1();
    var inputNoBiasPin = coupledLines.pin2();

    var jgpio = Jgpio.getInstance();
    try (var inputChip1 = jgpio.openChipByName(inputWithBiasPin.chipName());
        var biasInputLine = inputChip1.getLine(inputWithBiasPin.lineOffset())
            .openAsInput();
        var inputChip2 = jgpio.openChipByName(inputNoBiasPin.chipName());
        var highImpedanceLine = inputChip2.getLine(inputNoBiasPin.lineOffset())
            .openAsInput(Bias.HIGH_IMPEDANCE);
    ) {
      biasInputLine.setBias(Bias.PULL_UP);
      assertThat(highImpedanceLine.read())
          .as("Input line level")
          .isTrue();
      biasInputLine.setBias(Bias.PULL_DOWN);
      assertThat(highImpedanceLine.read())
          .as("Input line level")
          .isFalse();
    }
  }
}
