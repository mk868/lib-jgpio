package eu.softpol.lib.jgpioit.launcher;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.sun.management.OperatingSystemMXBean;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/// The container integration tests compiled to a GraalVM native image.
///
/// The native test executable and the per-environment test ID files are stored in [#OUTPUT_DIR].
/// They are built in the `lib-jgpio-it:native-builder` image, based on Oracle Linux 9 (glibc 2.34),
/// so the executable also runs on the Ubuntu based environment images.
class NativeTests {

  // executable name used by the native-maven-plugin test goal
  static final String EXECUTABLE = "native-tests";
  static final Path OUTPUT_DIR = Path.of("target", "native-it")
      .toAbsolutePath()
      .normalize();

  private static final String BUILDER_NAME = "native-builder";
  private static final String BUILDER_IMAGE = "lib-jgpio-it:native-builder";
  private static final Duration BUILD_TIMEOUT = Duration.ofMinutes(60);
  // fail fast on smaller machines with insufficient memory
  private static final long MIN_BUILD_MEMORY = 4L * 1024 * 1024 * 1024;

  private static final Path PROJECT_ROOT = Path.of("..").toAbsolutePath().normalize();
  private static final Path HOST_M2 = Path.of(System.getProperty("user.home"), ".m2")
      .toAbsolutePath()
      .normalize();

  private NativeTests() {
  }

  static Path executable() {
    return OUTPUT_DIR.resolve(EXECUTABLE);
  }

  static Path testIdsFile(TestEnvironment environment) {
    return OUTPUT_DIR.resolve("test-ids")
        .resolve(environment.name())
        .resolve(NativeTestIds.FILE_NAME);
  }

  /// Builds the native tests in the builder container and copies the results to [#OUTPUT_DIR].
  static void build() throws IOException {
    var totalMemory = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class)
        .getTotalMemorySize();
    assertThat(totalMemory)
        .as("Total memory required to build native tests (%d MiB available)",
            totalMemory / 1024 / 1024)
        .isGreaterThanOrEqualTo(MIN_BUILD_MEMORY);

    try (GenericContainer<?> container = new GenericContainer<>(
        DockerImageName.parse(BUILDER_IMAGE))
        .withEnv("MAVEN_USER_HOME", "/m2")
        .withFileSystemBind(HOST_M2.toString(), "/m2", BindMode.READ_ONLY)
        .withCopyFileToContainer(
            MountableFile.forHostPath(PROJECT_ROOT),
            "/workspace"
        )
        .withWorkingDirectory("/workspace")
        .withCommand(
            "bash",
            "-lc",
            """
                ./mvnw -o -ntp \
                -Dmaven.repo.local=/m2/repository \
                -f jgpio-it/pom.xml \
                -Pnative \
                package
                """)
        .withLogConsumer(frame -> printf("[%s] %s", BUILDER_NAME, frame.getUtf8String()))) {
      printf("%n=== Building native tests (%s) ===%n", BUILDER_IMAGE);

      int exitCode = startAndWait(container, BUILD_TIMEOUT);

      printf("%n=== Finished building native tests ===%n");

      assertThat(exitCode)
          .as("Exit code for %s", BUILDER_NAME)
          .isEqualTo(0);

      Files.createDirectories(OUTPUT_DIR);
      container.copyFileFromContainer(
          "/workspace/jgpio-it/target/" + EXECUTABLE,
          executable().toString()
      );
      for (var environment : TestEnvironment.ALL) {
        var testIdsFile = testIdsFile(environment);
        Files.createDirectories(testIdsFile.getParent());
        container.copyFileFromContainer(
            "/workspace/jgpio-it/target/native-test-ids/%s/%s"
                .formatted(environment.name(), NativeTestIds.FILE_NAME),
            testIdsFile.toString()
        );
      }
    }
  }

  /// Starts the container and waits until it exits.
  ///
  /// @return the exit code of the container
  static int startAndWait(GenericContainer<?> container, Duration timeout) {
    container.start();
    return container.getDockerClient()
        .waitContainerCmd(container.getContainerId())
        .exec(new WaitContainerResultCallback())
        .awaitStatusCode(timeout.toSeconds(), TimeUnit.SECONDS);
  }

  static void printf(String format, Object... args) {
    System.out.printf(format, args);
  }
}
