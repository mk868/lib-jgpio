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
import eu.softpol.lib.jgpio.InputMode;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.OutputMode;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MockLineBusyTest {

  private JgpioMock mock;
  private Chip chip;
  private LineProbe line;

  @BeforeEach
  void setUp() {
    mock = JgpioMock.builder()
        .chip("gpiochip0", c -> c.line(0, "gpio0"))
        .build();
    chip = mock.jgpio().openChipByName("gpiochip0");
    line = mock.chip("gpiochip0").line(0);
  }

  @Test
  void openTwice_throws() {
    // GIVEN
    chip.getLine(0).openAsInput(InputMode.builder().consumer("first").build());

    // WHEN-THEN
    assertThatThrownBy(() -> chip.getLine(0).openAsInput())
        .isInstanceOf(JgpioException.class)
        .hasMessageContaining("line 0")
        .hasMessageContaining("'first'");
  }

  @Test
  void openAsOutputWhileOpenAsInput_throws() {
    // GIVEN
    chip.getLine(0).openAsInput();

    // WHEN-THEN
    assertThatThrownBy(() -> chip.getLine(0).openAsOutput())
        .isInstanceOf(JgpioException.class);
  }

  @Test
  void openThroughOtherChipHandle_throws() {
    // GIVEN
    chip.getLine(0).openAsOutput();
    Chip other = mock.jgpio().openChipByName("gpiochip0");

    // WHEN-THEN
    assertThatThrownBy(() -> other.getLine(0).openAsOutput())
        .isInstanceOf(JgpioException.class);
  }

  @Test
  void externallyRequested_throws() {
    // GIVEN
    line.setExternallyRequested(MockLineRequest.asOutput("kernel"));

    // WHEN-THEN
    assertThatThrownBy(() -> chip.getLine(0).openAsInput())
        .isInstanceOf(JgpioException.class)
        .hasMessageContaining("'kernel'");
  }

  @Test
  void externalRequestCleared_opens() {
    // GIVEN
    line.setExternallyRequested(MockLineRequest.asOutput("kernel"));
    line.clearExternallyRequested();

    // WHEN
    var session = chip.getLine(0).openAsInput();

    // THEN
    assertThat(session.isClosed()).isFalse();
    assertThat(line.isRequested()).isTrue();
  }

  @Test
  void afterSessionClose_opens() {
    // GIVEN
    chip.getLine(0).openAsInput().close();

    // WHEN
    var session = chip.getLine(0).openAsOutput();

    // THEN
    assertThat(session.isClosed()).isFalse();
    assertThat(line.request()).containsInstanceOf(MockLineRequest.AsOutput.class);
  }

  @Test
  void afterChipClose_opensThroughNewChip() {
    // GIVEN
    chip.getLine(0).openAsOutput();
    chip.close();

    // WHEN
    var session = mock.jgpio().openChipByName("gpiochip0").getLine(0).openAsOutput();

    // THEN
    assertThat(session.isClosed()).isFalse();
  }

  @Test
  void rejectedOutputRequest_lineStateUnchanged() {
    // GIVEN
    var first = chip.getLine(0).openAsOutput(OutputMode.builder()
        .consumer("first")
        .driveMode(DriveMode.OPEN_DRAIN)
        .initialValue(true)
        .build());
    List<Boolean> writes = new ArrayList<>();
    line.setWriteCallback(writes::add);

    // WHEN
    assertThatThrownBy(() -> chip.getLine(0).openAsOutput(OutputMode.builder()
        .consumer("second")
        .driveMode(DriveMode.PUSH_PULL)
        .initialValue(false)
        .build()))
        .isInstanceOf(JgpioException.class);

    // THEN
    assertThat(line.outputValue()).isTrue();
    assertThat(line.driveMode()).isEqualTo(DriveMode.OPEN_DRAIN);
    assertThat(line.request()).contains(MockLineRequest.asOutput("first"));
    assertThat(writes).isEmpty();
    assertThat(first.isClosed()).isFalse();
  }

  @Test
  void rejectedInputRequest_biasUnchanged() {
    // GIVEN
    chip.getLine(0).openAsInput(Bias.PULL_UP);

    // WHEN
    assertThatThrownBy(() -> chip.getLine(0).openAsInput(Bias.PULL_DOWN))
        .isInstanceOf(JgpioException.class);

    // THEN
    assertThat(line.bias()).isEqualTo(Bias.PULL_UP);
  }

  @Test
  void rejectedRequest_firstSessionStillWorks() {
    // GIVEN
    var first = chip.getLine(0).openAsOutput();
    assertThatThrownBy(() -> chip.getLine(0).openAsOutput())
        .isInstanceOf(JgpioException.class);

    // WHEN
    first.write(true);
    first.close();

    // THEN
    assertThat(line.outputValue()).isTrue();
    assertThat(line.isRequested()).isFalse();
  }
}
