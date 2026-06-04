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
package eu.softpol.lib.jgpio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class LineTest {

  @Test
  void openAsInput_hasDefaultInputMode() {
    // GIVEN
    Line line = mock(Line.class);
    LineInputSession lineInputSession = mock(LineInputSession.class);
    when(line.openAsInput(any(InputMode.class)))
        .thenReturn(lineInputSession);
    when(line.openAsInput())
        .thenCallRealMethod();
    InputMode defaultInputMode = InputMode.builder().build();

    // WHEN
    line.openAsInput();

    // THEN
    ArgumentCaptor<InputMode> inputModeCaptor =
        ArgumentCaptor.forClass(InputMode.class);

    verify(line).openAsInput(inputModeCaptor.capture());

    assertThat(inputModeCaptor.getValue())
        .isEqualTo(defaultInputMode);
  }

  @Test
  void openAsInput_inputModeBiasSet() {
    // GIVEN
    Line line = mock(Line.class);
    LineInputSession lineInputSession = mock(LineInputSession.class);
    when(line.openAsInput(any(InputMode.class)))
        .thenReturn(lineInputSession);
    when(line.openAsInput(any(Bias.class)))
        .thenCallRealMethod();

    // WHEN
    line.openAsInput(Bias.PULL_UP);

    // THEN
    ArgumentCaptor<InputMode> inputModeCaptor =
        ArgumentCaptor.forClass(InputMode.class);

    verify(line).openAsInput(inputModeCaptor.capture());

    assertThat(inputModeCaptor.getValue().bias())
        .isEqualTo(Bias.PULL_UP);
  }

  @Test
  @SuppressWarnings("NullAway")
  void openAsInput_throwWhenBiasIsNull() {
    // GIVEN
    Line line = mock(Line.class);
    LineInputSession lineInputSession = mock(LineInputSession.class);
    when(line.openAsInput(any(InputMode.class)))
        .thenReturn(lineInputSession);
    when(line.openAsInput(nullable(Bias.class)))
        .thenCallRealMethod();

    // WHEN-THEN
    assertThatThrownBy(() -> line.openAsInput((Bias) null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("bias");
  }

  @Test
  void openAsOutput_hasDefaultOutputMode() {
    // GIVEN
    Line line = mock(Line.class);
    LineOutputSession lineOutputSession = mock(LineOutputSession.class);
    when(line.openAsOutput(any(OutputMode.class)))
        .thenReturn(lineOutputSession);
    when(line.openAsOutput())
        .thenCallRealMethod();
    OutputMode defaultOutputMode = OutputMode.builder().build();

    // WHEN
    line.openAsOutput();

    // THEN
    ArgumentCaptor<OutputMode> outputModeCaptor =
        ArgumentCaptor.forClass(OutputMode.class);

    verify(line).openAsOutput(outputModeCaptor.capture());

    assertThat(outputModeCaptor.getValue())
        .isEqualTo(defaultOutputMode);
  }

  @Test
  void openAsOutput_outputModeDriveModeSet() {
    // GIVEN
    Line line = mock(Line.class);
    LineOutputSession lineOutputSession = mock(LineOutputSession.class);
    when(line.openAsOutput(any(OutputMode.class)))
        .thenReturn(lineOutputSession);
    when(line.openAsOutput(any(DriveMode.class)))
        .thenCallRealMethod();

    // WHEN
    line.openAsOutput(DriveMode.PUSH_PULL);

    // THEN
    ArgumentCaptor<OutputMode> outputModeCaptor =
        ArgumentCaptor.forClass(OutputMode.class);

    verify(line).openAsOutput(outputModeCaptor.capture());

    assertThat(outputModeCaptor.getValue().driveMode())
        .isEqualTo(DriveMode.PUSH_PULL);
  }

  @Test
  @SuppressWarnings("NullAway")
  void openAsOutput_throwWhenDriveModeIsNull() {
    // GIVEN
    Line line = mock(Line.class);
    LineOutputSession lineOutputSession = mock(LineOutputSession.class);
    when(line.openAsOutput(any(OutputMode.class)))
        .thenReturn(lineOutputSession);
    when(line.openAsOutput(nullable(DriveMode.class)))
        .thenCallRealMethod();

    // WHEN-THEN
    assertThatThrownBy(() -> line.openAsOutput((DriveMode) null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("driveMode");
  }
}
