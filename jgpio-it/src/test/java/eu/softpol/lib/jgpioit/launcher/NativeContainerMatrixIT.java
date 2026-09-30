package eu.softpol.lib.jgpioit.launcher;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.dockerjava.api.command.WaitContainerResultCallback;
import eu.softpol.lib.jgpioit.ItTags;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/// Runs the container integration tests compiled to a GraalVM native image.
///
/// The native test executable is built once in the builder container and then executed in every
/// [TestEnvironment]. The builder is based on Oracle Linux 9 (glibc 2.34), so the executable also
/// runs on the Ubuntu based environment images.
@Tag(ItTags.NATIVE_CONTAINER_LAUNCHER)
public class NativeContainerMatrixIT {

  private static final List<TestEnvironment> ENVIRONMENTS = TestEnvironment.ALL;

  private static final String BUILDER_NAME = "native-builder";
  private static final String BUILDER_IMAGE = "lib-jgpio-it:native-builder";
  // executable name used by the native-maven-plugin test goal
  private static final String NATIVE_TESTS = "native-tests";

  private static final Path PROJECT_ROOT = Path.of("..").toAbsolutePath().normalize();
  private static final Path HOST_M2 = Path.of(System.getProperty("user.home"), ".m2")
      .toAbsolutePath()
      .normalize();
  private static final Path OUTPUT_DIR = Path.of("target", "native-it")
      .toAbsolutePath()
      .normalize();

  @BeforeAll
  static void buildNativeTests() throws IOException {
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

      int exitCode = startAndWait(container);

      printf("%n=== Finished building native tests ===%n");

      assertThat(exitCode)
          .as("Exit code for %s", BUILDER_NAME)
          .isEqualTo(0);

      Files.createDirectories(OUTPUT_DIR);
      container.copyFileFromContainer(
          "/workspace/jgpio-it/target/" + NATIVE_TESTS,
          OUTPUT_DIR.resolve(NATIVE_TESTS).toString()
      );
      for (var environment : ENVIRONMENTS) {
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

  @ParameterizedTest(name = "[{index}] {0}")
  @FieldSource("ENVIRONMENTS")
  void assertNativeIntegrationTestsPass(TestEnvironment environment) {
    try (GenericContainer<?> container = new GenericContainer<>(
        DockerImageName.parse(environment.image()))
        .withPrivilegedMode(true)
        .withCopyFileToContainer(
            MountableFile.forHostPath(OUTPUT_DIR.resolve(NATIVE_TESTS), 0755),
            "/native-it/" + NATIVE_TESTS
        )
        .withCopyFileToContainer(
            MountableFile.forHostPath(testIdsFile(environment)),
            "/native-it/test-ids/" + NativeTestIds.FILE_NAME
        )
        .withWorkingDirectory("/native-it")
        .withCommand(
            "./" + NATIVE_TESTS,
            "-Djunit.platform.listeners.uid.tracking.output.dir=/native-it/test-ids",
            "--xml-output-dir",
            "/native-it/reports")
        .withLogConsumer(frame -> printf("[%s] %s", environment.name(), frame.getUtf8String()))) {
      printf("%n=== Running native IT in %s (%s) ===%n", environment.name(), environment.image());

      int exitCode = startAndWait(container);

      printf("%n=== Finished native IT in %s ===%n", environment.name());

      assertThat(exitCode)
          .as("Exit code for %s", environment.name())
          .isEqualTo(0);
    }
  }

  private static Path testIdsFile(TestEnvironment environment) {
    return OUTPUT_DIR.resolve("test-ids")
        .resolve(environment.name())
        .resolve(NativeTestIds.FILE_NAME);
  }

  private static int startAndWait(GenericContainer<?> container) {
    container.start();
    return container.getDockerClient()
        .waitContainerCmd(container.getContainerId())
        .exec(new WaitContainerResultCallback())
        .awaitStatusCode();
  }

  private static void printf(String format, Object... args) {
    System.out.printf(format, args);
  }
}
