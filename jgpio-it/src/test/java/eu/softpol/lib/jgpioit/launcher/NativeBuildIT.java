package eu.softpol.lib.jgpioit.launcher;

import eu.softpol.lib.jgpioit.ItTags;
import java.io.IOException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/// Builds the native tests only, then run on the test board by [NativeContainerMatrixIT] with
/// `-Djgpio.it.native.prebuilt=true`.
@Tag(ItTags.NATIVE_BUILD)
class NativeBuildIT {

  @Test
  void buildNativeTests() throws IOException {
    NativeTests.build();
  }
}
