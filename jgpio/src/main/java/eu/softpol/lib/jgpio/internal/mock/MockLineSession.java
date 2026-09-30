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
package eu.softpol.lib.jgpio.internal.mock;

import eu.softpol.lib.jgpio.internal.JgpioDefaults;
import eu.softpol.lib.jgpio.internal.JgpioExceptions;
import eu.softpol.lib.jgpio.mock.MockLineRequest;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public abstract class MockLineSession {

  protected final MockChip chip;
  protected final MockLineProbe line;
  private final String consumer;

  private boolean closed;

  protected MockLineSession(
      MockChip chip,
      MockLineProbe line,
      @Nullable String consumer
  ) {
    this.chip = chip;
    this.line = line;
    this.consumer = Objects.requireNonNullElse(consumer, JgpioDefaults.CONSUMER_NAME);
    line.acquire(this);
    chip.registerSession(this);
  }

  /// Describes this session as reported by [MockLineProbe#request()].
  protected abstract MockLineRequest request();

  protected boolean isClosed() {
    return closed || chip.isClosed();
  }

  protected void close() {
    if (closed) {
      return;
    }
    closed = true;
    line.release(this);
    chip.unregisterSession(this);
  }

  protected void throwWhenLineSessionClosed() {
    if (closed) {
      throw JgpioExceptions.lineSessionClosed();
    }
  }

  protected void throwWhenChipClosed() {
    if (chip.isClosed()) {
      throw JgpioExceptions.chipForLineClosed();
    }
  }

  protected String consumer() {
    return consumer;
  }
}
