package eu.softpol.lib.jgpioit.ownership;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.InputMode;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.OutputMode;
import eu.softpol.lib.jgpioit.Defs;
import eu.softpol.lib.jgpioit.support.TestPin;
import eu.softpol.lib.jgpioit.support.annotation.AnyLibgpiodIT;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

@AnyLibgpiodIT
class LineSessionCustomConsumerIT {

  static final List<TestPin> PINS = List.of(Defs.UNCONNECTED_PIN);

  @ParameterizedTest
  @FieldSource("PINS")
  void should_consumer_return_custom_name_when_opening_input(TestPin pin) {
    var inputMode = InputMode.builder()
        .consumer("my-consumer")
        .build();

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var line = chip.getLine(pin.lineOffset());

      try (var _ = line.openAsInput(inputMode)) {
        assertThat(line.consumer())
            .as("Line consumer while input session active")
            .isEqualTo("my-consumer");
      }
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_consumer_return_custom_name_when_opening_output(TestPin pin) {
    var outputMode = OutputMode.builder()
        .consumer("my-consumer")
        .build();

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var line = chip.getLine(pin.lineOffset());

      try (var _ = line.openAsOutput(outputMode)) {
        assertThat(line.consumer())
            .as("Line consumer while output session active")
            .isEqualTo("my-consumer");
      }
    }
  }

}
