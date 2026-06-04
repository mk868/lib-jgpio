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

import org.junit.jupiter.api.Test;


class OutputModeTest {

  @Test
  void builder_buildEmptyMode() {
    // WHEN
    OutputMode mode = OutputMode.builder()
        .build();

    // THEN
    assertThat(mode.consumer())
        .isNull();
    assertThat(mode.driveMode())
        .isNull();
    assertThat(mode.initialValue())
        .isNull();
  }

  @Test
  void builder_setConsumer() {
    // GIVEN
    String consumer = "test-consumer";

    // WHEN
    OutputMode mode = OutputMode.builder()
        .consumer(consumer)
        .build();

    // THEN
    assertThat(mode.consumer())
        .isEqualTo(consumer);
  }

  @Test
  void builder_clearConsumer() {
    // GIVEN
    String consumer = "test-consumer";

    // WHEN
    OutputMode mode = OutputMode.builder()
        .consumer(consumer)
        .clearConsumer()
        .build();

    // THEN
    assertThat(mode.consumer())
        .isNull();
  }

  @Test
  void builder_setDriveMode() {
    // GIVEN
    DriveMode driveMode = DriveMode.OPEN_DRAIN;

    // WHEN
    OutputMode mode = OutputMode.builder()
        .driveMode(driveMode)
        .build();

    // THEN
    assertThat(mode.driveMode())
        .isEqualTo(driveMode);
  }

  @Test
  void builder_clearDriveMode() {
    // GIVEN
    DriveMode driveMode = DriveMode.OPEN_DRAIN;

    // WHEN
    OutputMode mode = OutputMode.builder()
        .driveMode(driveMode)
        .clearDriveMode()
        .build();

    // THEN
    assertThat(mode.driveMode())
        .isNull();
  }

  @Test
  void builder_setInitialValueToTrue() {
    // WHEN
    OutputMode mode = OutputMode.builder()
        .initialValue(true)
        .build();

    // THEN
    assertThat(mode.initialValue())
        .isTrue();
  }

  @Test
  void builder_setInitialValueToFalse() {
    // WHEN
    OutputMode mode = OutputMode.builder()
        .initialValue(false)
        .build();

    // THEN
    assertThat(mode.initialValue())
        .isFalse();
  }

  @Test
  void builder_clearInitialValue() {
    // WHEN
    OutputMode mode = OutputMode.builder()
        .initialValue(true)
        .clearInitialValue()
        .build();

    // THEN
    assertThat(mode.initialValue())
        .isNull();
  }

  @Test
  void builder_setAllProperties() {
    // GIVEN
    String consumer = "test-consumer";
    DriveMode driveMode = DriveMode.OPEN_SOURCE;
    boolean initialValue = true;

    // WHEN
    OutputMode mode = OutputMode.builder()
        .consumer(consumer)
        .driveMode(driveMode)
        .initialValue(initialValue)
        .build();

    // THEN
    assertThat(mode.consumer())
        .isEqualTo(consumer);
    assertThat(mode.driveMode())
        .isEqualTo(driveMode);
    assertThat(mode.initialValue())
        .isEqualTo(initialValue);
  }

  @Test
  void toBuilder_copyProperties() {
    // GIVEN
    OutputMode mode = OutputMode.builder()
        .consumer("test-consumer")
        .driveMode(DriveMode.OPEN_DRAIN)
        .initialValue(true)
        .build();

    // WHEN
    OutputMode copiedMode = mode.toBuilder()
        .build();

    // THEN
    assertThat(copiedMode.consumer())
        .isEqualTo(mode.consumer());
    assertThat(copiedMode.driveMode())
        .isEqualTo(mode.driveMode());
    assertThat(copiedMode.initialValue())
        .isEqualTo(mode.initialValue());
  }

  @Test
  void toBuilder_changeProperties() {
    // GIVEN
    OutputMode originalMode = OutputMode.builder()
        .consumer("original-consumer")
        .driveMode(DriveMode.OPEN_DRAIN)
        .initialValue(true)
        .build();

    // WHEN
    OutputMode modifiedMode = originalMode.toBuilder()
        .consumer("modified-consumer")
        .driveMode(DriveMode.OPEN_SOURCE)
        .initialValue(false)
        .build();

    // THEN
    assertThat(originalMode.consumer())
        .isEqualTo("original-consumer");
    assertThat(originalMode.driveMode())
        .isEqualTo(DriveMode.OPEN_DRAIN);
    assertThat(originalMode.initialValue())
        .isTrue();

    assertThat(modifiedMode.consumer())
        .isEqualTo("modified-consumer");
    assertThat(modifiedMode.driveMode())
        .isEqualTo(DriveMode.OPEN_SOURCE);
    assertThat(modifiedMode.initialValue())
        .isFalse();
  }

  @Test
  void toBuilder_clearProperties() {
    // GIVEN
    OutputMode originalMode = OutputMode.builder()
        .consumer("test-consumer")
        .driveMode(DriveMode.OPEN_DRAIN)
        .initialValue(true)
        .build();

    // WHEN
    OutputMode modifiedMode = originalMode.toBuilder()
        .clearConsumer()
        .clearDriveMode()
        .clearInitialValue()
        .build();

    // THEN
    assertThat(originalMode.consumer())
        .isEqualTo("test-consumer");
    assertThat(originalMode.driveMode())
        .isEqualTo(DriveMode.OPEN_DRAIN);
    assertThat(originalMode.initialValue())
        .isTrue();

    assertThat(modifiedMode.consumer())
        .isNull();
    assertThat(modifiedMode.driveMode())
        .isNull();
    assertThat(modifiedMode.initialValue())
        .isNull();
  }

  @Test
  @SuppressWarnings("NullAway")
  void builder_throwWhenConsumerIsNull() {
    // GIVEN
    OutputMode.Builder builder = OutputMode.builder();

    // WHEN-THEN
    assertThatThrownBy(() -> builder.consumer(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("consumer");
  }

  @Test
  @SuppressWarnings("NullAway")
  void builder_throwWhenDriveModeIsNull() {
    // GIVEN
    OutputMode.Builder builder = OutputMode.builder();

    // WHEN-THEN
    assertThatThrownBy(() -> builder.driveMode(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("driveMode");
  }
}
