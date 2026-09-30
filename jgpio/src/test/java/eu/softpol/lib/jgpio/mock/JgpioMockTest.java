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

import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.InputMode;
import eu.softpol.lib.jgpio.Line;
import eu.softpol.lib.jgpio.OutputMode;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JgpioMockTest {

  static Stream<MockLineRequest> externalRequests() {
    return Stream.of(MockLineRequest.asInput("other-app"), MockLineRequest.asOutput("other-app"));
  }

  @Nested
  class ChipBuilder {

    @Nested
    class Label {

      @Test
      void chipAccessibleByLabel() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.label("pinctrl-bcm2711"))
            .build();

        // WHEN
        Chip chip = mock.jgpio().openChipByLabel("pinctrl-bcm2711");

        // THEN
        assertThat(chip.label()).isEqualTo("pinctrl-bcm2711");
      }
    }

    @Nested
    class Lines {

      @Test
      void lineAccessibleByOffset() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(2, l -> {
            }))
            .build();

        // WHEN
        Line line = mock.jgpio().openChipByName("gpiochip0").getLine(2);

        // THEN
        assertThat(line.offset()).isEqualTo(2);
      }

      @Test
      void multipleLines_allAccessible() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c
                .line(0, l -> {
                })
                .line(1, l -> {
                })
                .line(4, l -> {
                }))
            .build();
        Chip chip = mock.jgpio().openChipByName("gpiochip0");

        // WHEN-THEN
        assertThat(chip.findLine(0)).isPresent();
        assertThat(chip.findLine(1)).isPresent();
        assertThat(chip.findLine(2)).isPresent();
        assertThat(chip.findLine(3)).isPresent();
        assertThat(chip.findLine(4)).isPresent();
        assertThat(chip.findLine(5)).isEmpty();
      }

      @Test
      void gapBeforeLine_countLinesIsHighestOffsetPlusOne() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(14, "GPIO14"))
            .build();

        // WHEN
        int count = mock.jgpio().openChipByName("gpiochip0").countLines();

        // THEN
        assertThat(count).isEqualTo(15);
      }

      @Test
      void gapBeforeLine_chipInfoCountLinesIsHighestOffsetPlusOne() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(14, "GPIO14"))
            .build();

        // WHEN
        int count = mock.jgpio().getChips().getFirst().countLines();

        // THEN
        assertThat(count).isEqualTo(15);
      }

      @Test
      void gapBeforeLine_unconfiguredLinesExistUnnamedAndUnused() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(14, "GPIO14"))
            .build();
        Chip chip = mock.jgpio().openChipByName("gpiochip0");

        // WHEN-THEN
        for (int offset = 0; offset < 14; offset++) {
          Line line = chip.findLine(offset).orElseThrow();
          assertThat(line.offset()).isEqualTo(offset);
          assertThat(line.name()).isNull();
          assertThat(line.isUsed()).isFalse();
          assertThat(line.direction()).isEqualTo(Direction.INPUT);
        }
        assertThat(chip.getLine(14).name()).isEqualTo("GPIO14");
        assertThat(chip.findLine(15)).isEmpty();
      }

      @Test
      void gapBeforeLine_unconfiguredLineAccessibleThroughProbe() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(14, "GPIO14"))
            .build();
        LineProbe probe = mock.chip("gpiochip0").line(3);
        probe.setInputLevel(InputLevel.HIGH);

        // WHEN
        boolean value;
        try (var session = mock.jgpio().openChipByName("gpiochip0").getLine(3).openAsInput()) {
          value = session.read();
        }

        // THEN
        assertThat(probe.offset()).isEqualTo(3);
        assertThat(probe.name()).isNull();
        assertThat(value).isTrue();
      }

      @Test
      void noLines_countLinesIsZero() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> {
            })
            .build();

        // WHEN
        int count = mock.jgpio().openChipByName("gpiochip0").countLines();

        // THEN
        assertThat(count).isZero();
      }
    }

    @Nested
    class LineShortcut {

      @Test
      void lineAccessibleByName() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(3, "gpio3"))
            .build();

        // WHEN
        Line line = mock.jgpio().openChipByName("gpiochip0").getLine("gpio3");

        // THEN
        assertThat(line.name()).isEqualTo("gpio3");
        assertThat(line.offset()).isEqualTo(3);
      }
    }
  }

  @Nested
  class LineBuilder {

    @Nested
    class Name {

      @Test
      void lineAccessibleByName() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(5, l -> l.name("my-line")))
            .build();

        // WHEN
        Line line = mock.jgpio().openChipByName("gpiochip0").getLine("my-line");

        // THEN
        assertThat(line.name()).isEqualTo("my-line");
      }
    }

    @Nested
    class InputLevels {

      @Test
      void high_readReturnsTrue() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(0, l -> l.inputLevel(InputLevel.HIGH)))
            .build();
        Line line = mock.jgpio().openChipByName("gpiochip0").getLine(0);

        // WHEN
        boolean value;
        try (var session = line.openAsInput(InputMode.builder().build())) {
          value = session.read();
        }

        // THEN
        assertThat(value).isTrue();
      }

      @Test
      void low_readReturnsFalse() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(0, l -> l.inputLevel(InputLevel.LOW)))
            .build();
        Line line = mock.jgpio().openChipByName("gpiochip0").getLine(0);

        // WHEN
        boolean value;
        try (var session = line.openAsInput(InputMode.builder().build())) {
          value = session.read();
        }

        // THEN
        assertThat(value).isFalse();
      }
    }

    @Nested
    class ExternallyRequested {

      @ParameterizedTest
      @MethodSource("eu.softpol.lib.jgpio.mock.JgpioMockTest#externalRequests")
      void lineIsRequested(MockLineRequest request) {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(0, l -> l.externallyRequested(request)))
            .build();

        // WHEN
        LineProbe line = mock.chip("gpiochip0").line(0);

        // THEN
        assertThat(line.isRequested()).isTrue();
        assertThat(line.request()).contains(request);
      }

      @Test
      void asOutput_apiReportsUsedOutputWithConsumer() {
        // GIVEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(1,
                l -> l.externallyRequested(MockLineRequest.asOutput("other-app"))))
            .build();

        // WHEN
        Line line = mock.jgpio().openChipByName("gpiochip0").getLine(1);

        // THEN
        assertThat(line.isUsed()).isTrue();
        assertThat(line.direction()).isEqualTo(Direction.OUTPUT);
        assertThat(line.consumer()).isEqualTo("other-app");
      }
    }
  }

  @Nested
  class ProbeAndApi {

    @Test
    void inputLevelSetOnProbe_readThroughApi() {
      // GIVEN
      JgpioMock mock = JgpioMock.builder()
          .chip("gpiochip0", c -> c.line(0, "gpio0"))
          .build();
      LineProbe probe = mock.chip("gpiochip0").line(0);
      Line line = mock.jgpio().openChipByName("gpiochip0").getLine(0);

      try (var session = line.openAsInput()) {
        // WHEN
        probe.setInputLevel(InputLevel.HIGH);
        boolean high = session.read();
        probe.setInputLevel(InputLevel.LOW);
        boolean low = session.read();

        // THEN
        assertThat(high).isTrue();
        assertThat(low).isFalse();
      }
    }

    @Test
    void outputWrittenThroughApi_visibleOnProbe() {
      // GIVEN
      JgpioMock mock = JgpioMock.builder()
          .chip("gpiochip0", c -> c.line(0, "gpio0"))
          .build();
      LineProbe probe = mock.chip("gpiochip0").line(0);

      // WHEN
      mock.jgpio().openChipByName("gpiochip0").getLine(0)
          .openAsOutput(OutputMode.builder().consumer("blinker").build())
          .write(true);

      // THEN
      assertThat(probe.outputValue()).isTrue();
      assertThat(probe.request()).contains(MockLineRequest.asOutput("blinker"));
    }
  }
}
