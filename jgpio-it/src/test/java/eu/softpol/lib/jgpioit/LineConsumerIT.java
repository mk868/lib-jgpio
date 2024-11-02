package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestPin;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class LineConsumerIT {

  static final List<TestPin> PINS = List.of(
      Defs.UNCONNECTED_PIN
  );

  @ParameterizedTest
  @FieldSource("PINS")
  void should_be_unused_by_default(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
    ) {
      var line = chip.getLine(pin.lineOffset());

      assertSoftly(softly -> {
        softly.assertThat(line.isUsed())
            .as("Line used")
            .isFalse();
        softly.assertThat(line.consumer())
            .as("Line consumer")
            .isNull();
      });
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_be_used_when_requested_for_write(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
    ) {
      var line = chip.getLine(pin.lineOffset());

      try (var _ = line.openAsOutput()) {
        assertSoftly(softly -> {
          softly.assertThat(line.isUsed())
              .as("Line used")
              .isTrue();
          softly.assertThat(line.consumer())
              .as("Line consumer")
              .isNotNull();
        });
      }
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_be_used_when_requested_for_read(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
    ) {
      var line = chip.getLine(pin.lineOffset());

      try (var _ = line.openAsInput()) {
        assertSoftly(softly -> {
          softly.assertThat(line.isUsed())
              .as("Line used")
              .isTrue();
          softly.assertThat(line.consumer())
              .as("Line consumer")
              .isNotNull();
        });
      }
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_not_be_used_after_read_release(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
    ) {
      var line = chip.getLine(pin.lineOffset());

      try (var _ = line.openAsInput()) {
        // NOP
      }

      assertSoftly(softly -> {
        softly.assertThat(line.isUsed())
            .as("Line used")
            .isFalse();
        softly.assertThat(line.consumer())
            .as("Line consumer")
            .isNull();
      });
    }
  }

  @ParameterizedTest
  @FieldSource("PINS")
  void should_not_be_used_after_write_release(TestPin pin) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(pin.chipName());
    ) {
      var line = chip.getLine(pin.lineOffset());

      try (var _ = line.openAsOutput()) {
        // NOP
      }

      assertSoftly(softly -> {
        softly.assertThat(line.isUsed())
            .as("Line used")
            .isFalse();
        softly.assertThat(line.consumer())
            .as("Line consumer")
            .isNull();
      });
    }
  }
}
