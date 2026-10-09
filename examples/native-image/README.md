# JGPIO examples - native image

The [Blink](../basics/README.md#blink) example compiled into a native executable
with [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/).
The executable doesn't need a JVM on the device and starts instantly.

## Requirements

* GraalVM 25 or newer, set as `JAVA_HOME`
* JGPIO 1.5.0 or newer, the first version with the native image metadata. Until it is released,
  install the JGPIO snapshot locally, see [DEVELOPMENT.md](../../DEVELOPMENT.md)

Native Image doesn't cross-compile, build the executable on Linux with the same CPU architecture as
the device, e.g. on the device itself. The build needs about 2 GB of free memory.

## How to build

To build the executable run the command:

```
mvn -Pnative clean package
```

The `blink` executable will be created in the `target` directory. Copy it to the device with GPIO
pins.

The build options are set in the `native` profile in [pom.xml](pom.xml):

* `--enable-native-access=ALL-UNNAMED` - grants native access to JGPIO at build time, it's not
  needed when running the executable,
* `-march=compatibility` - the executable runs on all ARMv8 CPUs. The GraalVM default target
  requires ARMv8.1, not supported e.g. by the Cortex-A53 or Cortex-A72 used in Raspberry Pi 3 and 4.

## How to run

The native executable doesn't use `java.library.path`, `libgpiod.so` must be found by the system
dynamic linker. Install the `libgpiod-dev` package, which provides it:

```
apt install libgpiod-dev
```

Or point `LD_LIBRARY_PATH` to the directory with the link created
in [Loading libgpiod](../../README.md#loading-libgpiod):

```
export LD_LIBRARY_PATH=/usr/java/packages/lib
```

Connect the LED as in the [Blink](../basics/README.md#blink) example, then run:

```
./blink
```

The LED should blink 10 times.
