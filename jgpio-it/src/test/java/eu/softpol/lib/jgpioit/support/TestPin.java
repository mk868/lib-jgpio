package eu.softpol.lib.jgpioit.support;

import org.jspecify.annotations.Nullable;

public record TestPin(
    TestChip chip,
    int lineOffset,
    @Nullable String lineName
) {

  public String chipName() {
    return chip.name();
  }

}
