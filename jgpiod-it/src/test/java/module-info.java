import org.jspecify.annotations.NullMarked;

@NullMarked
open module eu.softpol.lib.jgpioit {
  requires eu.softpol.lib.jgpio;

  requires org.jspecify;
  requires org.assertj.core;
  requires net.bytebuddy;
  requires transitive org.junit.jupiter.engine;
  requires org.junit.jupiter.params;
}
