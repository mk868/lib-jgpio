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
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MockCloseTest {

  private JgpioMock mock;
  private Chip chip;

  @BeforeEach
  void setUp() {
    mock = JgpioMock.builder()
        .chip("gpiochip0", c -> c.line(0, "gpio0"))
        .build();
    chip = mock.jgpio().openChipByName("gpiochip0");
  }

  @Nested
  class ChipClosed {

    @Test
    void isClosed_returnsTrue() {
      // WHEN
      chip.close();

      // THEN
      assertThat(chip.isClosed()).isTrue();
    }

    @Test
    void chipApi_throws() {
      // WHEN
      chip.close();

      // THEN
      assertThatThrownBy(chip::name).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(chip::label).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(chip::countLines).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(() -> chip.findLine(0)).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(() -> chip.getLine(0)).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(() -> chip.findLine("gpio0"))
          .isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(() -> chip.getLine("gpio0"))
          .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void close_calledTwice_doesNotThrow() {
      // WHEN
      chip.close();
      chip.close();

      // THEN
      assertThat(chip.isClosed()).isTrue();
    }

    @Test
    void lineApi_throws() {
      // GIVEN
      Line line = chip.getLine(0);

      // WHEN
      chip.close();

      // THEN
      assertThatThrownBy(line::offset).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(line::name).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(line::consumer).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(line::direction).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(line::isUsed).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(line::openAsInput).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(line::openAsOutput).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void otherOpenedChip_notAffected() {
      // GIVEN
      Chip other = mock.jgpio().openChipByName("gpiochip0");

      // WHEN
      chip.close();

      // THEN
      assertThat(other.isClosed()).isFalse();
      assertThat(other.getLine(0).offset()).isZero();
    }

    @Test
    void openInputSession_released() {
      // GIVEN
      chip.getLine(0).openAsInput();

      // WHEN
      chip.close();

      // THEN
      assertThat(mock.chip("gpiochip0").line(0).isRequested()).isFalse();
    }

    @Test
    void openOutputSession_releasedAndValueKept() {
      // GIVEN
      chip.getLine(0).openAsOutput().write(true);

      // WHEN
      chip.close();

      // THEN
      LineProbe line = mock.chip("gpiochip0").line(0);
      assertThat(line.isRequested()).isFalse();
      assertThat(line.outputValue()).isTrue();
    }

    @Test
    void externalRequest_kept() {
      // GIVEN
      MockLineRequest request = MockLineRequest.asOutput("other-app");
      LineProbe line = mock.chip("gpiochip0").line(0);
      line.setExternallyRequested(request);

      // WHEN
      chip.close();

      // THEN
      assertThat(line.request()).contains(request);
    }

    @Test
    void probes_stillWork() {
      // WHEN
      chip.close();

      // THEN
      ChipProbe mockChip = mock.chip("gpiochip0");
      LineProbe line = mockChip.line(0);
      assertThat(mockChip.name()).isEqualTo("gpiochip0");
      assertThat(line.offset()).isZero();
      assertThat(line.name()).isEqualTo("gpio0");
      line.setInputLevel(InputLevel.HIGH);
      line.setExternallyRequested(MockLineRequest.asInput("other-app"));
      assertThat(line.request()).contains(MockLineRequest.asInput("other-app"));
      line.clearExternallyRequested();
      assertThat(line.isRequested()).isFalse();
    }
  }

  @Nested
  class InputSession {

    @Test
    void sessionClosed_throws() {
      // GIVEN
      var session = chip.getLine(0).openAsInput();

      // WHEN
      session.close();

      // THEN
      assertThat(session.isClosed()).isTrue();
      assertThatThrownBy(session::read).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(() -> session.setBias(Bias.PULL_UP))
          .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void chipClosed_sessionIsClosedAndThrows() {
      // GIVEN
      var session = chip.getLine(0).openAsInput();

      // WHEN
      chip.close();

      // THEN
      assertThat(session.isClosed()).isTrue();
      assertThatThrownBy(session::read).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(() -> session.setBias(Bias.PULL_UP))
          .isInstanceOf(IllegalStateException.class);
    }
  }

  @Nested
  class OutputSession {

    @Test
    void sessionClosed_throwsAndValueUnchanged() {
      // GIVEN
      var session = chip.getLine(0).openAsOutput();
      session.write(true);

      // WHEN
      session.close();

      // THEN
      assertThat(session.isClosed()).isTrue();
      assertThatThrownBy(() -> session.write(false)).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(() -> session.setDriveMode(DriveMode.OPEN_DRAIN))
          .isInstanceOf(IllegalStateException.class);
      assertThat(mock.chip("gpiochip0").line(0).outputValue()).isTrue();
    }

    @Test
    void chipClosed_sessionIsClosedAndThrows() {
      // GIVEN
      var session = chip.getLine(0).openAsOutput();

      // WHEN
      chip.close();

      // THEN
      assertThat(session.isClosed()).isTrue();
      assertThatThrownBy(() -> session.write(true)).isInstanceOf(IllegalStateException.class);
      assertThatThrownBy(() -> session.setDriveMode(DriveMode.OPEN_DRAIN))
          .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void close_calledTwice_doesNotThrow() {
      // GIVEN
      var session = chip.getLine(0).openAsOutput();

      // WHEN
      session.close();
      session.close();

      // THEN
      assertThat(session.isClosed()).isTrue();
    }
  }
}
