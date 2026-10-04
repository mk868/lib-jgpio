package eu.softpol.lib.jgpioit.launcher;

import static eu.softpol.lib.jgpioit.launcher.NativeTests.printf;
import static org.assertj.core.api.Assertions.assertThat;

import eu.softpol.lib.jgpioit.ItTags;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/// Runs the container integration tests compiled to a GraalVM native image in every
/// [TestEnvironment].
///
/// With `-Djgpio.it.native.prebuilt=true` the native tests previously built by [NativeBuildIT] are
/// used, otherwise they are built first.
@Tag(ItTags.NATIVE_CONTAINER_LAUNCHER)
public class NativeContainerMatrixIT {

  private static final List<TestEnvironment> ENVIRONMENTS = TestEnvironment.ALL;

  private static final Duration RUN_TIMEOUT = Duration.ofMinutes(15);

  @BeforeAll
  static void prepareNativeTests() throws IOException {
    if (Boolean.getBoolean("jgpio.it.native.prebuilt")) {
      assertThat(NativeTests.executable())
          .as("Prebuilt native tests")
          .isRegularFile();
      printf("%n=== Using prebuilt native tests from %s ===%n", NativeTests.OUTPUT_DIR);
      return;
    }
    NativeTests.build();
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @FieldSource("ENVIRONMENTS")
  void assertNativeIntegrationTestsPass(TestEnvironment environment) {
    try (GenericContainer<?> container = new GenericContainer<>(
        DockerImageName.parse(environment.image()))
        .withPrivilegedMode(true)
        .withCopyFileToContainer(
            MountableFile.forHostPath(NativeTests.executable(), 0755),
            "/native-it/" + NativeTests.EXECUTABLE
        )
        .withCopyFileToContainer(
            MountableFile.forHostPath(NativeTests.testIdsFile(environment)),
            "/native-it/test-ids/" + NativeTestIds.FILE_NAME
        )
        .withWorkingDirectory("/native-it")
        .withCommand(
            "./" + NativeTests.EXECUTABLE,
            "-Djunit.platform.listeners.uid.tracking.output.dir=/native-it/test-ids",
            "--xml-output-dir",
            "/native-it/reports")
        .withLogConsumer(frame -> printf("[%s] %s", environment.name(), frame.getUtf8String()))) {
      printf("%n=== Running native IT in %s (%s) ===%n", environment.name(), environment.image());

      int exitCode = NativeTests.startAndWait(container, RUN_TIMEOUT);

      printf("%n=== Finished native IT in %s ===%n", environment.name());

      assertThat(exitCode)
          .as("Exit code for %s", environment.name())
          .isEqualTo(0);
    }
  }
}
