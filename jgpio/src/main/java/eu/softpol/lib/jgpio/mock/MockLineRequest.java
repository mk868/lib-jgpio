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

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;

import eu.softpol.lib.jgpio.mock.MockLineRequest.AsInput;
import eu.softpol.lib.jgpio.mock.MockLineRequest.AsOutput;

public sealed interface MockLineRequest permits AsInput, AsOutput {

  String consumer();

  record AsInput(String consumer) implements MockLineRequest {

    public AsInput {
      checkNonNull(consumer, "consumer");
    }
  }

  record AsOutput(String consumer) implements MockLineRequest {

    public AsOutput {
      checkNonNull(consumer, "consumer");
    }
  }

  static MockLineRequest asInput(String consumer) {
    return new AsInput(consumer);
  }

  static MockLineRequest asOutput(String consumer) {
    return new AsOutput(consumer);
  }
}
