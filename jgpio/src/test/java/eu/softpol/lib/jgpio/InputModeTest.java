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

class InputModeTest {

  @Test
  void builder_buildEmptyMode() {
    // WHEN
    InputMode mode = InputMode.builder()
        .build();

    // THEN
    assertThat(mode.consumer())
        .isNull();
    assertThat(mode.bias())
        .isNull();
  }

  @Test
  void builder_setConsumer() {
    // GIVEN
    String consumer = "test-consumer";

    // WHEN
    InputMode mode = InputMode.builder()
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
    InputMode mode = InputMode.builder()
        .consumer(consumer)
        .clearConsumer()
        .build();

    // THEN
    assertThat(mode.consumer())
        .isNull();
  }

  @Test
  void builder_setBias() {
    // GIVEN
    Bias bias = Bias.PULL_UP;

    // WHEN
    InputMode mode = InputMode.builder()
        .bias(bias)
        .build();

    // THEN
    assertThat(mode.bias())
        .isEqualTo(bias);
  }

  @Test
  void builder_clearBias() {
    // GIVEN
    Bias bias = Bias.PULL_UP;

    // WHEN
    InputMode mode = InputMode.builder()
        .bias(bias)
        .clearBias()
        .build();

    // THEN
    assertThat(mode.bias())
        .isNull();
  }

  @Test
  void builder_setAllProperties() {
    // GIVEN
    String consumer = "test-consumer";
    Bias bias = Bias.PULL_DOWN;

    // WHEN
    InputMode mode = InputMode.builder()
        .consumer(consumer)
        .bias(bias)
        .build();

    // THEN
    assertThat(mode.consumer())
        .isEqualTo(consumer);
    assertThat(mode.bias())
        .isEqualTo(bias);
  }

  @Test
  void toBuilder_copyProperties() {
    // GIVEN
    InputMode mode = InputMode.builder()
        .consumer("test-consumer")
        .bias(Bias.PULL_UP)
        .build();

    // WHEN
    InputMode copiedMode = mode.toBuilder()
        .build();

    // THEN
    assertThat(copiedMode.consumer())
        .isEqualTo(mode.consumer());
    assertThat(copiedMode.bias())
        .isEqualTo(mode.bias());
  }

  @Test
  void toBuilder_changeProperties() {
    // GIVEN
    InputMode originalMode = InputMode.builder()
        .consumer("original-consumer")
        .bias(Bias.PULL_UP)
        .build();

    // WHEN
    InputMode modifiedMode = originalMode.toBuilder()
        .consumer("modified-consumer")
        .bias(Bias.PULL_DOWN)
        .build();

    // THEN
    assertThat(originalMode.consumer())
        .isEqualTo("original-consumer");
    assertThat(originalMode.bias())
        .isEqualTo(Bias.PULL_UP);

    assertThat(modifiedMode.consumer())
        .isEqualTo("modified-consumer");
    assertThat(modifiedMode.bias())
        .isEqualTo(Bias.PULL_DOWN);
  }

  @Test
  void toBuilder_clearProperties() {
    // GIVEN
    InputMode originalMode = InputMode.builder()
        .consumer("test-consumer")
        .bias(Bias.PULL_UP)
        .build();

    // WHEN
    InputMode modifiedMode = originalMode.toBuilder()
        .clearConsumer()
        .clearBias()
        .build();

    // THEN
    assertThat(originalMode.consumer())
        .isEqualTo("test-consumer");
    assertThat(originalMode.bias())
        .isEqualTo(Bias.PULL_UP);

    assertThat(modifiedMode.consumer())
        .isNull();
    assertThat(modifiedMode.bias())
        .isNull();
  }

  @Test
  @SuppressWarnings("NullAway")
  void builder_throwWhenConsumerIsNull() {
    // GIVEN
    InputMode.Builder builder = InputMode.builder();

    // WHEN-THEN
    assertThatThrownBy(() -> builder.consumer(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("consumer");
  }

  @Test
  @SuppressWarnings("NullAway")
  void builder_throwWhenBiasIsNull() {
    // GIVEN
    InputMode.Builder builder = InputMode.builder();

    // WHEN-THEN
    assertThatThrownBy(() -> builder.bias(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("bias");
  }
}
