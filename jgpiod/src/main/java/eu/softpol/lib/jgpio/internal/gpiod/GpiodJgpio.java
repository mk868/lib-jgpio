package eu.softpol.lib.jgpio.internal.gpiod;

import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNegative;
import static eu.softpol.lib.jgpio.internal.ArgCheck.checkNonNull;
import static eu.softpol.lib.jgpio.internal.FFMUtil.isNull;
import static eu.softpol.lib.jgpio.internal.FFMUtil.toNonNullString;
import static eu.softpol.lib.jgpio.internal.FFMUtil.toNullableString;
import static eu.softpol.lib.jgpio.internal.LibDiscovery.tryToInitLibrary;

import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.ChipInfo;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.internal.ffm.libgpiod.gpiod_h;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class GpiodJgpio implements Jgpio {

  public GpiodJgpio() {
    tryToInitLibrary(gpiod_h.class, "gpiod");
  }

  @Override
  public Chip openChipByName(String name) {
    checkNonNull(name, "name");
    return GpiodChip.openByName(name);
  }

  @Override
  public Chip openChipByLabel(String label) {
    checkNonNull(label, "label");
    return GpiodChip.openByLabel(label);
  }

  @Override
  public Chip openChipByPath(Path path) {
    checkNonNull(path, "path");
    return GpiodChip.openByPath(path);
  }

  @Override
  public Chip openChipByNumber(int number) {
    checkNonNegative(number, "number");
    return GpiodChip.openByNumber(number);
  }

  @Override
  public List<ChipInfo> getChips() {
    var iter = gpiod_h.gpiod_chip_iter_new();
    if (isNull(iter)) {
      throw new JgpioException("Cannot iterate over chips.");
    }

    var result = new ArrayList<ChipInfo>();
    for (
        var chip = gpiod_h.gpiod_chip_iter_next(iter);
        !isNull(chip);
        chip = gpiod_h.gpiod_chip_iter_next(iter)
    ) {
      result.add(new GpiodChipInfo(
          toNonNullString(gpiod_h.gpiod_chip_name(chip)),
          toNonNullString(gpiod_h.gpiod_chip_label(chip)),
          gpiod_h.gpiod_chip_num_lines(chip)
      ));
    }
    gpiod_h.gpiod_chip_iter_free(iter);
    return List.copyOf(result);
  }

  @Override
  public @Nullable String version() {
    return toNullableString(gpiod_h.gpiod_version_string());
  }
}
