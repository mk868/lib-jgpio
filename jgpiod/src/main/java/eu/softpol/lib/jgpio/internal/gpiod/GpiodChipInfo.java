package eu.softpol.lib.jgpio.internal.gpiod;

import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.ChipInfo;

public record GpiodChipInfo(
    String name,
    String label,
    int countLines

) implements ChipInfo {

  @Override
  public Chip open() {
    return GpiodChip.openByName(name);
  }
}
