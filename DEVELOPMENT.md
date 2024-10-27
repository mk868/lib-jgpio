# Development

## Requirements

- Java 22 or newer
- [jextract](https://jdk.java.net/jextract/) in `$PATH`

## How to build

```shell
./prepare
cd jgpio
mvn install
```

By default, the build will use `jextract` from your system's `$PATH` variable during Maven
compilation.

If you want to download and use a fixed version of `jextract`, use the following:

```shell
./prepare-jextract.sh
mvn install -Djextract.bin=../jextract/bin/jextract
```
