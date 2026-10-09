# JGPIO examples - testing

This example shows how to test code that uses JGPIO without GPIO hardware, using the mocked
backend from the `eu.softpol.lib.jgpio.mock` package. The mock is pure Java, so the tests run on
any OS, including the CI.

## How to run

To run the tests use the command:

```
mvn clean verify
```

## How it works

The code under test gets the [Jgpio](../../jgpio/src/main/java/eu/softpol/lib/jgpio/Jgpio.java)
passed in instead of calling `Jgpio.getInstance()` itself. Its `main` method passes
`Jgpio.getInstance()`, the tests pass `JgpioMock.jgpio()`:

* [Blink](src/main/java/eu/softpol/lib/jgpioexamples/testing/Blink.java) and
  [BlinkTest](src/test/java/eu/softpol/lib/jgpioexamples/testing/BlinkTest.java) - testing outputs,
* [Toggle](src/main/java/eu/softpol/lib/jgpioexamples/testing/Toggle.java) and
  [ToggleTest](src/test/java/eu/softpol/lib/jgpioexamples/testing/ToggleTest.java) - simulating
  inputs. The polling loop stays in `main`, the tests call `update()` directly.

In the test, describe the simulated hardware with `JgpioMock.builder()`:

```java
var mock = JgpioMock.builder()
    .chip("gpiochip0", c -> c
        .label("pinctrl-rp1")
        .line(14, "GPIO14"))
    .build();

var blink = new Blink(mock.jgpio(), Duration.ZERO);
```

Then use the probes returned by `mock.chip(...)` and `.line(...)` to drive inputs and verify
outputs. Probes stay usable after the code under test closes the chip:

```java
var led = mock.chip("gpiochip0").line("GPIO14");

blink.blink(3);

assertThat(led.outputValue()).isFalse();
assertThat(led.isRequested()).isFalse();
```

To simulate an input, set its level. `InputLevel.HIGH_IMPEDANCE` is a floating line, it reads
according to the bias set by the code under test, so the test also checks that the bias is right:

```java
var button = mock.chip("gpiochip0").line("GPIO18");

button.setInputLevel(InputLevel.LOW); // pressed, connected to GND
toggle.update();
assertThat(led.outputValue()).isTrue();

button.setInputLevel(InputLevel.HIGH_IMPEDANCE); // released, pulled up
toggle.update();
assertThat(led.outputValue()).isFalse();
```

`LineProbe` also allows to:

* record or reject written values - `setWriteCallback(...)`,
* supply a different input level on each read - `setReadCallback(...)`,
* simulate a line used by another application - `setExternallyRequested(...)`,
* check the configured bias and drive mode - `bias()`, `driveMode()`.
