package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestPin;
import eu.softpol.lib.jgpioit.util.TwoPins;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class LineSessionBiasChangeIT {

  static final List<TestPin> PINS = List.of(
      Defs.UNCONNECTED_PIN
  );
  static final List<TwoPins> COUPLED_LINES = List.of(
      Defs.CONNECTED_PINS
  );

  @ParameterizedTest
  @FieldSource("PINS")
  void should_read_value_matching_bias_on_hanging_line(TestPin inputWithBiasPin) {
    var jgpio = Jgpio.getInstance();
    try (var inputChip1 = jgpio.openChipByName(inputWithBiasPin.chipName());
        var biasInputLine = inputChip1.getLine(inputWithBiasPin.lineOffset())
            .openAsInput();
    ) {
      biasInputLine.setBias(Bias.PULL_UP);
      assertThat(biasInputLine.read())
          .as("Input line level")
          .isTrue();
      biasInputLine.setBias(Bias.PULL_DOWN);
      assertThat(biasInputLine.read())
          .as("Input line level")
          .isFalse();
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_read_true_for_pull_up_bias_on_hanging_line(TestPin inputWithBiasPin) {
    var jgpio = Jgpio.getInstance();
    try (var inputChip1 = jgpio.openChipByName(inputWithBiasPin.chipName());
        var biasInputLine = inputChip1.getLine(inputWithBiasPin.lineOffset())
            .openAsInput(Bias.PULL_UP);
    ) {
      assertThat(biasInputLine.read())
          .as("Input line level")
          .isTrue();
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_read_false_for_pull_down_bias_on_hanging_line(TestPin inputWithBiasPin) {
    var jgpio = Jgpio.getInstance();
    try (var inputChip1 = jgpio.openChipByName(inputWithBiasPin.chipName());
        var biasInputLine = inputChip1.getLine(inputWithBiasPin.lineOffset())
            .openAsInput(Bias.PULL_DOWN);
    ) {
      assertThat(biasInputLine.read())
          .as("Input line level")
          .isFalse();
    }
  }

  @ParameterizedTest
  @FieldSource("COUPLED_LINES")
  void should_read_value_matching_bias_on_coupled_line(TwoPins coupledLines) {
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

  @ParameterizedTest
  @FieldSource("COUPLED_LINES")
  void should_read_true_for_pull_up_bias_on_coupled_line(TwoPins coupledLines) {
    var inputWithBiasPin = coupledLines.pin1();
    var inputNoBiasPin = coupledLines.pin2();

    var jgpio = Jgpio.getInstance();
    try (var inputChip1 = jgpio.openChipByName(inputWithBiasPin.chipName());
        var _ = inputChip1.getLine(inputWithBiasPin.lineOffset())
            .openAsInput(Bias.PULL_UP);
        var inputChip2 = jgpio.openChipByName(inputNoBiasPin.chipName());
        var highImpedanceLine = inputChip2.getLine(inputNoBiasPin.lineOffset())
            .openAsInput(Bias.HIGH_IMPEDANCE);
    ) {
      assertThat(highImpedanceLine.read())
          .as("Input line level")
          .isTrue();
    }
  }

  @ParameterizedTest
  @FieldSource("COUPLED_LINES")
  void should_read_true_for_pull_down_bias_on_coupled_line(TwoPins coupledLines) {
    var inputWithBiasPin = coupledLines.pin1();
    var inputNoBiasPin = coupledLines.pin2();

    var jgpio = Jgpio.getInstance();
    try (var inputChip1 = jgpio.openChipByName(inputWithBiasPin.chipName());
        var _ = inputChip1.getLine(inputWithBiasPin.lineOffset())
            .openAsInput(Bias.PULL_DOWN);
        var inputChip2 = jgpio.openChipByName(inputNoBiasPin.chipName());
        var highImpedanceLine = inputChip2.getLine(inputNoBiasPin.lineOffset())
            .openAsInput(Bias.HIGH_IMPEDANCE);
    ) {
      assertThat(highImpedanceLine.read())
          .as("Input line level")
          .isFalse();
    }
  }
}
