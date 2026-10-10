package eu.softpol.lib.jgpioexamples.web;

import static eu.softpol.lib.jgpioexamples.web.Json.json;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Chip;
import eu.softpol.lib.jgpio.DriveMode;
import eu.softpol.lib.jgpio.InputMode;
import eu.softpol.lib.jgpio.Jgpio;
import eu.softpol.lib.jgpio.Line;
import eu.softpol.lib.jgpio.LineInputSession;
import eu.softpol.lib.jgpio.LineOutputSession;
import eu.softpol.lib.jgpio.OutputMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/// Keeps all chips open, reads the state of their lines and controls the lines requested from the
/// web page.
///
/// Chips and line sessions must not be used by many threads at once, so all methods are
/// synchronized.
final class GpioMonitor implements AutoCloseable {

  /// The consumer of lines requested from the web page, shown e.g. by `gpioinfo`
  static final String CONSUMER = "jgpio-web";

  private final List<Chip> chips = new ArrayList<>();
  private final Map<LineId, Control> controls = new HashMap<>();
  private final Map<LineId, LineState> states = new HashMap<>();

  GpioMonitor(final Jgpio jgpio) {
    try {
      for (var chipInfo : jgpio.getChips()) {
        chips.add(chipInfo.open());
      }
    } catch (RuntimeException e) {
      close();
      throw e;
    }
  }

  /// Returns the names, labels and line counts of the chips as a JSON array.
  synchronized String chipsJson() {
    return chips.stream()
        .map(chip -> "{\"name\":" + json(chip.name())
                     + ",\"label\":" + json(chip.label())
                     + ",\"lines\":" + json(chip.countLines())
                     + "}")
        .collect(Collectors.joining(",", "[", "]"));
  }

  /// Reads the state of all lines.
  ///
  /// @return the lines whose state changed since the previous call, all lines on the first call
  synchronized List<LineState> poll() {
    var changed = new ArrayList<LineState>();
    for (var chip : chips) {
      for (var offset = 0; offset < chip.countLines(); offset++) {
        var state = readState(chip, offset);
        if (!state.equals(states.put(new LineId(chip.name(), offset), state))) {
          changed.add(state);
        }
      }
    }
    return changed;
  }

  private LineState readState(final Chip chip, final int offset) {
    // get the line on every poll, the libgpiod v1 backend refreshes the line info on lookup
    var line = chip.getLine(offset);
    var control = controls.get(new LineId(chip.name(), offset));
    return new LineState(
        chip.name(),
        offset,
        line.name(),
        line.direction(),
        line.isUsed(),
        line.consumer(),
        control != null,
        control instanceof InputControl input ? input.bias() : null,
        control instanceof OutputControl output ? output.driveMode() : null,
        switch (control) {
          case null -> null;
          case InputControl input -> input.session().read();
          case OutputControl output -> output.value();
        }
    );
  }

  /// Requests the line as an input, releasing it first when it's already requested by this
  /// application.
  ///
  /// @param bias the bias, `null` to leave it unchanged
  synchronized void requestInput(final LineId id, final Bias bias) {
    var line = line(id);
    release(id);
    var mode = InputMode.builder()
        .consumer(CONSUMER);
    if (bias != null) {
      mode.bias(bias);
    }
    controls.put(id, new InputControl(line.openAsInput(mode.build()), bias));
  }

  /// Requests the line as an output, releasing it first when it's already requested by this
  /// application.
  ///
  /// @param driveMode the drive mode, `null` to leave it unchanged
  synchronized void requestOutput(final LineId id, final DriveMode driveMode, final boolean value) {
    var line = line(id);
    release(id);
    var mode = OutputMode.builder()
        .consumer(CONSUMER)
        .initialValue(value);
    if (driveMode != null) {
      mode.driveMode(driveMode);
    }
    controls.put(id, new OutputControl(line.openAsOutput(mode.build()), driveMode, value));
  }

  synchronized void write(final LineId id, final boolean value) {
    if (!(controls.get(id) instanceof OutputControl output)) {
      throw notRequested(id, "output");
    }
    output.session().write(value);
    controls.put(id, new OutputControl(output.session(), output.driveMode(), value));
  }

  synchronized void setBias(final LineId id, final Bias bias) {
    if (!(controls.get(id) instanceof InputControl input)) {
      throw notRequested(id, "input");
    }
    input.session().setBias(bias);
    controls.put(id, new InputControl(input.session(), bias));
  }

  synchronized void setDriveMode(final LineId id, final DriveMode driveMode) {
    if (!(controls.get(id) instanceof OutputControl output)) {
      throw notRequested(id, "output");
    }
    output.session().setDriveMode(driveMode);
    controls.put(id, new OutputControl(output.session(), driveMode, output.value()));
  }

  /// Releases the line when it's requested by this application.
  synchronized void release(final LineId id) {
    var control = controls.remove(id);
    if (control != null) {
      control.close();
    }
  }

  /// Releases all lines requested by this application and closes the chips.
  @Override
  public synchronized void close() {
    controls.values().forEach(Control::close);
    controls.clear();
    chips.forEach(Chip::close);
    chips.clear();
  }

  private Line line(final LineId id) {
    return chips.stream()
        .filter(chip -> chip.name().equals(id.chip()))
        .findFirst()
        .flatMap(chip -> chip.findLine(id.offset()))
        .orElseThrow(() -> new NoSuchElementException("Line " + id + " not found"));
  }

  private static IllegalStateException notRequested(final LineId id, final String direction) {
    return new IllegalStateException(
        "Line " + id + " is not requested as " + direction + " by this application");
  }

  /// Identifies a line by the name of its chip and its offset.
  record LineId(String chip, int offset) {

    @Override
    public String toString() {
      return chip + "/" + offset;
    }
  }

  /// A line requested from the web page.
  private sealed interface Control extends AutoCloseable {

    @Override
    void close();
  }

  private record InputControl(LineInputSession session, Bias bias) implements Control {

    @Override
    public void close() {
      session.close();
    }
  }

  private record OutputControl(LineOutputSession session, DriveMode driveMode, boolean value)
      implements Control {

    @Override
    public void close() {
      session.close();
    }
  }

}
