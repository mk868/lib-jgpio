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

import eu.softpol.lib.jgpio.mock.InputLevel;
import eu.softpol.lib.jgpio.mock.JgpioMock;
import eu.softpol.lib.jgpio.mock.JgpioMock.Builder;
import eu.softpol.lib.jgpio.mock.JgpioMock.ChipBuilder;
import eu.softpol.lib.jgpio.mock.MockLineRequest;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

public class JgpioMockBuilderImpl implements JgpioMock.Builder {

  private static final Pattern CHIP_NAME = Pattern.compile("gpiochip(\\d+)");

  private final List<MockChipProbe> chips = new ArrayList<>();

  @Override
  public Builder chip(String name, Consumer<ChipBuilder> chipBuilder) {
    checkNonNull(name, "name");
    checkNonNull(chipBuilder, "chipBuilder");
    parseChipNumber(name);
    return chip(Path.of("/dev/" + name), chipBuilder);
  }

  @Override
  public Builder chip(int number, Consumer<ChipBuilder> chipBuilder) {
    checkNonNegative(number, "number");
    checkNonNull(chipBuilder, "chipBuilder");
    return chip(Path.of("/dev/gpiochip" + number), chipBuilder);
  }

  @Override
  public Builder chip(Path path, Consumer<ChipBuilder> chipBuilder) {
    checkNonNull(path, "path");
    checkNonNull(chipBuilder, "chipBuilder");
    var fileName = path.getFileName();
    if (fileName == null) {
      throw new IllegalArgumentException("Chip path '" + path + "' has no file name");
    }
    var name = fileName.toString();
    var number = parseChipNumber(name);
    throwWhenChipAlreadyDefined(name, number);
    var builder = new ChipBuilderImpl(name, number, path);
    chipBuilder.accept(builder);
    chips.add(builder.build());
    return this;
  }

  /// Chip names have the form `gpiochipN`, like the device files in `/dev`; `N` is the number used
  /// by [eu.softpol.lib.jgpio.Jgpio#openChipByNumber(int)].
  private static int parseChipNumber(String name) {
    var matcher = CHIP_NAME.matcher(name);
    if (matcher.matches()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        // too large for int, reported below
      }
    }
    throw new IllegalArgumentException(
        "Chip name '" + name + "' must have the form 'gpiochipN', where N is a chip number");
  }

  /// Name and number each identify a chip, so neither may repeat; the path is covered by the name,
  /// which is its file name. Labels may repeat, like on real systems.
  private void throwWhenChipAlreadyDefined(String name, int number) {
    for (var chip : chips) {
      if (chip.name().equals(name)) {
        throw new IllegalArgumentException("Chip with name '" + name + "' is already defined");
      }
      if (chip.number() == number) {
        throw new IllegalArgumentException("Chip with number " + number + " is already defined");
      }
    }
  }

  @Override
  public JgpioMock build() {
    return new JgpioMockImpl(chips);
  }

  private static class ChipBuilderImpl implements JgpioMock.ChipBuilder {

    private final String name;
    private final int number;
    private final Path path;
    private String label = "";
    private final Map<Integer, MockLineProbe> lines = new HashMap<>();

    ChipBuilderImpl(String name, int number, Path path) {
      this.name = name;
      this.number = number;
      this.path = path;
    }

    @Override
    public ChipBuilder label(String label) {
      checkNonNull(label, "label");
      this.label = label;
      return this;
    }

    @Override
    public ChipBuilder line(int offset, Consumer<JgpioMock.LineBuilder> lineBuilder) {
      checkNonNegative(offset, "offset");
      checkNonNull(lineBuilder, "lineBuilder");
      if (lines.containsKey(offset)) {
        throw new IllegalArgumentException("Line with offset " + offset + " is already defined");
      }
      var builder = new LineBuilderImpl(offset);
      lineBuilder.accept(builder);
      lines.put(offset, builder.build());
      return this;
    }

    MockChipProbe build() {
      return new MockChipProbe(name, label, path, number, linesByOffset());
    }

    /// Like on a real chip, lines are numbered `0..n-1` without gaps: offsets up to the highest
    /// configured one that were not configured become unnamed lines with default state.
    private List<MockLineProbe> linesByOffset() {
      var lineCount = lines.keySet().stream()
                          .mapToInt(Integer::intValue)
                          .max()
                          .orElse(-1) + 1;
      var result = new ArrayList<MockLineProbe>(lineCount);
      for (int offset = 0; offset < lineCount; offset++) {
        var line = lines.get(offset);
        result.add(line != null ? line : new MockLineProbe(offset, null));
      }
      return result;
    }
  }

  private static class LineBuilderImpl implements JgpioMock.LineBuilder {

    private final int offset;
    private @Nullable String name;
    private @Nullable InputLevel inputLevel;
    private @Nullable MockLineRequest externallyRequested;

    LineBuilderImpl(int offset) {
      this.offset = offset;
    }

    @Override
    public JgpioMock.LineBuilder name(String name) {
      checkNonNull(name, "name");
      this.name = name;
      return this;
    }

    @Override
    public JgpioMock.LineBuilder inputLevel(InputLevel level) {
      checkNonNull(level, "level");
      this.inputLevel = level;
      return this;
    }

    @Override
    public JgpioMock.LineBuilder externallyRequested(MockLineRequest request) {
      checkNonNull(request, "request");
      this.externallyRequested = request;
      return this;
    }

    MockLineProbe build() {
      var line = new MockLineProbe(offset, name);
      if (inputLevel != null) {
        line.setInputLevel(inputLevel);
      }
      if (externallyRequested != null) {
        line.setExternallyRequested(externallyRequested);
      }
      return line;
    }
  }
}
