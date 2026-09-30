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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class MockLineRequestTest {

  // the factories call the record constructors, which do the check

  @Test
  @SuppressWarnings("NullAway")
  void asInput_throwWhenConsumerIsNull() {
    // WHEN-THEN
    assertThatThrownBy(() -> MockLineRequest.asInput(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("consumer");
  }

  @Test
  @SuppressWarnings("NullAway")
  void asOutput_throwWhenConsumerIsNull() {
    // WHEN-THEN
    assertThatThrownBy(() -> MockLineRequest.asOutput(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("consumer");
  }
}
