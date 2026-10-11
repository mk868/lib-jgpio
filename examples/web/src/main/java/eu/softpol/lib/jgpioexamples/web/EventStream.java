package eu.softpol.lib.jgpioexamples.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.softpol.lib.jgpioexamples.web.GpioMonitor.LineId;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

final class EventStream implements HttpHandler {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final int QUEUE_CAPACITY = 1000;
  private static final String DISCONNECT = "";

  private final List<ChipState> chips;
  private final Map<LineId, LineState> lines = new LinkedHashMap<>();
  private final List<BlockingQueue<String>> clients = new ArrayList<>();

  EventStream(final List<ChipState> chips) {
    this.chips = chips;
  }

  synchronized void publish(final List<LineState> changed) {
    for (var line : changed) {
      lines.put(new LineId(line.chip(), line.offset()), line);
      var message = event("line", line);
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
    var client = new LinkedBlockingQueue<String>(QUEUE_CAPACITY);
    client.add(event("snapshot", new Snapshot(chips, List.copyOf(lines.values()))));
    clients.add(client);
    return client;
  }

  private synchronized void disconnect(final BlockingQueue<String> client) {
    clients.remove(client);
  }

  private static String event(final String name, final Object data) {
    try {
      return "event: " + name + "\ndata: " + MAPPER.writeValueAsString(data) + "\n\n";
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  private record Snapshot(List<ChipState> chips, List<LineState> lines) {

  }

}
