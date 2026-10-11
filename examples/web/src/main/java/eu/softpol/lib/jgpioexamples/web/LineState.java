package eu.softpol.lib.jgpioexamples.web;

import eu.softpol.lib.jgpio.Bias;
import eu.softpol.lib.jgpio.Direction;
import eu.softpol.lib.jgpio.DriveMode;

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

}
