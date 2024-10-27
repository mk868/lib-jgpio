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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class FFMUtilTest {

  @Test
  void toNonNullString_throwsWhenNull() {
    // GIVEN
    var stringPtr = MemorySegment.NULL;

    // WHEN-THEN
    assertThatThrownBy(() -> FFMUtil.toNonNullString(stringPtr))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void toNonNullString_returnsNonNullString() {
    // GIVEN
    var arena = Arena.ofAuto();
    var stringPtr = arena.allocateFrom("string", StandardCharsets.US_ASCII);

    // WHEN
    var result = FFMUtil.toNonNullString(stringPtr);

    // THEN
    assertThat(result)
        .isEqualTo("string");
  }

  @Test
  void toNullableString_returnsNull() {
    // GIVEN
    var stringPtr = MemorySegment.NULL;

    // WHEN
    var result = FFMUtil.toNullableString(stringPtr);

    // THEN
    assertThat(result)
        .isNull();
  }

  @Test
  void toNullableString_returnsString() {
    // GIVEN
    var arena = Arena.ofAuto();
    var stringPtr = arena.allocateFrom("string", StandardCharsets.US_ASCII);

    // WHEN
    var result = FFMUtil.toNullableString(stringPtr);

    // THEN
    assertThat(result)
        .isEqualTo("string");
  }

  @Test
  void isNull_returnsTrueForNullAddress() {
    // GIVEN
    var ptr = MemorySegment.NULL;

    // WHEN
    var result = FFMUtil.isNull(ptr);

    // THEN
    assertThat(result)
        .isTrue();
  }

  @Test
  void isNull_returnsFalseForNonNullAddress() {
    // GIVEN
    var arena = Arena.ofAuto();
    var ptr = arena.allocateFrom("string", StandardCharsets.US_ASCII);

    // WHEN
    var result = FFMUtil.isNull(ptr);

    // THEN
    assertThat(result)
        .isFalse();
  }
}
