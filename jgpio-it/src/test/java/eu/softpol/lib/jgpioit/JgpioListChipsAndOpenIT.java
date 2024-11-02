package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestChip;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class JgpioListChipsAndOpenIT {

  static final List<TestChip> CHIPS = Defs.CHIPS;

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_open_from_list(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    var chips = jgpio.getChips();

    var chipInfo = chips.stream()
        .filter(a -> a.name().equals(testChip.name()))
        .findFirst()
        .orElseThrow();

    try (var chip = chipInfo.open()) {
      assertThat(chip.name())
          .isEqualTo(testChip.name());
      assertThat(chip.label())
          .isEqualTo(testChip.label());
      assertThat(chip.countLines())
          .isEqualTo(testChip.countLines());
    }
  }

}
