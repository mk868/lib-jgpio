package eu.softpol.lib.jgpio.internal.testdata;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;

public class foobar_h {

  static final Arena LIBRARY_ARENA = Arena.ofAuto();

  static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.libraryLookup(
          System.mapLibraryName("foobar"), LIBRARY_ARENA)
      .or(SymbolLookup.loaderLookup())
      .or(Linker.nativeLinker().defaultLookup());
}
