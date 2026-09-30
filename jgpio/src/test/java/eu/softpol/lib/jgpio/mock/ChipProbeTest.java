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

import org.junit.jupiter.api.Test;

public class ChipProbeTest {

  @Test
  void chip_returnsChipProbe() {
    // GIVEN
    JgpioMock mock = JgpioMock.builder()
        .chip("gpiochip0", c -> c.label("pinctrl"))
        .build();

    // WHEN
    ChipProbe chip = mock.chip("gpiochip0");

    // THEN
    assertThat(chip.name()).isEqualTo("gpiochip0");
    assertThat(chip.label()).isEqualTo("pinctrl");
  }

  @Test
  void chip_unknownChip_throws() {
    // GIVEN
    JgpioMock mock = JgpioMock.builder()
        .chip("gpiochip0", c -> {
        })
        .build();

    // WHEN-THEN
    assertThatThrownBy(() -> mock.chip("gpiochip1"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void line_byOffsetAndName_returnsSameLine() {
    // GIVEN
    ChipProbe chip = JgpioMock.builder()
        .chip("gpiochip0", c -> c.line(3, "gpio3"))
        .build()
        .chip("gpiochip0");

    // WHEN
    LineProbe byOffset = chip.line(3);
    LineProbe byName = chip.line("gpio3");

    // THEN
    assertThat(byOffset).isSameAs(byName);
    assertThat(byOffset.offset()).isEqualTo(3);
    assertThat(byOffset.name()).isEqualTo("gpio3");
  }

  @Test
  void line_unknownLine_throws() {
    // GIVEN
    ChipProbe chip = JgpioMock.builder()
        .chip("gpiochip0", c -> c.line(0, "gpio0"))
        .build()
        .chip("gpiochip0");

    // WHEN-THEN
    assertThatThrownBy(() -> chip.line(1))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> chip.line("gpio1"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
