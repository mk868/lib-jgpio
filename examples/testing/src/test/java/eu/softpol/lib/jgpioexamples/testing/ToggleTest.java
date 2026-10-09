package eu.softpol.lib.jgpioexamples.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.mock.InputLevel;
import eu.softpol.lib.jgpio.mock.JgpioMock;
import eu.softpol.lib.jgpio.mock.LineProbe;
import eu.softpol.lib.jgpio.mock.MockLineRequest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ToggleTest {

  private Jgpio jgpio;
  private LineProbe led;
  private LineProbe button;

  @BeforeEach
  void setUp() {
    // simulated hardware: a Raspberry Pi 5 like chip with an LED on GPIO14 and a button on GPIO18
    var mock = JgpioMock.builder()
        .chip("gpiochip0", c -> c
            .label("pinctrl-rp1")
            .line(14, "GPIO14")
            .line(18, "GPIO18"))
        .build();
    jgpio = mock.jgpio();
    led = mock.chip("gpiochip0").line("GPIO14");
    button = mock.chip("gpiochip0").line("GPIO18");
  }

  @Test
  void opensButtonWithPullUp() {
    try (var _ = new Toggle(jgpio)) {
      assertThat(button.bias()).isEqualTo(Bias.PULL_UP);
    }
  }

  @Test
  void buttonReleased_ledOff() {
    // GIVEN
    // a released button leaves the line floating, the pull-up makes it read high
    button.setInputLevel(InputLevel.HIGH_IMPEDANCE);

    try (var toggle = new Toggle(jgpio)) {
      // WHEN
      toggle.update();

      // THEN
      assertThat(led.outputValue()).isFalse();
    }
  }

  @Test
  void buttonPressed_ledOn() {
    // GIVEN
    // a pressed button connects the line to GND
    button.setInputLevel(InputLevel.LOW);

    try (var toggle = new Toggle(jgpio)) {
      // WHEN
      toggle.update();

      // THEN
      assertThat(led.outputValue()).isTrue();
    }
  }

  @Test
  void ledFollowsButtonPresses() {
    // GIVEN
    // each read takes the next level: pressed, pressed, released, pressed
    var levels = new ArrayDeque<>(List.of(
        InputLevel.LOW, InputLevel.LOW, InputLevel.HIGH_IMPEDANCE, InputLevel.LOW));
    button.setReadCallback(levels::remove);

    try (var toggle = new Toggle(jgpio)) {
      List<Boolean> writes = new ArrayList<>();
      led.setWriteCallback(writes::add);

      // WHEN
      for (var i = 0; i < 4; i++) {
        toggle.update();
      }

      // THEN
      assertThat(writes).containsExactly(true, true, false, true);
    }
  }

  @Test
  void close_releasesLines() {
    // GIVEN
    var toggle = new Toggle(jgpio);

    // WHEN
    toggle.close();

    // THEN
    assertThat(led.isRequested()).isFalse();
    assertThat(button.isRequested()).isFalse();
  }

  @Test
  void buttonUsedByAnotherApplication_throwsAndReleasesLed() {
    // GIVEN
    button.setExternallyRequested(MockLineRequest.asInput("other-app"));

    // WHEN-THEN
    assertThatThrownBy(() -> new Toggle(jgpio))
        .isInstanceOf(JgpioException.class)
        .hasMessageContaining("'other-app'");
    assertThat(led.isRequested()).isFalse();
  }

}
