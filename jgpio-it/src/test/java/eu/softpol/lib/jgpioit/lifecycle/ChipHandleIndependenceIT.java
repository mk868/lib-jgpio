package eu.softpol.lib.jgpioit.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpioit.Defs;
import eu.softpol.lib.jgpioit.support.TwoPins;
import eu.softpol.lib.jgpioit.support.annotation.AnyLibgpiodIT;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

@AnyLibgpiodIT
class ChipHandleIndependenceIT {

  static final List<TwoPins> CONNECTED_PINS = List.of(Defs.CONNECTED_PINS);

  @ParameterizedTest
  @FieldSource("CONNECTED_PINS")
  void should_session_from_second_chip_remain_valid_after_first_chip_closed(TwoPins twoPins) {
    var outputPin = twoPins.pin1();
    var inputPin = twoPins.pin2();
    final var chipName = outputPin.chipName();

    var jgpio = Jgpio.getInstance();
    var chip1 = jgpio.openChipByName(chipName);
    try {
      try (var chip2 = jgpio.openChipByName(chipName);
          var outputSession = chip2.getLine(outputPin.lineOffset()).openAsOutput();
          var inputSession = chip2.getLine(inputPin.lineOffset()).openAsInput()) {

        chip1.close();

        outputSession.write(true);
        assertThat(inputSession.read())
            .as("Input line level after unrelated chip handle closed")
            .isTrue();

        outputSession.write(false);
        assertThat(inputSession.read())
            .as("Input line level after unrelated chip handle closed")
            .isFalse();
      }
    } finally {
      chip1.close(); // idempotent if already closed
    }
  }

  @ParameterizedTest
  @FieldSource("CONNECTED_PINS")
  void should_session_from_first_chip_remain_valid_after_second_chip_closed(TwoPins twoPins) {
    var outputPin = twoPins.pin1();
    var inputPin = twoPins.pin2();
    final var chipName = outputPin.chipName();

    var jgpio = Jgpio.getInstance();
    try (var chip1 = jgpio.openChipByName(chipName);
        var outputSession = chip1.getLine(outputPin.lineOffset()).openAsOutput();
        var inputSession = chip1.getLine(inputPin.lineOffset()).openAsInput()) {

      var chip2 = jgpio.openChipByName(chipName);
      chip2.close();

      outputSession.write(false);
      assertThat(inputSession.read())
          .as("Input line level after unrelated chip handle closed")
          .isFalse();

      outputSession.write(true);
      assertThat(inputSession.read())
          .as("Input line level after unrelated chip handle closed")
          .isTrue();
    }
  }

  @ParameterizedTest
  @FieldSource("CONNECTED_PINS")
  void should_line_busy_via_first_chip_be_unrequestable_as_input_via_second_chip(TwoPins twoPins) {
    final var pin = twoPins.pin1();
    final var chipName = pin.chipName();

    var jgpio = Jgpio.getInstance();
    try (var chip1 = jgpio.openChipByName(chipName);
        var _ = chip1.getLine(pin.lineOffset()).openAsInput()) {

      var chip2 = jgpio.openChipByName(chipName);
      try {
        var line2 = chip2.getLine(pin.lineOffset());
        assertThatThrownBy(line2::openAsInput)
            .isInstanceOf(JgpioException.class);
      } finally {
        chip2.close();
      }
    }
  }

  @ParameterizedTest
  @FieldSource("CONNECTED_PINS")
  void should_line_busy_as_input_be_unrequestable_as_output_via_second_chip(TwoPins twoPins) {
    final var pin = twoPins.pin1();
    final var chipName = pin.chipName();

    var jgpio = Jgpio.getInstance();
    try (var chip1 = jgpio.openChipByName(chipName);
        var _ = chip1.getLine(pin.lineOffset()).openAsInput()) {

      var chip2 = jgpio.openChipByName(chipName);
      try {
        var line2 = chip2.getLine(pin.lineOffset());
        assertThatThrownBy(line2::openAsOutput)
            .isInstanceOf(JgpioException.class);
      } finally {
        chip2.close();
      }
    }
  }

  @ParameterizedTest
  @FieldSource("CONNECTED_PINS")
  void should_line_busy_as_output_be_unrequestable_as_input_via_second_chip(TwoPins twoPins) {
    final var pin = twoPins.pin1();
    final var chipName = pin.chipName();

    var jgpio = Jgpio.getInstance();
    try (var chip1 = jgpio.openChipByName(chipName);
        var _ = chip1.getLine(pin.lineOffset()).openAsOutput()) {

      var chip2 = jgpio.openChipByName(chipName);
      try {
        var line2 = chip2.getLine(pin.lineOffset());
        assertThatThrownBy(line2::openAsInput)
            .isInstanceOf(JgpioException.class);
      } finally {
        chip2.close();
      }
    }
  }
}
