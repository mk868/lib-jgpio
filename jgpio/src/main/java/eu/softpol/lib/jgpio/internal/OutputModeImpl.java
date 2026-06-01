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

import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.OutputMode;
import org.jspecify.annotations.Nullable;

public record OutputModeImpl(
    @Nullable String consumer,
    @Nullable DriveMode driveMode,
    @Nullable Boolean initialValue
) implements OutputMode {

  @Override
  public Builder toBuilder() {
    return new BuilderImpl(consumer, driveMode, initialValue);
  }

  public static class BuilderImpl implements Builder {

    private @Nullable String consumer;
    private @Nullable DriveMode driveMode;
    private @Nullable Boolean initialValue;

    public BuilderImpl(
        @Nullable String consumer,
        @Nullable DriveMode driveMode,
        @Nullable Boolean initialValue
    ) {
      this.consumer = consumer;
      this.driveMode = driveMode;
      this.initialValue = initialValue;
    }

    public BuilderImpl() {
    }

    @Override
    public Builder consumer(String consumer) {
      ArgCheck.checkNonNull(consumer, "consumer");
      this.consumer = consumer;
      return this;
    }

    @Override
    public Builder clearConsumer() {
      this.consumer = null;
      return this;
    }

    @Override
    public Builder driveMode(DriveMode driveMode) {
      ArgCheck.checkNonNull(driveMode, "driveMode");
      this.driveMode = driveMode;
      return this;
    }

    @Override
    public Builder clearDriveMode() {
      this.driveMode = null;
      return this;
    }

    @Override
    public Builder initialValue(boolean initialValue) {
      this.initialValue = initialValue;
      return this;
    }

    @Override
    public Builder clearInitialValue() {
      this.initialValue = null;
      return this;
    }

    @Override
    public OutputMode build() {
      return new OutputModeImpl(consumer, driveMode, initialValue);
    }
  }
}
