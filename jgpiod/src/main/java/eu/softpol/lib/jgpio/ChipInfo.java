package eu.softpol.lib.jgpio;

/// Immutable information about chip
public interface ChipInfo {

  /// Get chip name
  ///
  /// @return Chip name
  String name();

  /// Get chip label
  ///
  /// @return chip label
  String label();

  /// Count lines
  ///
  /// @return number of lines
  int countLines();

  /// Open chip
  ///
  /// @return chip object
  /// @see Jgpio#openChipByName(String)
  Chip open();

}
