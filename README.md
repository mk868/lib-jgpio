# JGPIO

The libgpiod bindings for Java using
the [Java 22 FFM API](https://docs.oracle.com/en/java/javase/22/core/foreign-function-and-memory-api.html).

Key features:

* FFM + jextract
* JPMS
* System.Logger
* JSpecify annotations

The API allows you to:

- List information about available chips and lines
- Read GPIO pin values
    - Option to set bias: high impedance, pull-up, pull-down
- Writing GPIO pin values

## Usage

Add the library to your project:

```xml
<dependency>
  <groupId>eu.soft-pol.lib.jgpio</groupId>
  <artifactId>jgpio</artifactId>
  <version>1.0.0</version>
</dependency>
```

If you are using JPMS, add `requires` to your `module-info.java` file:

```java
module yourapp {
  requires eu.softpol.lib.jgpio;
}
```

The sample code demonstrating the Blink example:

```java
import eu.softpol.lib.jgpio.Jgpio;

public static void main(final String[] args) throws InterruptedException {
  try (
      var chip = Jgpio.getInstance()
          .openChipByLabel("pinctrl-rp1");
      var gpio14 = chip.getLine("GPIO14")
          .openAsOutput()
  ) {
    for (var i = 0; i < 10; i++) {
      gpio14.write(true);
      Thread.sleep(500);
      gpio14.write(false);
      Thread.sleep(500);
    }
  }
}
```

For details and more examples, see [examples/README.md](examples/README.md).

## System Preparation

### Install libgpiod

Install the libgpiod library on the embedded device.

On Debian:

```shell
apt install libgpiod
```

### GPIO permissions

Before granting the user GPIO permissions, check if the user already has the necessary permissions
by using the following command:

```shell
gpioinfo
```

If the command does not display any error message, it means you already have the required
permissions.

#### Raspberry PI

The Raspberry Pi already has an environment set up for GPIO user interactions.
All you need to do is add your user to the `gpio` group:

```shell
usermod -a -G gpio <username>
```

[More info](https://www.raspberrypi.com/documentation/computers/raspberry-pi.html#permissions)

After logging in again, try to call the `gpioinfo` command to verify access.

#### Other boards

You will need to create a new `gpio` group and add your user to this group.

On Debian:

```shell
groupadd gpio
usermod -a -G gpio <username>
```

Next, create the `/etc/udev/rules.d/60-gpio.rules` rule file with the following content:

```
SUBSYSTEM=="gpio", KERNEL=="gpiochip*", GROUP="gpio", MODE="0660"
```

After restart, try to call the `gpioinfo` command to verify access.

### Loading libgpiod

By default, JVM looks for libraries in directories defined in the `java.library.path` environment
variable.  
You can list these directories using the following command:

```shell
echo 'System.getProperty("java.library.path")' | jshell --feedback concise
```

Sample output:

```
jshell> $1 ==> "/usr/java/packages/lib:/usr/lib64:/lib64:/lib:/usr/lib"
```

To quickly get started, you can create a link to the `libgpiod.so` file in one of these directories:

```shell
mkdir -p /usr/java/packages/lib
ln -s /lib/aarch64-linux-gnu/libgpiod.so.2 /usr/java/packages/lib/libgpiod.so
```

#### Extra: `LD_LIBRARY_PATH`

Your system may require you to specify the library directory in the `LD_LIBRARY_PATH` environment
variable.  
This can be done as follows:

```shell
export LD_LIBRARY_PATH=/usr/java/packages/lib
```
