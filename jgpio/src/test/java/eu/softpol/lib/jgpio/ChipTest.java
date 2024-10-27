/*
 * Copyright 2024 SOFT-POL
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
package eu.softpol.lib.jgpio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ChipTest {

  @Test
  void getLine_offset_validOffset() {
    // GIVEN
    int validOffset = 2;
    Chip chip = mock(Chip.class);
    Line line = mock(Line.class);
    when(chip.findLine(validOffset))
        .thenReturn(Optional.of(line));
    when(chip.getLine(anyInt()))
        .thenCallRealMethod();

    // WHEN
    Line resultedLine = chip.getLine(validOffset);

    // THEN
    assertThat(resultedLine)
        .isEqualTo(line);
  }

  @Test
  void getLine_offset_invalidOffset() {
    // GIVEN
    int invalidOffset = 4;
    Chip chip = mock(Chip.class);
    when(chip.findLine(invalidOffset))
        .thenReturn(Optional.empty());
    when(chip.getLine(anyInt()))
        .thenCallRealMethod();

    // WHEN-THEN
    assertThatThrownBy(() -> chip.getLine(invalidOffset))
        .isInstanceOf(JgpioException.class);
  }

  @Test
  void getLine_name_validName() {
    // GIVEN
    String validName = "valid";
    Chip chip = mock(Chip.class);
    Line line = mock(Line.class);
    when(chip.findLine(validName))
        .thenReturn(Optional.of(line));
    when(chip.getLine(anyString()))
        .thenCallRealMethod();

    // WHEN
    Line resultedLine = chip.getLine(validName);

    // THEN
    assertThat(resultedLine)
        .isEqualTo(line);
  }

  @Test
  void getLine_name_invalidName() {
    // GIVEN
    String invalidName = "invalid";
    Chip chip = mock(Chip.class);
    when(chip.findLine(invalidName))
        .thenReturn(Optional.empty());
    when(chip.getLine(anyString()))
        .thenCallRealMethod();

    // WHEN-THEN
    assertThatThrownBy(() -> chip.getLine(invalidName))
        .isInstanceOf(JgpioException.class);
  }
}
