package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestChip;
import org.junit.jupiter.api.Test;

class JgpioListChipsIT {

  @Test
  void should_return_actual_name_label_and_countLines() {
    var jgpio = Jgpio.getInstance();
    var chips = jgpio.getChips();

    assertThat(chips)
        .map(c -> new TestChip(c.name(), c.label(), c.countLines()))
        .isEqualTo(Defs.CHIPS);
  }

}
