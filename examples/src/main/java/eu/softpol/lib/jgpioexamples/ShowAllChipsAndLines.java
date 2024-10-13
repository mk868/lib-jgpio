package eu.softpol.lib.jgpioexamples;

import eu.softpol.lib.jgpio.Jgpio;

public class ShowAllChipsAndLines {

  public static void main(final String[] args) {
    var chips = Jgpio.getInstance()
        .getChips();
    for (var chipInfo : chips) {
      try (var chip = chipInfo.open()) {
        System.out.println();
        System.out.println("-".repeat(80));
        System.out.printf("Chip name: %s%n", chip.name());
        System.out.printf("Chip label: %s%n", chip.label());
        System.out.printf("Chip lines(%d):%n", chip.countLines());
        System.out.println(
            "offset                           name                          in use  direction"
        );
        for (var i = 0; i < chip.countLines(); i++) {
          var line = chip.getLine(i);
          var lineName = line.name();
          if (lineName != null) {
            lineName = '"' + lineName + '"';
          }
          var used = "no";
          if (line.isUsed()) {
            var consumer = line.consumer();
            used = "used by " + (consumer == null ? "null" : '"' + line.consumer() + '"');
          }
          System.out.printf(
              "%6d %30s  %30s %10s%n",
              line.offset(),
              lineName,
              used,
              line.direction().name().toLowerCase()
          );
        }
      }
    }
  }

}
