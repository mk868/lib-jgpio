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
package eu.softpol.lib.jgpio.internal.mock;

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNegative;
import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;

import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.ChipInfo;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.internal.JgpioExceptions;
import java.nio.file.Path;
import java.util.List;
import org.jspecify.annotations.Nullable;

/// Mocked [Jgpio] API. Each opened chip is a new [MockChip] over the shared chip state.
public class MockJgpio implements Jgpio {

  private final List<MockChipProbe> chips;

  public MockJgpio(List<MockChipProbe> chips) {
    this.chips = chips;
  }

  @Override
  public Chip openChipByName(String name) {
    checkNonNull(name, "name");
    return chips.stream()
        .filter(chip -> name.equals(chip.name()))
        .findFirst()
        .map(MockChip::new)
        .orElseThrow(() -> JgpioExceptions.chipOpenFailedByName(name));
  }

  @Override
  public Chip openChipByLabel(String label) {
    checkNonNull(label, "label");
    return chips.stream()
        .filter(chip -> label.equals(chip.label()))
        .findFirst()
        .map(MockChip::new)
        .orElseThrow(() -> JgpioExceptions.chipOpenFailedByLabel(label));
  }

  @Override
  public Chip openChipByPath(Path path) {
    checkNonNull(path, "path");
    return chips.stream()
        .filter(chip -> path.equals(chip.path()))
        .findFirst()
        .map(MockChip::new)
        .orElseThrow(() -> JgpioExceptions.chipOpenFailedByPath(path));
  }

  @Override
  public Chip openChipByNumber(int number) {
    checkNonNegative(number, "number");
    return chips.stream()
        .filter(chip -> chip.number() == number)
        .findFirst()
        .map(MockChip::new)
        .orElseThrow(() -> JgpioExceptions.chipOpenFailedByNumber(number));
  }

  @Override
  public List<ChipInfo> getChips() {
    return chips.stream()
        .<ChipInfo>map(MockChipInfo::new)
        .toList();
  }

  @Override
  public @Nullable String version() {
    return "mock";
  }
}
