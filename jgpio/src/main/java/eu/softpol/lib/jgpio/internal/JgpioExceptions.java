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
package eu.softpol.lib.jgpio.internal;

import eu.softpol.lib.jgpio.JgpioException;
import java.nio.file.Path;

public class JgpioExceptions {

  private JgpioExceptions() {
  }

  public static JgpioException chipOpenFailedByName(String name) {
    return chipOpenFailed("name", name);
  }

  public static JgpioException chipOpenFailedByNumber(int number) {
    return chipOpenFailed("number", number);
  }

  public static JgpioException chipOpenFailedByLabel(String label) {
    return chipOpenFailed("label", label);
  }

  public static JgpioException chipOpenFailedByPath(Path path) {
    return chipOpenFailed("path", path);
  }

  private static JgpioException chipOpenFailed(String selector, Object value) {
    return new JgpioException(
        "Cannot open GPIO chip by %s '%s': device not found or permission denied"
            .formatted(selector, value));
  }

  public static IllegalStateException lineSessionClosed() {
    return new IllegalStateException("Line Session has been closed");
  }

  public static IllegalStateException chipClosed() {
    return new IllegalStateException("Chip for this line has been closed");
  }
}
