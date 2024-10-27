package eu.softpol.lib.jgpio.internal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpio.internal.testdata.foobar_h;
import eu.softpol.lib.jgpio.internal.testdata.zip_h;
import org.junit.jupiter.api.Test;

class LibDiscoveryTest {

  @Test
  void tryToInitLibrary_loadsLibrary() {
    // GIVEN
    var generatedClass = zip_h.class;
    var libName = "zip"; // libzip.so is a part of the JDK

    // WHEN-THEN
    assertThatCode(() -> LibDiscovery.tryToInitLibrary(generatedClass, libName))
        .doesNotThrowAnyException();
  }

  @Test
  void tryToInitLibrary_throwWhenCannotLoadLibrary() {
    // GIVEN
    var generatedClass = foobar_h.class;
    var libName = "foobar"; // libfoobar.so doesn't exist, cannot be loaded

    // WHEN-THEN
    assertThatThrownBy(() -> LibDiscovery.tryToInitLibrary(generatedClass, libName))
        .isInstanceOf(JgpioException.class);
  }
}
