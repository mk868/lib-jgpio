package eu.softpol.lib.jgpioit;

import static eu.softpol.lib.jgpio.DriveMode.OPEN_DRAIN;
import static eu.softpol.lib.jgpio.DriveMode.OPEN_DRAIN_PULL_UP;
import static eu.softpol.lib.jgpio.DriveMode.OPEN_SOURCE;
import static eu.softpol.lib.jgpio.DriveMode.OPEN_SOURCE_PULL_DOWN;
import static eu.softpol.lib.jgpio.DriveMode.PUSH_PULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.ParameterizedTest.INDEX_PLACEHOLDER;
import static org.junit.jupiter.params.provider.Arguments.of;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestPin;
import eu.softpol.lib.jgpioit.util.TwoPins;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LineSessionWriteDriveIT {

  private static final List<TwoPins> CONNECTED_PINS = List.of(
      Defs.CONNECTED_PINS
  );

  private static Stream<Arguments> args() {
    return Stream.of(
        // PUSH_PULL
        of(PUSH_PULL, Bias.HIGH_IMPEDANCE, false, false),
        of(PUSH_PULL, Bias.HIGH_IMPEDANCE, true, true),
        of(PUSH_PULL, Bias.PULL_UP, false, false),
        of(PUSH_PULL, Bias.PULL_UP, true, true),
        of(PUSH_PULL, Bias.PULL_DOWN, false, false),
        of(PUSH_PULL, Bias.PULL_DOWN, true, true),
        // OPEN_DRAIN
        of(OPEN_DRAIN, Bias.HIGH_IMPEDANCE, false, false),
        //of(OPEN_DRAIN, Bias.HIGH_IMPEDANCE, true, undefined),
        of(OPEN_DRAIN, Bias.PULL_UP, false, false),
        of(OPEN_DRAIN, Bias.PULL_UP, true, true),
        of(OPEN_DRAIN, Bias.PULL_DOWN, false, false),
        of(OPEN_DRAIN, Bias.PULL_DOWN, true, false),
        // OPEN_DRAIN_PULL_UP
        of(OPEN_DRAIN_PULL_UP, Bias.HIGH_IMPEDANCE, false, false),
        of(OPEN_DRAIN_PULL_UP, Bias.HIGH_IMPEDANCE, true, true),
        of(OPEN_DRAIN_PULL_UP, Bias.PULL_UP, false, false),
        of(OPEN_DRAIN_PULL_UP, Bias.PULL_UP, true, true),
        of(OPEN_DRAIN_PULL_UP, Bias.PULL_DOWN, false, false),
        //of(OPEN_DRAIN_PULL_UP, Bias.PULL_DOWN, true, undefined),
        // OPEN_SOURCE
        //of(OPEN_SOURCE, Bias.HIGH_IMPEDANCE, false, undefined),
        of(OPEN_SOURCE, Bias.HIGH_IMPEDANCE, true, true),
        of(OPEN_SOURCE, Bias.PULL_UP, false, true),
        of(OPEN_SOURCE, Bias.PULL_UP, true, true),
        of(OPEN_SOURCE, Bias.PULL_DOWN, false, false),
        of(OPEN_SOURCE, Bias.PULL_DOWN, true, true),
        // OPEN_SOURCE_PULL_DOWN
        of(OPEN_SOURCE_PULL_DOWN, Bias.HIGH_IMPEDANCE, false, false),
        of(OPEN_SOURCE_PULL_DOWN, Bias.HIGH_IMPEDANCE, true, true),
        //of(OPEN_SOURCE_PULL_DOWN, Bias.PULL_UP, false, undefined),
        of(OPEN_SOURCE_PULL_DOWN, Bias.PULL_UP, true, true),
        of(OPEN_SOURCE_PULL_DOWN, Bias.PULL_DOWN, false, false),
        of(OPEN_SOURCE_PULL_DOWN, Bias.PULL_DOWN, true, true)
    ).flatMap(it ->
        CONNECTED_PINS.stream()
            .map(pins -> of(Stream.concat(
                Stream.of(
                    pins.pin1(),
                    pins.pin2()
                ),
                Arrays.stream(it.get())
            ).toArray()))
    );
  }

  @ParameterizedTest(name = "[" + INDEX_PLACEHOLDER + "] "
      + "driveMode={2}, "
      + "bias={3}, "
      + "writeValue={4}, "
      + "readValue={5} ")
  @MethodSource("args")
  void should_read_value_depending_on_bias_and_drive_mode_on_coupled_line(
      TestPin inputPin,
      TestPin outputPin,
      DriveMode driveMode,
      Bias bias,
      boolean writeValue,
      boolean readValue
  ) {
    var jgpio = Jgpio.getInstance();
    try (var outputChip = jgpio.openChipByName(outputPin.chipName());
        var outputLine = outputChip.getLine(outputPin.lineOffset())
            .openAsOutput(driveMode);
        var inputChip = jgpio.openChipByName(inputPin.chipName());
        var inputLine = inputChip.getLine(inputPin.lineOffset())
            .openAsInput(bias);
    ) {
      outputLine.write(writeValue);
      assertThat(inputLine.read())
          .as("Input line level")
          .isEqualTo(readValue);
    }
  }

  @ParameterizedTest(name = "[" + INDEX_PLACEHOLDER + "] "
      + "driveMode={2}, "
      + "bias={3}, "
      + "writeValue={4}, "
      + "readValue={5} ")
  @MethodSource("args")
  void should_read_value_depending_on_bias_and_drive_mode_on_coupled_line2(
      TestPin inputPin,
      TestPin outputPin,
      DriveMode driveMode,
      Bias bias,
      boolean writeValue,
      boolean readValue
  ) {
    var jgpio = Jgpio.getInstance();
    try (var outputChip = jgpio.openChipByName(outputPin.chipName());
        var outputLine = outputChip.getLine(outputPin.lineOffset())
            .openAsOutput();
        var inputChip = jgpio.openChipByName(inputPin.chipName());
        var inputLine = inputChip.getLine(inputPin.lineOffset())
            .openAsInput(bias);
    ) {
      outputLine.setDriveMode(driveMode);
      outputLine.write(writeValue);
      assertThat(inputLine.read())
          .as("Input line level")
          .isEqualTo(readValue);
    }
  }
}
