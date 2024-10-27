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
