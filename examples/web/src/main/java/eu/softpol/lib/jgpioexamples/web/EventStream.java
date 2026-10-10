package eu.softpol.lib.jgpioexamples.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.softpol.lib.jgpioexamples.web.GpioMonitor.LineId;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/// Sends line states to web pages as
/// [Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events).
///
/// A connected page first gets a `snapshot` event with all chips and lines, then a `line` event
/// for each line whose state changed.
final class EventStream implements HttpHandler {

  private static final int QUEUE_CAPACITY = 1000;
  /// Ends the stream of a page that can't keep up, the browser reconnects and gets a new snapshot
  private static final String DISCONNECT = "";

  private final String chipsJson;
  private final Map<LineId, LineState> lines = new LinkedHashMap<>();
  private final List<BlockingQueue<String>> clients = new ArrayList<>();

  EventStream(final String chipsJson) {
    this.chipsJson = chipsJson;
  }

  /// Sends the changed lines to all connected pages.
  synchronized void publish(final List<LineState> changed) {
    for (var line : changed) {
      lines.put(new LineId(line.chip(), line.offset()), line);
      var message = event("line", line.toJson());
      for (var client : clients) {
        if (!client.offer(message)) {
          client.clear();
          client.add(DISCONNECT);
        }
      }
    }
  }

  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    try (exchange) {
      if (!exchange.getRequestMethod().equals("GET")) {
        exchange.sendResponseHeaders(405, -1);
        return;
      }
      exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
      exchange.getResponseHeaders().set("Cache-Control", "no-cache");
      exchange.sendResponseHeaders(200, 0);
      var client = connect();
      try {
        stream(client, exchange);
      } finally {
        disconnect(client);
      }
    }
  }

  private void stream(final BlockingQueue<String> client, final HttpExchange exchange) {
    try {
      var body = exchange.getResponseBody();
      while (true) {
        var message = client.poll(15, TimeUnit.SECONDS);
        if (DISCONNECT.equals(message)) {
          return;
        }
        // a comment sent when idle, detects closed pages and keeps proxies from timing out
        body.write((message != null ? message : ": keep-alive\n\n").getBytes(UTF_8));
        body.flush();
      }
    } catch (IOException e) {
      // the page was closed
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private synchronized BlockingQueue<String> connect() {
    var snapshot = "{\"chips\":" + chipsJson
                   + ",\"lines\":" + lines.values().stream()
                       .map(LineState::toJson)
                       .collect(Collectors.joining(",", "[", "]"))
                   + "}";
    var client = new LinkedBlockingQueue<String>(QUEUE_CAPACITY);
    client.add(event("snapshot", snapshot));
    clients.add(client);
    return client;
  }

  private synchronized void disconnect(final BlockingQueue<String> client) {
    clients.remove(client);
  }

  private static String event(final String name, final String json) {
    return "event: " + name + "\ndata: " + json + "\n\n";
  }

}
