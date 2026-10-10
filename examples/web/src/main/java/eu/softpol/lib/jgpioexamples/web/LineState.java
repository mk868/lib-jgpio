package eu.softpol.lib.jgpioexamples.web;

import static eu.softpol.lib.jgpioexamples.web.Json.json;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.DriveMode;

/// State of a line shown on the web page.
///
/// @param name     the line name, `null` for unnamed lines
/// @param consumer the name of the application using the line, `null` when unused or not defined
/// @param owned    `true` when the line is requested by this application
/// @param bias     the bias of an owned input line, `null` when not set
/// @param drive    the drive mode of an owned output line, `null` when not set
/// @param value    the value of an owned line, `null` for other lines, which can't be read without
///                 requesting them
record LineState(
    String chip,
    int offset,
    String name,
    Direction direction,
    boolean used,
    String consumer,
    boolean owned,
    Bias bias,
    DriveMode drive,
    Boolean value
) {

  String toJson() {
    return "{\"chip\":" + json(chip)
           + ",\"offset\":" + json(offset)
           + ",\"name\":" + json(name)
           + ",\"direction\":" + json(direction)
           + ",\"used\":" + json(used)
           + ",\"consumer\":" + json(consumer)
           + ",\"owned\":" + json(owned)
           + ",\"bias\":" + json(bias)
           + ",\"drive\":" + json(drive)
           + ",\"value\":" + json(value)
           + "}";
  }

}
