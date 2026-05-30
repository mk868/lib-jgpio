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
package eu.softpol.lib.jgpio.internal.gpiod2;

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
import eu.softpol.lib.jgpio.internal.ffm.libgpiod2.gpiod_h;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public class Gpiod2Jgpio implements Jgpio {

  public Gpiod2Jgpio() {
    tryToInitLibrary(gpiod_h.class, "gpiod");
  }

  @Override
  public Chip openChipByName(String name) {
    checkNonNull(name, "name");
    var path = Path.of("/dev/" + name);
    return Gpiod2Chip.openByPath(path);
  }

  @Override
  public Chip openChipByLabel(String label) {
    checkNonNull(label, "label");
    return getChips().stream()
        .filter(c -> c.label().equals(label))
        .findFirst()
        .orElseThrow(() -> new JgpioException(
            "Cannot open chip with label '%s', no resource or lack of permissions"
                .formatted(label)))
        .open();
  }

  @Override
  public Chip openChipByPath(Path path) {
    checkNonNull(path, "path");
    return Gpiod2Chip.openByPath(path);
  }

  @Override
  public Chip openChipByNumber(int number) {
    checkNonNegative(number, "number");
    var path = Path.of("/dev/gpiochip" + number);
    return Gpiod2Chip.openByPath(path);
  }

  @Override
  public List<ChipInfo> getChips() {
    Path dev = Path.of("/dev");

    final List<Path> gpiochipList;
    try (Stream<Path> entries = Files.list(dev)) {
      gpiochipList = entries
          .map(Path::toAbsolutePath)
          .filter(path -> path.getFileName().toString().startsWith("gpiochip"))
          .sorted()
          .toList();
    } catch (IOException e) {
      throw new JgpioException("Cannot iterate over chips.", e);
    }

    var result = new ArrayList<ChipInfo>();

    for (var path : gpiochipList) {
      try (var arena = Arena.ofConfined()) {
        var pathPtr = arena.allocateFrom(path.toString(), StandardCharsets.US_ASCII);
        if (!gpiod_h.gpiod_is_gpiochip_device(pathPtr)) {
          continue;
        }
        var chipPtr = gpiod_h.gpiod_chip_open(pathPtr);
        if (isNull(chipPtr)) {
          continue;
        }
        try {
          var chipInfoPtr = gpiod_h.gpiod_chip_get_info(chipPtr);
          if (isNull(chipInfoPtr)) {
            continue;
          }
          result.add(new Gpiod2ChipInfo(
              toNonNullString(gpiod_h.gpiod_chip_info_get_name(chipInfoPtr)),
              toNonNullString(gpiod_h.gpiod_chip_info_get_label(chipInfoPtr)),
              (int) gpiod_h.gpiod_chip_info_get_num_lines(chipInfoPtr),
              path
          ));
          gpiod_h.gpiod_chip_info_free(chipInfoPtr);
        } finally {
          gpiod_h.gpiod_chip_close(chipPtr);
        }
      }
    }

    return List.copyOf(result);
  }

  @Override
  public @Nullable String version() {
    return toNullableString(gpiod_h.gpiod_api_version());
  }
}
