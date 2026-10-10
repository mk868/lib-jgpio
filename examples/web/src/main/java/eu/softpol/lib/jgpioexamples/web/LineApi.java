package eu.softpol.lib.jgpioexamples.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.JgpioException;
import eu.softpol.lib.jgpioexamples.web.GpioMonitor.LineId;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/// Controls the lines: `POST /api/lines/{chip}/{offset}/{action}?{parameters}`
///
/// | action    | parameters                       | description                           |
/// |-----------|----------------------------------|---------------------------------------|
/// | `input`   | `bias` (optional)                | requests the line as an input         |
/// | `output`  | `drive`, `value` (both optional) | requests the line as an output        |
/// | `write`   | `value`                          | sets the value of an output line      |
/// | `bias`    | `bias`                           | sets the bias of an input line        |
/// | `drive`   | `drive`                          | sets the drive mode of an output line |
/// | `release` |                                  | releases the line                     |
///
/// `bias` and `drive` take the names of the [Bias] and [DriveMode] constants, `value` takes `true`
/// or `false`.
final class LineApi implements HttpHandler {

  private static final Logger logger = System.getLogger(LineApi.class.getName());

  private final GpioMonitor gpio;
  private final Runnable onChange;

  /// @param onChange called after a successful action, to send the new line state to the pages
  LineApi(final GpioMonitor gpio, final Runnable onChange) {
    this.gpio = gpio;
    this.onChange = onChange;
  }

  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    try (exchange) {
      try {
        execute(exchange);
      } catch (RuntimeException e) {
        var status = switch (e) {
          case HttpError error -> error.status;
          case NoSuchElementException _ -> 404;
          case IllegalArgumentException _ -> 400;
          case JgpioException _, IllegalStateException _ -> 409;
          default -> 500;
        };
        if (status == 500) {
          logger.log(Level.ERROR, "Cannot handle " + exchange.getRequestURI(), e);
        }
        var message = String.valueOf(e.getMessage()).getBytes(UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, message.length);
        exchange.getResponseBody().write(message);
        return;
      }
      onChange.run();
      exchange.sendResponseHeaders(204, -1);
    }
  }

  private void execute(final HttpExchange exchange) {
    if (!exchange.getRequestMethod().equals("POST")) {
      throw new HttpError(405, "Use POST");
    }
    checkOrigin(exchange);
    // ["", "api", "lines", chip, offset, action]
    var path = exchange.getRequestURI().getPath().split("/");
    if (path.length != 6) {
      throw new HttpError(404, "Use /api/lines/{chip}/{offset}/{action}");
    }
    var id = new LineId(path[3], Integer.parseInt(path[4]));
    var params = queryParams(exchange.getRequestURI());
    switch (path[5]) {
      case "input" -> gpio.requestInput(id, optionalEnum(Bias.class, params.get("bias")));
      case "output" -> gpio.requestOutput(
          id,
          optionalEnum(DriveMode.class, params.get("drive")),
          params.containsKey("value") && parseValue(params.get("value"))
      );
      case "write" -> gpio.write(id, parseValue(required(params, "value")));
      case "bias" -> gpio.setBias(id, Bias.valueOf(required(params, "bias")));
      case "drive" -> gpio.setDriveMode(id, DriveMode.valueOf(required(params, "drive")));
      case "release" -> gpio.release(id);
      default -> throw new HttpError(404, "Unknown action '" + path[5] + "'");
    }
  }

  /// Rejects requests sent from pages of other websites open in the browser (cross-site request
  /// forgery), browsers set their address in the `Origin` header.
  private static void checkOrigin(final HttpExchange exchange) {
    var origin = exchange.getRequestHeaders().getFirst("Origin");
    var host = exchange.getRequestHeaders().getFirst("Host");
    if (origin != null && !Objects.equals(URI.create(origin).getAuthority(), host)) {
      throw new HttpError(403, "Requests from " + origin + " are not allowed");
    }
  }

  private static Map<String, String> queryParams(final URI uri) {
    var params = new HashMap<String, String>();
    if (uri.getQuery() != null) {
      for (var param : uri.getQuery().split("&")) {
        var nameValue = param.split("=", 2);
        params.put(nameValue[0], nameValue.length > 1 ? nameValue[1] : "");
      }
    }
    return params;
  }

  private static String required(final Map<String, String> params, final String name) {
    var value = params.get(name);
    if (value == null) {
      throw new HttpError(400, "Missing parameter '" + name + "'");
    }
    return value;
  }

  private static <E extends Enum<E>> E optionalEnum(final Class<E> type, final String name) {
    return name == null ? null : Enum.valueOf(type, name);
  }

  private static boolean parseValue(final String value) {
    return switch (value) {
      case "true" -> true;
      case "false" -> false;
      default -> throw new IllegalArgumentException("Value must be 'true' or 'false'");
    };
  }

  private static class HttpError extends RuntimeException {

    private final int status;

    HttpError(final int status, final String message) {
      super(message);
      this.status = status;
    }
  }

}
