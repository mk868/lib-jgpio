/*
 * Copyright 2024 SOFT-POL
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
package eu.softpol.lib.jgpio.internal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArgCheckTest {

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 5, 10})
  void checkNonNegative_passWhenNonNegative(int testValue) {
    // GIVEN
    var argName = "testValue";

    // WHEN-THEN
    assertThatCode(() -> ArgCheck.checkNonNegative(testValue, argName))
        .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -2, -5, -10})
  void checkNonNegative_throwWhenNegative(int value) {
    // GIVEN
    var argName = "testValue";

    // WHEN-THEN
    assertThatThrownBy(() -> ArgCheck.checkNonNegative(value, argName))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(argName);
  }

  @Test
  void checkNonNull_throwWhenNull() {
    // GIVEN
    Object value = null;
    var argName = "testValue";

    // WHEN-THEN
    assertThatThrownBy(() -> ArgCheck.checkNonNull(value, argName))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(argName);
  }

  @Test
  void checkNonNull_passWhenNonNull() {
    // GIVEN
    Object value = 123;
    var argName = "testValue";

    // WHEN-THEN
    assertThatCode(() -> ArgCheck.checkNonNull(value, argName))
        .doesNotThrowAnyException();
  }
}
