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

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.InputMode;
import eu.softpol.lib.jgpio.OutputMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LineProbeSettingsTest {

  private Chip chip;
  private LineProbe line;

  @BeforeEach
  void setUp() {
    JgpioMock mock = JgpioMock.builder()
        .chip("gpiochip0", c -> c.line(0, "gpio0"))
        .build();
    chip = mock.jgpio().openChipByName("gpiochip0");
    line = mock.chip("gpiochip0").line(0);
  }

  @Test
  void defaults() {
    // WHEN-THEN
    assertThat(line.bias()).isEqualTo(Bias.HIGH_IMPEDANCE);
    assertThat(line.driveMode()).isEqualTo(DriveMode.PUSH_PULL);
  }

  @Nested
  class BiasSetting {

    @Test
    void openWithBias_applied() {
      // WHEN
      chip.getLine(0).openAsInput(Bias.PULL_UP);

      // THEN
      assertThat(line.bias()).isEqualTo(Bias.PULL_UP);
    }

    @Test
    void setBias_applied() {
      // GIVEN
      var session = chip.getLine(0).openAsInput();

      // WHEN
      session.setBias(Bias.PULL_DOWN);

      // THEN
      assertThat(line.bias()).isEqualTo(Bias.PULL_DOWN);
    }

    @Test
    void openWithoutBias_keepsCurrent() {
      // GIVEN
      chip.getLine(0).openAsInput(Bias.PULL_UP).close();

      // WHEN
      chip.getLine(0).openAsInput(InputMode.builder().consumer("reader").build());

      // THEN
      assertThat(line.bias()).isEqualTo(Bias.PULL_UP);
    }

    @Test
    void keptAfterChipClose() {
      // GIVEN
      chip.getLine(0).openAsInput(Bias.PULL_UP);

      // WHEN
      chip.close();

      // THEN
      assertThat(line.bias()).isEqualTo(Bias.PULL_UP);
      assertThat(line.isRequested()).isFalse();
    }

    @Test
    void highImpedanceInput_readFollowsBias() {
      // GIVEN
      line.setInputLevel(InputLevel.HIGH_IMPEDANCE);
      var session = chip.getLine(0).openAsInput(Bias.PULL_UP);

      // WHEN
      boolean pulledUp = session.read();
      session.setBias(Bias.PULL_DOWN);
      boolean pulledDown = session.read();

      // THEN
      assertThat(pulledUp).isTrue();
      assertThat(pulledDown).isFalse();
    }
  }

  @Nested
  class DriveModeSetting {

    @Test
    void openWithDriveMode_applied() {
      // WHEN
      chip.getLine(0).openAsOutput(DriveMode.OPEN_DRAIN);

      // THEN
      assertThat(line.driveMode()).isEqualTo(DriveMode.OPEN_DRAIN);
    }

    @Test
    void setDriveMode_applied() {
      // GIVEN
      var session = chip.getLine(0).openAsOutput();

      // WHEN
      session.setDriveMode(DriveMode.OPEN_SOURCE);

      // THEN
      assertThat(line.driveMode()).isEqualTo(DriveMode.OPEN_SOURCE);
    }

    @Test
    void openWithoutDriveMode_keepsCurrent() {
      // GIVEN
      chip.getLine(0).openAsOutput(DriveMode.OPEN_DRAIN).close();

      // WHEN
      chip.getLine(0).openAsOutput(OutputMode.builder().consumer("writer").build());

      // THEN
      assertThat(line.driveMode()).isEqualTo(DriveMode.OPEN_DRAIN);
    }

    @Test
    void keptAfterSessionClose() {
      // GIVEN
      var session = chip.getLine(0).openAsOutput(DriveMode.OPEN_DRAIN);

      // WHEN
      session.close();

      // THEN
      assertThat(line.driveMode()).isEqualTo(DriveMode.OPEN_DRAIN);
      assertThat(line.isRequested()).isFalse();
    }
  }

  @Nested
  class OutputValueOnOpen {

    @Test
    void withoutInitialValue_previousValueResetToLow() {
      // GIVEN
      try (var first = chip.getLine(0).openAsOutput()) {
        first.write(true);
      }

      // WHEN
      chip.getLine(0).openAsOutput();

      // THEN
      assertThat(line.outputValue()).isFalse();
    }

    @Test
    void withInitialValue_applied() {
      // WHEN
      chip.getLine(0).openAsOutput(OutputMode.builder().initialValue(true).build());

      // THEN
      assertThat(line.outputValue()).isTrue();
    }

    @Test
    void valueKeptAfterSessionClose() {
      // GIVEN
      var session = chip.getLine(0).openAsOutput();
      session.write(true);

      // WHEN
      session.close();

      // THEN
      assertThat(line.outputValue()).isTrue();
    }

    @Test
    void inputSession_outputValueUnchanged() {
      // GIVEN
      try (var output = chip.getLine(0).openAsOutput()) {
        output.write(true);
      }

      // WHEN
      chip.getLine(0).openAsInput();

      // THEN
      assertThat(line.outputValue()).isTrue();
    }
  }
}
