package eu.softpol.lib.jgpioexamples.web;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.mock.ChipProbe;
import eu.softpol.lib.jgpio.mock.InputLevel;
import eu.softpol.lib.jgpio.mock.JgpioMock;
import eu.softpol.lib.jgpio.mock.MockLineRequest;

/// A simulated Raspberry Pi 5, to try the web page without a board, e.g. on a PC.
///
/// Besides the lines used by the system, it simulates:
///
/// * a wire between `GPIO14` and `GPIO15`, `GPIO15` reads the level driven on `GPIO14`,
/// * a button between `GND` and `GPIO18`, pressed every other second; request `GPIO18` as input
///   with the pull-up bias to see it.
final class MockBoard {

  /// Names of the `pinctrl-rp1` lines from offset 28, the lines before are `ID_SDA`, `ID_SCL` and
  /// `GPIO2`..`GPIO27`; `null` for unnamed lines
  private static final String[] RP1_LINES = {
      "PCIE_RP1_WAKE", "FAN_TACH", "HOST_SDA", "HOST_SCL", "ETH_RST_N", null,
      "CD0_IO0_MICCLK", "CD0_IO0_MICDAT0", "RP1_PCIE_CLKREQ_N", null, "CD0_SDA", "CD0_SCL",
      "CD1_SDA", "CD1_SCL", "USB_VBUS_EN", "USB_OC_N", "RP1_STAT_LED", "FAN_PWM",
      "CD1_IO0_MICCLK", "2712_WAKE", "CD1_IO1_MICDAT1", "EN_MAX_USB_CUR", null, null, null, null
  };
  private static final String[] BRCMSTB_508500_LINES = {
      null, "2712_BOOT_CS_N", "2712_BOOT_MISO", "2712_BOOT_MOSI", "2712_BOOT_SCLK", null, null,
      null, null, null, null, null, null, null, "PCIE_SDA", "PCIE_SCL", null, null, null, null,
      "PWR_GPIO", "2712_G21_FS", null, null, "BT_RTS", "BT_CTS", "BT_TXD", "BT_RXD", "WL_ON",
      "BT_ON", "WIFI_SDIO_CLK", "WIFI_SDIO_CMD"
  };
  private static final String[] BRCMSTB_508520_LINES = {
      "WIFI_SDIO_D0", "WIFI_SDIO_D1", "WIFI_SDIO_D2", "WIFI_SDIO_D3"
  };
  private static final String[] BRCMSTB_517C00_LINES = {
      "RP1_SDA", "RP1_SCL", "RP1_RUN", "SD_IOVDD_SEL", "SD_PWR_ON", "SD_CDET_N", "SD_FLG_N", null,
      "2712_WAKE", "2712_STAT_LED", null, null, "PMIC_INT", "UART_TX_FS", "UART_RX_FS", null, null
  };
  private static final String[] BRCMSTB_517C20_LINES = {
      "HDMI0_SCL", "HDMI0_SDA", "HDMI1_SCL", "HDMI1_SDA", "PMIC_SCL", "PMIC_SDA"
  };

  private MockBoard() {
  }

  static Jgpio create() {
    var mock = JgpioMock.builder()
        .chip("gpiochip0", chip -> {
          chip.label("pinctrl-rp1")
              .line(0, "ID_SDA")
              .line(1, "ID_SCL");
          for (var offset = 2; offset < 28; offset++) {
            chip.line(offset, "GPIO" + offset);
          }
          lines(chip, 28, RP1_LINES);
        })
        .chip("gpiochip10", chip -> lines(
            chip.label("gpio-brcmstb@107d508500"), 0, BRCMSTB_508500_LINES))
        .chip("gpiochip11", chip -> lines(
            chip.label("gpio-brcmstb@107d508520"), 0, BRCMSTB_508520_LINES))
        .chip("gpiochip12", chip -> lines(
            chip.label("gpio-brcmstb@107d517c00"), 0, BRCMSTB_517C00_LINES))
        .chip("gpiochip13", chip -> lines(
            chip.label("gpio-brcmstb@107d517c20"), 0, BRCMSTB_517C20_LINES))
        .build();

    var rp1 = mock.chip("gpiochip0");
    usedBySystem(rp1, "ETH_RST_N", MockLineRequest.asOutput("phy-reset"));
    usedBySystem(rp1, "CD0_IO0_MICCLK", MockLineRequest.asOutput("cam0_reg"));
    usedBySystem(rp1, "RP1_STAT_LED", MockLineRequest.asOutput("PWR"));
    usedBySystem(rp1, "CD1_IO0_MICCLK", MockLineRequest.asOutput("cam1_reg"));
    var brcmstb508500 = mock.chip("gpiochip10");
    usedBySystem(brcmstb508500, "2712_BOOT_CS_N", MockLineRequest.asOutput("spi10 CS0"));
    usedBySystem(brcmstb508500, "PWR_GPIO", MockLineRequest.asInput("pwr_button"));
    usedBySystem(brcmstb508500, "WL_ON", MockLineRequest.asOutput("wl_on_reg"));
    usedBySystem(brcmstb508500, "BT_ON", MockLineRequest.asOutput("shutdown"));
    var brcmstb517c00 = mock.chip("gpiochip12");
    usedBySystem(brcmstb517c00, "RP1_RUN", MockLineRequest.asOutput("RP1 RUN pin"));
    usedBySystem(brcmstb517c00, "SD_IOVDD_SEL", MockLineRequest.asOutput("vdd-sd-io"));
    usedBySystem(brcmstb517c00, "SD_PWR_ON", MockLineRequest.asOutput("sd_vcc_reg"));
    usedBySystem(brcmstb517c00, "SD_CDET_N", MockLineRequest.asInput("cd"));
    usedBySystem(brcmstb517c00, "2712_STAT_LED", MockLineRequest.asOutput("ACT"));

    var gpio14 = rp1.line("GPIO14");
    rp1.line("GPIO15").setReadCallback(() -> {
      if (!(gpio14.request().orElse(null) instanceof MockLineRequest.AsOutput)) {
        return InputLevel.HIGH_IMPEDANCE;
      }
      return gpio14.outputValue() ? InputLevel.HIGH : InputLevel.LOW;
    });
    rp1.line("GPIO18").setReadCallback(() -> System.currentTimeMillis() / 1000 % 2 == 0
        ? InputLevel.LOW
        : InputLevel.HIGH_IMPEDANCE);

    return mock.jgpio();
  }

  /// Defines lines from the given offset, `null` names define unnamed lines.
  private static void lines(
      final JgpioMock.ChipBuilder chip,
      final int firstOffset,
      final String[] names
  ) {
    for (var i = 0; i < names.length; i++) {
      var name = names[i];
      chip.line(firstOffset + i, line -> {
        if (name != null) {
          line.name(name);
        }
      });
    }
  }

  private static void usedBySystem(
      final ChipProbe chip,
      final String line,
      final MockLineRequest request
  ) {
    chip.line(line).setExternallyRequested(request);
  }

}
