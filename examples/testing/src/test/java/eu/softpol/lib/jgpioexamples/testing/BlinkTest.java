package eu.softpol.lib.jgpioexamples.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.mock.JgpioMock;
import eu.softpol.lib.jgpio.mock.LineProbe;
import eu.softpol.lib.jgpio.mock.MockLineRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlinkTest {

  private LineProbe led;
  private Blink blink;

  @BeforeEach
  void setUp() {
    // simulated hardware: a Raspberry Pi 5 like chip with an LED on GPIO14
    var mock = JgpioMock.builder()
        .chip("gpiochip0", c -> c
            .label("pinctrl-rp1")
            .line(14, "GPIO14"))
        .build();
    led = mock.chip("gpiochip0").line("GPIO14");
    blink = new Blink(mock.jgpio(), Duration.ZERO);
  }

  @Test
  void writesEachBlink() throws InterruptedException {
    // GIVEN
    List<Boolean> writes = new ArrayList<>();
    led.setWriteCallback(writes::add);

    // WHEN
    blink.blink(3);

    // THEN
    // the first `false` is the initial value set when the line is opened as output
    assertThat(writes).containsExactly(false, true, false, true, false, true, false);
  }

  @Test
  void leavesLedOffAndReleasesLine() throws InterruptedException {
    // WHEN
    blink.blink(3);

    // THEN
    // probes are still usable after Blink closed the chip
    assertThat(led.outputValue()).isFalse();
    assertThat(led.isRequested()).isFalse();
  }

  @Test
  void lineUsedByAnotherApplication_throws() {
    // GIVEN
    led.setExternallyRequested(MockLineRequest.asOutput("other-app"));

    // WHEN-THEN
    assertThatThrownBy(() -> blink.blink(3))
        .isInstanceOf(JgpioException.class)
        .hasMessageContaining("'other-app'");
  }

  @Test
  void writeFails_releasesLine() {
    // GIVEN
    led.setWriteCallback(value -> {
      if (value) {
        throw new JgpioException("simulated write failure");
      }
    });

    // WHEN-THEN
    assertThatThrownBy(() -> blink.blink(3))
        .isInstanceOf(JgpioException.class)
        .hasMessage("simulated write failure");
    assertThat(led.isRequested()).isFalse();
  }

}
