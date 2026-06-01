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

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.InputMode;
import org.jspecify.annotations.Nullable;

public record InputModeImpl(
    @Nullable String consumer,
    @Nullable Bias bias
) implements InputMode {

  @Override
  public Builder toBuilder() {
    return new BuilderImpl(consumer, bias);
  }

  public static class BuilderImpl implements Builder {

    private @Nullable String consumer;
    private @Nullable Bias bias;

    public BuilderImpl(
        @Nullable String consumer,
        @Nullable Bias bias
    ) {
      this.consumer = consumer;
      this.bias = bias;
    }

    public BuilderImpl() {
    }

    @Override
    public Builder consumer(String consumer) {
      checkNonNull(consumer, "consumer");
      this.consumer = consumer;
      return this;
    }

    @Override
    public Builder clearConsumer() {
      this.consumer = null;
      return this;
    }

    @Override
    public Builder bias(Bias bias) {
      checkNonNull(bias, "bias");
      this.bias = bias;
      return this;
    }

    @Override
    public Builder clearBias() {
      this.bias = null;
      return this;
    }

    @Override
    public InputMode build() {
      return new InputModeImpl(consumer, bias);
    }
  }
}
