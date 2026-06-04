package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.annotation.AnyLibgpiodIT;
import org.junit.jupiter.api.Test;

@AnyLibgpiodIT
public class JgpioArgsIT {

  @Test
  void should_openChipByName_throwWhenNameIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();

    // WHEN-THEN
    assertThatThrownBy(() -> jgpio.openChipByName(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("name");
  }

  @Test
  void should_openChipByLabel_throwWhenLabelIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();

    // WHEN-THEN
    assertThatThrownBy(() -> jgpio.openChipByLabel(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("label");
  }

  @Test
  void should_openChipByPath_throwWhenPathIsNull() {
    // GIVEN
    var jgpio = Jgpio.getInstance();

    // WHEN-THEN
    assertThatThrownBy(() -> jgpio.openChipByPath(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("path");
  }
}
