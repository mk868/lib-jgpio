package eu.softpol.lib.jgpio.internal;

import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import org.jspecify.annotations.Nullable;

public class FFMUtil {

  private FFMUtil() {
  }

  public static String toNonNullString(MemorySegment memorySegment) {
    return memorySegment.getString(0, StandardCharsets.US_ASCII);
  }

  public static @Nullable String toNullableString(MemorySegment memorySegment) {
    if (MemorySegment.NULL.equals(memorySegment)) {
      return null;
    }
    return toNonNullString(memorySegment);
  }

  public static boolean isNull(MemorySegment memorySegment) {
    return MemorySegment.NULL.equals(memorySegment);
  }
}
