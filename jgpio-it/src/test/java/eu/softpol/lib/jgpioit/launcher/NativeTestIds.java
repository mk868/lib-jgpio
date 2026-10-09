package eu.softpol.lib.jgpioit.launcher;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import eu.softpol.lib.jgpioit.ItTags;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

/// Generates the test ID files consumed by the GraalVM `NativeImageJUnitLauncher`.
///
/// The native launcher selects tests by unique ID only - it has no tag filtering. Normally these
/// files are recorded while running tests on the JVM, but a JVM run (or a JUnit dry run) records
/// individual parameterized invocations only if they were actually executed. This generator uses
/// test discovery instead and writes the ID of every test method and test template, so a single
/// native image can serve all [TestEnvironment]s.
///
/// Usage: `NativeTestIds <all-tests-dir> <per-environment-dir>`
///
/// - `<all-tests-dir>` - IDs of all container tests, used at image build time
/// - `<per-environment-dir>/<environment>` - IDs of the tests for the given environment, used at
///   image run time
public class NativeTestIds {

  // prefix expected by the native launcher, see UniqueIdTrackingListener.DEFAULT_OUTPUT_FILE_PREFIX
  static final String FILE_NAME = "junit-platform-unique-ids.txt";

  private NativeTestIds() {
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new IllegalArgumentException(
          "Usage: NativeTestIds <all-tests-dir> <per-environment-dir>");
    }
    var allTestsDir = Path.of(args[0]);
    var perEnvironmentDir = Path.of(args[1]);

    write(allTestsDir, ItTags.CONTAINER_IT);
    for (var environment : TestEnvironment.ALL) {
      write(perEnvironmentDir.resolve(environment.name()), environment.tagExpression());
    }
  }

  private static void write(Path dir, String tagExpression) throws IOException {
    var ids = discover(tagExpression);
    if (ids.isEmpty()) {
      throw new IllegalStateException("No tests found for '" + tagExpression + "'");
    }
    Files.createDirectories(dir);
    Files.write(dir.resolve(FILE_NAME), ids);
    System.out.printf("%d test IDs for '%s' written to %s%n", ids.size(), tagExpression, dir);
  }

  private static List<String> discover(String tagExpression) {
    var request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectPackage(ItTags.class.getPackageName()))
        .filters(TagFilter.includeTags(tagExpression))
        .build();
    TestPlan testPlan = LauncherFactory.create().discover(request);

    // test methods and test templates (e.g. @ParameterizedTest) - template invocations are not
    // known until execution, selecting the template runs all of them
    return testPlan.getRoots().stream()
        .flatMap(root -> testPlan.getDescendants(root).stream())
        .filter(id -> id.getSource().filter(MethodSource.class::isInstance).isPresent())
        .map(TestIdentifier::getUniqueId)
        .sorted()
        .toList();
  }
}
