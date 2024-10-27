package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpioit.util.TestChip;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class JgpioOpenChipIT {

  static List<TestChip> CHIPS = Defs.CHIPS;

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_open_chip_by_name(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByName(testChip.name());
    ) {
      assertThat(chip.name())
          .isEqualTo(testChip.name());
      assertThat(chip.label())
          .isEqualTo(testChip.label());
      assertThat(chip.countLines())
          .isEqualTo(testChip.countLines());
    }
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_open_chip_by_label(TestChip testChip) {
    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByLabel(testChip.label());
    ) {
      assertThat(chip.name())
          .isEqualTo(testChip.name());
      assertThat(chip.label())
          .isEqualTo(testChip.label());
      assertThat(chip.countLines())
          .isEqualTo(testChip.countLines());
    }
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_open_chip_by_number(TestChip testChip) {
    var number = Integer.parseInt(testChip.name()
        .replaceAll("[^0-9]", ""));

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByNumber(number);
    ) {
      assertThat(chip.name())
          .isEqualTo(testChip.name());
      assertThat(chip.label())
          .isEqualTo(testChip.label());
      assertThat(chip.countLines())
          .isEqualTo(testChip.countLines());
    }
  }

  @ParameterizedTest
  @FieldSource("CHIPS")
  void should_open_chip_by_path(TestChip testChip) {
    var path = Path.of("/dev/", testChip.name());

    var jgpio = Jgpio.getInstance();
    try (var chip = jgpio.openChipByPath(path);
    ) {
      assertThat(chip.name())
          .isEqualTo(testChip.name());
      assertThat(chip.label())
          .isEqualTo(testChip.label());
      assertThat(chip.countLines())
          .isEqualTo(testChip.countLines());
    }
  }

}
