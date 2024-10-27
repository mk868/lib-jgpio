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

import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class FFMUtil {

  private FFMUtil() {
  }

  public static String toNonNullString(MemorySegment memorySegment) {
    var result = toNullableString(memorySegment);
    return Objects.requireNonNull(result, "memorySegment modeling the NULL address");
  }

  public static @Nullable String toNullableString(MemorySegment memorySegment) {
    if (isNull(memorySegment)) {
      return null;
    }
    return memorySegment.getString(0, StandardCharsets.US_ASCII);
  }

  public static boolean isNull(MemorySegment memorySegment) {
    return MemorySegment.NULL.equals(memorySegment);
  }
}
