/*
 * Copyright 2024-2026 SOFT-POL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.softpol.lib.jgpio.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.ChipInfo;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.JgpioException;
import java.nio.file.Path;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MockJgpioTest {

  static Stream<Named<UnaryOperator<JgpioMock.Builder>>> chipDefinitions() {
    return Stream.of(
        Named.of("by name", b -> b.chip("gpiochip10", c -> {
        })),
        Named.of("by number", b -> b.chip(10, c -> {
        })),
        Named.of("by path", b -> b.chip(Path.of("/dev/gpiochip10"), c -> {
        }))
    );
  }

  @ParameterizedTest
  @MethodSource("chipDefinitions")
  void chip_accessibleByNameNumberAndPath(UnaryOperator<JgpioMock.Builder> definition) {
    // GIVEN
    Jgpio jgpio = definition.apply(JgpioMock.builder())
        .build()
        .jgpio();

    // WHEN
    Chip chip1 = jgpio.openChipByName("gpiochip10");
    Chip chip2 = jgpio.openChipByNumber(10);
    Chip chip3 = jgpio.openChipByPath(Path.of("/dev/gpiochip10"));

    // THEN
    assertThat(chip1.name()).isEqualTo("gpiochip10");
    assertThat(chip2.name()).isEqualTo("gpiochip10");
    assertThat(chip3.name()).isEqualTo("gpiochip10");
  }

  @Test
  void chip_multipleChips_allChipsInGetChips() {
    // GIVEN
    Jgpio jgpio = JgpioMock.builder()
        .chip("gpiochip0", c -> {
        })
        .chip("gpiochip1", c -> {
        })
        .build()
        .jgpio();

    // WHEN
    var chips = jgpio.getChips();

    // THEN
    assertThat(chips).extracting(ChipInfo::name)
        .containsExactly("gpiochip0", "gpiochip1");
  }

  @Test
  void chipInfo_open_opensChip() {
    // GIVEN
    Jgpio jgpio = JgpioMock.builder()
        .chip("gpiochip0", c -> c.line(0, "gpio0"))
        .build()
        .jgpio();

    // WHEN
    Chip chip = jgpio.getChips().getFirst().open();

    // THEN
    assertThat(chip.name()).isEqualTo("gpiochip0");
    assertThat(chip.countLines()).isEqualTo(1);
  }

  @Test
  void openChip_unknownChip_throws() {
    // GIVEN
    Jgpio jgpio = JgpioMock.builder()
        .chip("gpiochip0", c -> {
        })
        .build()
        .jgpio();

    // WHEN-THEN
    assertThatThrownBy(() -> jgpio.openChipByName("gpiochip1"))
        .isInstanceOf(JgpioException.class);
  }

  @Test
  void openChip_afterPreviousChipClosed_returnsOpenChip() {
    // GIVEN
    Jgpio jgpio = JgpioMock.builder()
        .chip("gpiochip0", c -> {
        })
        .build()
        .jgpio();
    jgpio.openChipByName("gpiochip0").close();

    // WHEN
    Chip chip = jgpio.openChipByName("gpiochip0");

    // THEN
    assertThat(chip.isClosed()).isFalse();
    assertThat(chip.name()).isEqualTo("gpiochip0");
  }

  @Test
  void getLine_unknownLine_throws() {
    // GIVEN
    Chip chip = JgpioMock.builder()
        .chip("gpiochip0", c -> c.line(0, "gpio0"))
        .build()
        .jgpio()
        .openChipByName("gpiochip0");

    // WHEN-THEN
    assertThat(chip.findLine(1)).isEmpty();
    assertThat(chip.findLine("gpio1")).isEmpty();
    assertThatThrownBy(() -> chip.getLine(1)).isInstanceOf(JgpioException.class);
    assertThatThrownBy(() -> chip.getLine("gpio1")).isInstanceOf(JgpioException.class);
  }
}
