# JGPIO examples - web

Before start with examples please check [System Preparation](../../README.md#system-preparation)

A web page showing all chips and lines, similar to `gpioinfo`, refreshed live. Free lines can be
requested as inputs, to watch their values, or as outputs, to set their values.

The example uses only the JDK, without any web framework:

* [HttpServer](https://docs.oracle.com/en/java/javase/22/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html)
  serves the page and the API,
* line changes are sent to the page
  with [Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events),
* the page is plain HTML, CSS and JavaScript, without a build step.

## How to build

To build the project, run the command:

```
mvn clean package
```

The following outputs will be created in the `target` directory:

* `jgpio-examples-web.jar` file
* `libs` directory

Copy these files to the device with GPIO pins.

## How to run

Run the application using command:

```
java \
  --enable-native-access=eu.softpol.lib.jgpio \
  -p jgpio-examples-web.jar:libs \
  -m eu.softpol.lib.jgpioexamples.web/eu.softpol.lib.jgpioexamples.web.WebMonitor
```

Then open `http://<device address>:8080/` in a web browser.

Options:

| option          | default | description                                    |
|-----------------|---------|------------------------------------------------|
| `--port <port>` | `8080`  | the port to listen on                          |
| `--mock`        |         | simulate a Raspberry Pi 5 instead of real GPIO |

## Try without a board

With the `--mock` option the application uses the [JGPIO mock](../testing/README.md) instead of
libgpiod, so it runs on any system, e.g. on Windows (note the `;` separator):

```
java -p "jgpio-examples-web.jar;libs" -m eu.softpol.lib.jgpioexamples.web/eu.softpol.lib.jgpioexamples.web.WebMonitor --mock
```

Then open http://localhost:8080/. Besides the lines used by the system, the mock simulates:

* a wire between `GPIO14` and `GPIO15`: request `GPIO14` as output and `GPIO15` as input, then
  switch `GPIO14`,
* a button between `GND` and `GPIO18`, pressed every other second: request `GPIO18` as input and
  set the pull-up bias.

## How it works

* [GpioMonitor](src/main/java/eu/softpol/lib/jgpioexamples/web/GpioMonitor.java) opens all chips,
  reads the state of all lines every 200 ms and keeps the line sessions requested from the page,
* [EventStream](src/main/java/eu/softpol/lib/jgpioexamples/web/EventStream.java) sends the state of
  all lines to a newly opened page, then only the changed lines,
* [LineApi](src/main/java/eu/softpol/lib/jgpioexamples/web/LineApi.java) handles the line controls,
* [web](src/main/resources/web) contains the page.
