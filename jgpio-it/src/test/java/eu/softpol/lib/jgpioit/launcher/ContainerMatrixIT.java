package eu.softpol.lib.jgpioit.launcher;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.dockerjava.api.command.WaitContainerResultCallback;
import eu.softpol.lib.jgpioit.ItTags;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Tag(ItTags.CONTAINER_LAUNCHER)
public class ContainerMatrixIT {

  private static final List<TestEnvironment> ENVIRONMENTS = List.of(
      new TestEnvironment(
          "libgpiod-v1",
          "lib-jgpio-it:gpiod-1.6",
          "%s & %s".formatted(ItTags.CONTAINER_IT, ItTags.LIBGPIOD_V1)
      ),
      new TestEnvironment(
          "libgpiod-v2",
          "lib-jgpio-it:gpiod-2.x",
          "%s & %s".formatted(ItTags.CONTAINER_IT, ItTags.LIBGPIOD_V2)
      ),
      new TestEnvironment(
          "no-libgpiod",
          "lib-jgpio-it:no-gpiod",
          "%s & %s".formatted(ItTags.CONTAINER_IT, ItTags.NO_LIBGPIOD)
      )
  );

  private static final Path PROJECT_ROOT = Path.of("..").toAbsolutePath().normalize();
  private static final Path HOST_M2 = Path.of(System.getProperty("user.home"), ".m2")
      .toAbsolutePath()
      .normalize();

  @ParameterizedTest(name = "[{index}] {0}")
  @FieldSource("ENVIRONMENTS")
  void assertIntegrationTestsPass(TestEnvironment environment) {
    try (GenericContainer<?> container = new GenericContainer<>(
        DockerImageName.parse(environment.image()))
        .withPrivilegedMode(true)
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
                verify \
                -Dgroups='%s'
                """
                .formatted(environment.tagExpression()))
        .withLogConsumer(frame -> printf("[%s] %s", environment.name(), frame.getUtf8String()))) {
      printf("%n=== Running IT in %s (%s) ===%n", environment.name(), environment.image());

      container.start();

      int exitCode = container.getDockerClient()
          .waitContainerCmd(container.getContainerId())
          .exec(new WaitContainerResultCallback())
          .awaitStatusCode();

      printf("%n=== Finished IT in %s ===%n", environment.name());

      // TODO copy reports from container using container.copyFileFromContainer

      assertThat(exitCode)
          .as("Exit code for %s", environment.name())
          .isEqualTo(0);
    }
  }

  private static void printf(String format, Object... args) {
    System.out.printf(format, args);
  }
}
