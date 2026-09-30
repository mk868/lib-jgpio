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

import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.ChipInfo;

public class MockChipInfo implements ChipInfo {

  private final MockChipProbe chip;
  private final String name;
  private final String label;
  private final int countLines;

  public MockChipInfo(MockChipProbe chip) {
    this.chip = chip;
    this.name = chip.name();
    this.label = chip.label();
    this.countLines = chip.countLines();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String label() {
    return label;
  }

  @Override
  public int countLines() {
    return countLines;
  }

  @Override
  public Chip open() {
    return new MockChip(chip);
  }
}
