package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.annotation.AnyLibgpiodIT;
import org.junit.jupiter.api.Test;

@AnyLibgpiodIT
public class ChipArgsIT {

  @Test
  void should_findLine_throwWhenOffsetNegative() {
    // GIVEN
    var invalidOffset = -1;
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(Defs.PIN_WITH_NAME.chip().name())) {

      // WHEN-THEN
      assertThatThrownBy(() -> chip.findLine(invalidOffset))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("offset");
    }
  }

  @Test
  void should_getLine_throwWhenOffsetNegative() {
    // GIVEN
    var invalidOffset = -1;
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(Defs.PIN_WITH_NAME.chip().name())) {

      // WHEN-THEN
      assertThatThrownBy(() -> chip.getLine(invalidOffset))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("offset");
    }
  }

  @Test
  void should_findLine_throwWhenNameIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(Defs.PIN_WITH_NAME.chip().name())) {

      // WHEN-THEN
      assertThatThrownBy(() -> chip.findLine(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("name");
    }
  }

  @Test
  void should_getLine_throwWhenNameIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(Defs.PIN_WITH_NAME.chip().name())) {

      // WHEN-THEN
      assertThatThrownBy(() -> chip.getLine(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("name");
    }
  }
}
