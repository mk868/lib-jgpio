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

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.OutputMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LineProbeCallbackTest {

  private JgpioMock mock;
  private Chip chip;
  private LineProbe line;

  @BeforeEach
  void setUp() {
    mock = JgpioMock.builder()
        .chip("gpiochip0", c -> c
            .line(0, "gpio0")
            .line(1, "gpio1"))
        .build();
    chip = mock.jgpio().openChipByName("gpiochip0");
    line = mock.chip("gpiochip0").line(0);
  }

  @Nested
  class Write {

    @Test
    void calledWithEachWrittenValue() {
      // GIVEN
      List<Boolean> writes = new ArrayList<>();
      line.setWriteCallback(writes::add);

      // WHEN
      try (var session = chip.getLine(0).openAsOutput()) {
        session.write(true);
        session.write(false);
        session.write(true);
      }

      // THEN
      assertThat(writes).containsExactly(false, true, false, true);
    }

    @Test
    void calledWithLowOnOpenWithoutInitialValue() {
      // GIVEN
      List<Boolean> writes = new ArrayList<>();
      line.setWriteCallback(writes::add);

      // WHEN
      chip.getLine(0).openAsOutput();

      // THEN
      assertThat(writes).containsExactly(false);
    }

    @Test
    void calledWithInitialValue() {
      // GIVEN
      List<Boolean> writes = new ArrayList<>();
      line.setWriteCallback(writes::add);

      // WHEN
      chip.getLine(0).openAsOutput(OutputMode.builder().initialValue(true).build());

      // THEN
      assertThat(writes).containsExactly(true);
    }

    @Test
    void notCalledWhenWriteFails() {
      // GIVEN
      var session = chip.getLine(0).openAsOutput();
      session.close();
      List<Boolean> writes = new ArrayList<>();
      line.setWriteCallback(writes::add);

      // WHEN
      assertThatThrownBy(() -> session.write(true)).isInstanceOf(IllegalStateException.class);

      // THEN
      assertThat(writes).isEmpty();
    }

    @Test
    void setAgain_replacesPrevious() {
      // GIVEN
      List<Boolean> first = new ArrayList<>();
      List<Boolean> second = new ArrayList<>();
      line.setWriteCallback(first::add);
      line.setWriteCallback(second::add);

      // WHEN
      chip.getLine(0).openAsOutput().write(true);

      // THEN
      assertThat(first).isEmpty();
      assertThat(second).containsExactly(false, true);
    }

    @Test
    void cleared_notCalled() {
      // GIVEN
      List<Boolean> writes = new ArrayList<>();
      line.setWriteCallback(writes::add);
      line.clearWriteCallback();

      // WHEN
      chip.getLine(0).openAsOutput().write(true);

      // THEN
      assertThat(writes).isEmpty();
      assertThat(line.outputValue()).isTrue();
    }

    @Test
    void exception_propagatedToCaller() {
      // GIVEN
      var session = chip.getLine(0).openAsOutput();
      line.setWriteCallback(v -> {
        throw new JgpioException("simulated failure");
      });

      // WHEN-THEN
      assertThatThrownBy(() -> session.write(true))
          .isInstanceOf(JgpioException.class)
          .hasMessage("simulated failure");
    }

    @Test
    void exceptionOnInitialValue_lineReleased() {
      // GIVEN
      line.setWriteCallback(v -> {
        throw new JgpioException("simulated failure");
      });

      // WHEN
      assertThatThrownBy(() -> chip.getLine(0)
          .openAsOutput(OutputMode.builder().initialValue(true).build()))
          .isInstanceOf(JgpioException.class);

      // THEN
      assertThat(line.isRequested()).isFalse();
    }

    @Test
    void exceptionOnOpenWithoutInitialValue_lineReleased() {
      // GIVEN
      line.setWriteCallback(v -> {
        throw new JgpioException("simulated failure");
      });

      // WHEN
      assertThatThrownBy(() -> chip.getLine(0).openAsOutput())
          .isInstanceOf(JgpioException.class);

      // THEN
      assertThat(line.isRequested()).isFalse();
    }

    @Test
    void keptAfterChipClose() {
      // GIVEN
      List<Boolean> writes = new ArrayList<>();
      line.setWriteCallback(writes::add);
      chip.close();

      // WHEN
      try (Chip reopened = mock.jgpio().openChipByName("gpiochip0");
          var session = reopened.getLine(0).openAsOutput()) {
        session.write(true);
      }

      // THEN
      assertThat(writes).containsExactly(false, true);
    }

    @Test
    void loopback_outputDrivesInput() {
      // GIVEN
      LineProbe input = mock.chip("gpiochip0").line(1);
      line.setWriteCallback(v -> input.setInputLevel(v ? InputLevel.HIGH : InputLevel.LOW));

      try (var out = chip.getLine(0).openAsOutput();
          var in = chip.getLine(1).openAsInput()) {
        // WHEN
        out.write(true);
        boolean high = in.read();
        out.write(false);
        boolean low = in.read();

        // THEN
        assertThat(high).isTrue();
        assertThat(low).isFalse();
      }
    }
  }

  @Nested
  class Read {

    @Test
    void levelTakenFromCallback() {
      // GIVEN
      var reads = new AtomicInteger();
      line.setReadCallback(() -> reads.incrementAndGet() >= 3 ? InputLevel.HIGH : InputLevel.LOW);

      try (var session = chip.getLine(0).openAsInput()) {
        // WHEN
        boolean first = session.read();
        boolean second = session.read();
        boolean third = session.read();

        // THEN
        assertThat(first).isFalse();
        assertThat(second).isFalse();
        assertThat(third).isTrue();
        assertThat(reads).hasValue(3);
      }
    }

    @Test
    void takesPrecedenceOverInputLevel() {
      // GIVEN
      line.setInputLevel(InputLevel.LOW);
      line.setReadCallback(() -> InputLevel.HIGH);

      // WHEN
      boolean value = chip.getLine(0).openAsInput().read();

      // THEN
      assertThat(value).isTrue();
    }

    @Test
    void highImpedance_followsBias() {
      // GIVEN
      line.setReadCallback(() -> InputLevel.HIGH_IMPEDANCE);

      // WHEN
      boolean value = chip.getLine(0).openAsInput(Bias.PULL_UP).read();

      // THEN
      assertThat(value).isTrue();
    }

    @Test
    void cleared_inputLevelUsedAgain() {
      // GIVEN
      line.setInputLevel(InputLevel.LOW);
      line.setReadCallback(() -> InputLevel.HIGH);
      line.clearReadCallback();

      // WHEN
      boolean value = chip.getLine(0).openAsInput().read();

      // THEN
      assertThat(value).isFalse();
    }

    @Test
    void notCalledWhenReadFails() {
      // GIVEN
      var reads = new AtomicInteger();
      line.setReadCallback(() -> {
        reads.incrementAndGet();
        return InputLevel.HIGH;
      });
      var session = chip.getLine(0).openAsInput();
      session.close();

      // WHEN
      assertThatThrownBy(session::read).isInstanceOf(IllegalStateException.class);

      // THEN
      assertThat(reads).hasValue(0);
    }

    @Test
    void exception_propagatedToCaller() {
      // GIVEN
      line.setReadCallback(() -> {
        throw new JgpioException("simulated failure");
      });
      var session = chip.getLine(0).openAsInput();

      // WHEN-THEN
      assertThatThrownBy(session::read)
          .isInstanceOf(JgpioException.class)
          .hasMessage("simulated failure");
    }

    @Test
    void nullLevel_throws() {
      // GIVEN
      line.setReadCallback(() -> null);
      var session = chip.getLine(0).openAsInput();

      // WHEN-THEN
      assertThatThrownBy(session::read).isInstanceOf(IllegalStateException.class);
    }
  }

  @Test
  @SuppressWarnings("NullAway")
  void setNullCallback_throws() {
    // WHEN-THEN
    assertThatThrownBy(() -> line.setWriteCallback(null))
        .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> line.setReadCallback(null))
        .isInstanceOf(NullPointerException.class);
  }
}
