package eu.softpol.lib.jgpioexamples.web;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.softpol.lib.jgpio.Jgpio;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/// A web page showing all chips and lines, refreshed live, which can also request free lines as
/// inputs or outputs.
///
/// Uses only the JDK: [HttpServer] serves the page and the API, line changes are sent to the page
/// with Server-Sent Events.
public class WebMonitor {

  private static final Logger logger = System.getLogger(WebMonitor.class.getName());
  /// Line changes shorter than this may be missed
  private static final long POLL_INTERVAL_MS = 200;

  public static void main(final String[] args) throws IOException {
    var host = "0.0.0.0";
    var port = 8080;
    var mock = false;
    try {
      for (var i = 0; i < args.length; i++) {
        switch (args[i]) {
          case "--port" -> port = Integer.parseInt(args[++i]);
          case "--mock" -> mock = true;
          default -> throw new IllegalArgumentException(args[i]);
        }
      }
    } catch (RuntimeException e) {
      System.err.println("Usage: WebMonitor [--port PORT] [--mock]");
      System.exit(1);
    }

    var gpio = new GpioMonitor(mock ? MockBoard.create() : Jgpio.getInstance());
    var events = new EventStream(gpio.chipsJson());
    events.publish(gpio.poll());

    // a single thread reads the lines, so the pages get the changes in order
    var poller = Executors.newSingleThreadScheduledExecutor();
    Runnable update = () -> {
      try {
        events.publish(gpio.poll());
      } catch (RuntimeException e) {
        logger.log(Level.ERROR, "Cannot read lines", e);
      }
    };
    poller.scheduleWithFixedDelay(
        update, POLL_INTERVAL_MS, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);

    var server = HttpServer.create(new InetSocketAddress(host, port), 0);
    // each page keeps a request open for the events
    server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    server.createContext("/", staticFiles());
    server.createContext("/api/events", events);
    server.createContext("/api/lines/", new LineApi(gpio, () -> poller.execute(update)));
    server.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      server.stop(0);
      poller.shutdownNow();
      gpio.close();
    }));
    System.out.printf("Listening on http://%s:%d/%s%n", host, port, mock ? " (mock)" : "");
  }

  /// Serves the page from `src/main/resources/web`.
  private static HttpHandler staticFiles() throws IOException {
    var files = Map.of(
        "/", load("index.html", "text/html; charset=utf-8"),
        "/app.js", load("app.js", "text/javascript; charset=utf-8"),
        "/style.css", load("style.css", "text/css; charset=utf-8")
    );
    return exchange -> {
      try (exchange) {
        var file = files.get(exchange.getRequestURI().getPath());
        if (file == null || !exchange.getRequestMethod().equals("GET")) {
          exchange.sendResponseHeaders(404, -1);
          return;
        }
        exchange.getResponseHeaders().set("Content-Type", file.contentType());
        exchange.sendResponseHeaders(200, file.content().length);
        exchange.getResponseBody().write(file.content());
      }
    };
  }

  private static StaticFile load(final String name, final String contentType) throws IOException {
    try (var in = WebMonitor.class.getResourceAsStream("/web/" + name)) {
      if (in == null) {
        throw new IOException("Resource /web/" + name + " not found");
      }
      return new StaticFile(contentType, in.readAllBytes());
    }
  }

  private record StaticFile(String contentType, byte[] content) {

  }

}
