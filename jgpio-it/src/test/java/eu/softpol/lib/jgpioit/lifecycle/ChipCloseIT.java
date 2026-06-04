package eu.softpol.lib.jgpioit.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.Defs;
import eu.softpol.lib.jgpioit.annotation.AnyLibgpiodIT;
import eu.softpol.lib.jgpioit.util.TestChip;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

@AnyLibgpiodIT
class ChipCloseIT {

  static final List<TestChip> CHIPS = Defs.CHIPS;

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_isClosed_return_false_before_close(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(testChip.name())) {
      assertThat(chip.isClosed())
          .as("Chip closed before close()")
          .isFalse();
    }
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_isClosed_return_true_after_close(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chip = jgpio.openChipByName(testChip.name());
    chip.close();

    assertThat(chip.isClosed())
        .as("Chip closed after close()")
        .isTrue();
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_close_be_idempotent(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chip = jgpio.openChipByName(testChip.name());
    chip.close();

    assertThatCode(chip::close)
        .as("Second close() should not throw")
        .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_name_throw_after_close(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chip = jgpio.openChipByName(testChip.name());
    chip.close();

    assertThatThrownBy(chip::name)
        .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_label_throw_after_close(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chip = jgpio.openChipByName(testChip.name());
    chip.close();

    assertThatThrownBy(chip::label)
        .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_countLines_throw_after_close(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chip = jgpio.openChipByName(testChip.name());
    chip.close();

    assertThatThrownBy(chip::countLines)
        .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_findLine_by_offset_throw_after_close(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chip = jgpio.openChipByName(testChip.name());
    chip.close();

    assertThatThrownBy(() -> chip.findLine(0))
        .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_getLine_by_offset_throw_after_close(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chip = jgpio.openChipByName(testChip.name());
    chip.close();

    assertThatThrownBy(() -> chip.getLine(0))
        .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_findLine_by_name_throw_after_close(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chip = jgpio.openChipByName(testChip.name());
    chip.close();

    assertThatThrownBy(() -> chip.findLine(Defs.PIN_WITH_NAME.lineName()))
        .isInstanceOf(IllegalStateException.class);
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_getLine_by_name_throw_after_close(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chip = jgpio.openChipByName(testChip.name());
    chip.close();

    assertThatThrownBy(() -> chip.getLine(Defs.PIN_WITH_NAME.lineName()))
        .isInstanceOf(IllegalStateException.class);
  }
}
