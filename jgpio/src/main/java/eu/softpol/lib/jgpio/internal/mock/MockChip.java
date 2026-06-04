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

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNegative;
import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;

import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.Line;
import eu.softpol.lib.jgpio.internal.JgpioExceptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/// Mocked [Chip] API, one instance per opened chip.
public class MockChip implements Chip {

  private final MockChipProbe chip;
  private final List<MockLineSession> activeSessions = new ArrayList<>();

  private boolean closed;

  public MockChip(MockChipProbe chip) {
    this.chip = chip;
  }

  @Override
  public String name() {
    throwWhenChipClosed();
    return chip.name();
  }

  @Override
  public String label() {
    throwWhenChipClosed();
    return chip.label();
  }

  @Override
  public int countLines() {
    throwWhenChipClosed();
    return chip.countLines();
  }

  @Override
  public Optional<Line> findLine(int offset) {
    checkNonNegative(offset, "offset");
    throwWhenChipClosed();
    return chip.findLine(offset)
        .map(line -> new MockLine(this, line));
  }

  @Override
  public Optional<Line> findLine(String name) {
    checkNonNull(name, "name");
    throwWhenChipClosed();
    return chip.findLine(name)
        .map(line -> new MockLine(this, line));
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public void close() {
    if (closed) {
      return;
    }
    closed = true;
    var sessionsToClose = new ArrayList<>(activeSessions);
    activeSessions.clear();
    sessionsToClose.forEach(MockLineSession::close);
  }

  protected void registerSession(MockLineSession session) {
    activeSessions.add(session);
  }

  protected void unregisterSession(MockLineSession session) {
    activeSessions.remove(session);
  }

  private void throwWhenChipClosed() {
    if (closed) {
      throw JgpioExceptions.chipClosed();
    }
  }
}
