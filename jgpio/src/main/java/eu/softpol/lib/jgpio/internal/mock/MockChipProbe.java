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

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;

import eu.softpol.lib.jgpio.mock.ChipProbe;
import eu.softpol.lib.jgpio.mock.LineProbe;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/// State of a mocked chip, shared by all [MockChip]s opened for it.
public class MockChipProbe implements ChipProbe {

  private final String name;
  private final String label;
  private final Path path;
  private final int number;

  private final List<MockLineProbe> lines;

  public MockChipProbe(
      String name,
      String label,
      Path path,
      int number,
      List<MockLineProbe> lines
  ) {
    this.name = name;
    this.label = label;
    this.path = path;
    this.number = number;
    this.lines = List.copyOf(lines);
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
  public LineProbe line(int offset) {
    return findLine(offset)
        .orElseThrow(() -> new IllegalArgumentException("No mock line with offset " + offset));
  }

  @Override
  public LineProbe line(String name) {
    checkNonNull(name, "name");
    return findLine(name)
        .orElseThrow(() -> new IllegalArgumentException("No mock line with name '" + name + "'"));
  }

  protected Path path() {
    return path;
  }

  protected int number() {
    return number;
  }

  protected int countLines() {
    return lines.size();
  }

  protected Optional<MockLineProbe> findLine(int offset) {
    if (offset < 0 || offset >= lines.size()) {
      return Optional.empty();
    }
    return Optional.of(lines.get(offset));
  }

  protected Optional<MockLineProbe> findLine(String name) {
    return lines.stream()
        .filter(l -> name.equals(l.name()))
        .findFirst();
  }
}
