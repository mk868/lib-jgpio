package eu.softpol.lib.jgpioit.launcher;

import eu.softpol.lib.jgpioit.ItTags;
import java.util.List;

public record TestEnvironment(
    String name,
    String image,
    String tagExpression
) {

  public static final List<TestEnvironment> ALL = List.of(
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

  @Override
  public String toString() {
    return "%s (%s)".formatted(name, image);
  }
}
