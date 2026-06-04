package eu.softpol.lib.jgpioit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpioit.support.annotation.LibgpiodV1IT;
import eu.softpol.lib.jgpioit.support.annotation.LibgpiodV2IT;
import eu.softpol.lib.jgpioit.support.annotation.NoLibgpiodIT;
import org.junit.jupiter.api.Test;

public class JgpioVersionIT {

  @LibgpiodV1IT
  @Test
  void should_version_return_v1_version() {
    var jgpio = Jgpio.getInstance();
    assertThat(jgpio.version()).startsWith("1.6.");
  }

  @LibgpiodV2IT
  @Test
  void should_version_return_v2_version() {
    var jgpio = Jgpio.getInstance();
    assertThat(jgpio.version()).startsWith("2.");
  }

  @NoLibgpiodIT
  @Test
  void should_getInstance_throw_exception() {
    assertThatThrownBy(() -> Jgpio.getInstance())
        .isInstanceOf(JgpioException.class);
  }
}
