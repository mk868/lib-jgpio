module eu.softpol.lib.jgpioexamples.web {
  requires eu.softpol.lib.jgpio;
  requires jdk.httpserver;
  requires com.fasterxml.jackson.databind;

  opens eu.softpol.lib.jgpioexamples.web to com.fasterxml.jackson.databind;
}
