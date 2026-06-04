package eu.softpol.lib.jgpioit.launcher;

public record TestEnvironment(
    String name,
    String image,
    String tagExpression
) {

  @Override
  public String toString() {
    return "%s (%s)".formatted(name, image);
  }
}
