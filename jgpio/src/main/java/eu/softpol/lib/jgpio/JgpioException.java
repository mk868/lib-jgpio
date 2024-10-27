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
package eu.softpol.lib.jgpio;

/// {@link JgpioException} represents errors specific to the JGPIO library operations.
public class JgpioException extends RuntimeException {

  /// Constructs a new {@link JgpioException} with the specified detail message.
  ///
  /// @param message the detail message to describe the error
  public JgpioException(String message) {
    super(message);
  }

  /// Constructs a new {@link JgpioException} with the specified detail message and cause.
  ///
  /// @param message the detail message to describe the error
  /// @param cause   the cause of the exception
  public JgpioException(String message, Throwable cause) {
    super(message, cause);
  }
}
