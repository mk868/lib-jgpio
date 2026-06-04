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

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.internal.mock.JgpioMockBuilderImpl;
import java.nio.file.Path;
import java.util.function.Consumer;

/// Entry point of the mock: builds the mocked GPIO setup and gives access to it through probes.
///
/// Pass [#jgpio()] to the code under test. Use [#chip(String)] in the test to set input values
/// and verify outputs. Probes stay usable after the code under test closes its chips.
public interface JgpioMock {

  /// Returns the mocked [Jgpio] API, which behaves like a real backend.
  Jgpio jgpio();

  /// Returns the probe of the chip with the given name.
  ///
  /// @throws IllegalArgumentException if no such chip was configured
  ChipProbe chip(String name);

  static Builder builder() {
    return new JgpioMockBuilderImpl();
  }

  interface Builder {

    Builder chip(String name, Consumer<ChipBuilder> chipBuilder);

    Builder chip(int number, Consumer<ChipBuilder> chipBuilder);

    Builder chip(Path path, Consumer<ChipBuilder> chipBuilder);

    JgpioMock build();
  }

  interface ChipBuilder {

    ChipBuilder label(String label);

    ChipBuilder line(int offset, Consumer<LineBuilder> lineBuilder);

    default ChipBuilder line(int offset, String name) {
      checkNonNull(name, "name");
      line(offset, l -> l.name(name));
      return this;
    }
  }

  interface LineBuilder {

    LineBuilder name(String name);

    LineBuilder inputLevel(InputLevel level);

    /// Simulate line request by another application
    LineBuilder externallyRequested(MockLineRequest request);
  }
}
