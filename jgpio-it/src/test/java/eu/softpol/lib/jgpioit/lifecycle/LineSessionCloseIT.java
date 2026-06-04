package eu.softpol.lib.jgpioit.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.Defs;
import eu.softpol.lib.jgpioit.annotation.AnyLibgpiodIT;
import eu.softpol.lib.jgpioit.util.TestPin;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

@AnyLibgpiodIT
class LineSessionCloseIT {

  static final List<TestPin> PINS = List.of(Defs.UNCONNECTED_PIN);

  @ParameterizedTest
  @FieldSource("PINS")
  void should_input_isClosed_return_false_before_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
        var session = chip.getLine(pin.lineOffset())
            .openAsInput()
    ) {
      assertThat(session.isClosed())
          .as("Input session closed before close()")
          .isFalse();
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_input_isClosed_return_true_after_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var session = chip.getLine(pin.lineOffset()).openAsInput();
      session.close();

      assertThat(session.isClosed())
          .as("Input session closed after close()")
          .isTrue();
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_close_input_session_be_idempotent(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var session = chip.getLine(pin.lineOffset()).openAsInput();
      session.close();

      assertThatCode(session::close)
          .as("Second input session close() should not throw")
          .doesNotThrowAnyException();
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_read_throw_after_input_session_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var session = chip.getLine(pin.lineOffset()).openAsInput();
      session.close();

      assertThatThrownBy(session::read)
          .isInstanceOf(IllegalStateException.class);
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_setBias_throw_after_input_session_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var session = chip.getLine(pin.lineOffset()).openAsInput();
      session.close();

      assertThatThrownBy(() -> session.setBias(Bias.PULL_UP))
          .isInstanceOf(IllegalStateException.class);
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_output_isClosed_return_false_before_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
        var session = chip.getLine(pin.lineOffset()).openAsOutput()) {
      assertThat(session.isClosed())
          .as("Output session closed before close()")
          .isFalse();
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_output_isClosed_return_true_after_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var session = chip.getLine(pin.lineOffset()).openAsOutput();
      session.close();

      assertThat(session.isClosed())
          .as("Output session closed after close()")
          .isTrue();
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_close_output_session_be_idempotent(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var session = chip.getLine(pin.lineOffset()).openAsOutput();
      session.close();

      assertThatCode(session::close)
          .as("Second output session close() should not throw")
          .doesNotThrowAnyException();
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_write_throw_after_output_session_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var session = chip.getLine(pin.lineOffset()).openAsOutput();
      session.close();

      assertThatThrownBy(() -> session.write(false))
          .isInstanceOf(IllegalStateException.class);
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_setDriveMode_throw_after_output_session_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var session = chip.getLine(pin.lineOffset()).openAsOutput();
      session.close();

      assertThatThrownBy(() -> session.setDriveMode(DriveMode.PUSH_PULL))
          .isInstanceOf(IllegalStateException.class);
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_line_be_reopenable_as_input_after_session_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var line = chip.getLine(pin.lineOffset());

      try (var _ = line.openAsInput()) {
        // NOP
      }

      try (var session = line.openAsInput()) {
        assertThat(session.isClosed())
            .as("Reopened input session should not be closed")
            .isFalse();
      }
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_line_be_reopenable_as_output_after_session_close(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName())) {
      var line = chip.getLine(pin.lineOffset());

      try (var _ = line.openAsOutput()) {
        // NOP
      }

      try (var session = line.openAsOutput()) {
        assertThat(session.isClosed())
            .as("Reopened output session should not be closed")
            .isFalse();
      }
    }
  }
}
