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

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.mock.InputLevel;
import eu.softpol.lib.jgpio.mock.LineProbe;
import eu.softpol.lib.jgpio.mock.MockLineRequest;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

/// State of a mocked line, shared by all [MockLine]s created for it.
public class MockLineProbe implements LineProbe {

  private static final Logger logger = System.getLogger(MockLineProbe.class.getName());

  private final int offset;
  private final @Nullable String name;

  private InputLevel inputLevel = InputLevel.HIGH_IMPEDANCE;
  private boolean outputValue;
  private Bias bias = Bias.HIGH_IMPEDANCE;
  private DriveMode driveMode = DriveMode.PUSH_PULL;
  private @Nullable MockLineRequest externallyRequested;
  private @Nullable MockLineSession session;
  private @Nullable Consumer<Boolean> writeCallback;
  private @Nullable Supplier<InputLevel> readCallback;

  public MockLineProbe(int offset, @Nullable String name) {
    this.offset = offset;
    this.name = name;
  }

  @Override
  public int offset() {
    return offset;
  }

  @Override
  public @Nullable String name() {
    return name;
  }

  @Override
  public void setInputLevel(InputLevel level) {
    checkNonNull(level, "level");
    this.inputLevel = level;
  }

  @Override
  public boolean outputValue() {
    return outputValue;
  }

  @Override
  public Optional<MockLineRequest> request() {
    if (session != null) {
      return Optional.of(session.request());
    }
    return Optional.ofNullable(externallyRequested);
  }

  @Override
  public Bias bias() {
    return bias;
  }

  @Override
  public DriveMode driveMode() {
    return driveMode;
  }

  @Override
  public void setExternallyRequested(MockLineRequest request) {
    checkNonNull(request, "request");
    if (session != null) {
      logger.log(Level.WARNING, "Setting externally requested on a line with active session");
    }
    externallyRequested = request;
  }

  @Override
  public void clearExternallyRequested() {
    externallyRequested = null;
  }

  @Override
  public void setWriteCallback(Consumer<Boolean> callback) {
    checkNonNull(callback, "callback");
    writeCallback = callback;
  }

  @Override
  public void clearWriteCallback() {
    writeCallback = null;
  }

  @Override
  public void setReadCallback(Supplier<InputLevel> callback) {
    checkNonNull(callback, "callback");
    readCallback = callback;
  }

  @Override
  public void clearReadCallback() {
    readCallback = null;
  }

  /// Returns the level seen by an input session, taken from the read callback when set.
  protected InputLevel readInputLevel() {
    if (readCallback == null) {
      return inputLevel;
    }
    var level = readCallback.get();
    if (level == null) {
      throw new IllegalStateException("Read callback returned null input level");
    }
    return level;
  }

  /// Applies a value written by an output session, then notifies the write callback.
  protected void writeOutputValue(boolean outputValue) {
    this.outputValue = outputValue;
    if (writeCallback != null) {
      writeCallback.accept(outputValue);
    }
  }

  protected void setBias(Bias bias) {
    this.bias = bias;
  }

  protected void setDriveMode(DriveMode driveMode) {
    this.driveMode = driveMode;
  }

  /// Requests the line for the session, failing like the kernel does (`EBUSY`) when the line is
  /// already requested by another session or externally.
  protected void acquire(MockLineSession session) {
    var current = request();
    if (current.isPresent()) {
      throw new JgpioException(
          "JGPIO line request failed: line %d is already in use by '%s'"
              .formatted(offset, current.get().consumer()));
    }
    this.session = session;
  }

  protected void release(MockLineSession session) {
    if (this.session == session) {
      this.session = null;
    }
  }
}
