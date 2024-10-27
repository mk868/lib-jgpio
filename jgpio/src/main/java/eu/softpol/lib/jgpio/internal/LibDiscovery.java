/*
 * Copyright 2024 SOFT-POL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.softpol.lib.jgpio.internal;

import static eu.softpol.lib.jgpio.internal.LibDiscovery.PathDiscoveryResult.LIB_FOUND;
import static eu.softpol.lib.jgpio.internal.LibDiscovery.PathDiscoveryResult.LIB_NOT_FOUND;

import eu.softpol.lib.jgpio.JgpioException;
import java.io.File;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

public class LibDiscovery {

  private static final Logger logger = System.getLogger(LibDiscovery.class.getName());

  private LibDiscovery() {
  }

  /// Try to load the library, in case of problems print detailed information about the context of
  /// the problem
  ///
  /// @param clazz   class generated by jextract
  /// @param libName library name
  public static void tryToInitLibrary(Class<?> clazz, String libName) {
    try {
      // triggering class initialization
      Class.forName(clazz.getName());
    } catch (ExceptionInInitializerError | ClassNotFoundException ex) {
      var nativeLibName = System.mapLibraryName(libName);
      var libAnalysis = LibDiscovery.analyzeLibraryPath(nativeLibName);

      logger.log(Level.ERROR, "Cannot open {0} library.", libName);
      logger.log(Level.ERROR, """
              The JGPIO lib checked for `{0}` file in the `java.library.path` and these are the results:
              {1}
              """,
          nativeLibName,
          libAnalysis.entrySet().stream()
              .map(kv -> " - " + kv.getKey() + " - " + switch (kv.getValue()) {
                case LIB_NOT_FOUND -> "not found";
                case LIB_FOUND -> "**LIB FOUND**";
              })
              .collect(Collectors.joining("\n"))
      );

      var foundPaths = libAnalysis.entrySet().stream()
          .filter(kv -> kv.getValue() == LIB_FOUND)
          .map(Entry::getKey)
          .toList();
      if (!foundPaths.isEmpty()) {
        logger.log(Level.ERROR, """
                Looks like {0} file has been found, but cannot be loaded.
                Your system may require setting the lib directory in the `LD_LIBRARY_PATH` environment variable.
                Please check `dlopen` manual for more details.
                """,
            nativeLibName);
      }
      throw new JgpioException("Cannot open " + libName + " library", ex);
    }
  }

  /// Check if library located in the java library path
  ///
  /// @param nativeLibName library name
  /// @return map of paths with search result
  private static Map<String, PathDiscoveryResult> analyzeLibraryPath(String nativeLibName) {
    var libPath = Objects.requireNonNullElse(System.getProperty("java.library.path"), "");

    var result = new HashMap<String, PathDiscoveryResult>();
    for (var path : libPath.split(File.pathSeparator)) {
      result.put(
          path,
          Files.exists(Path.of(path, nativeLibName)) ? LIB_FOUND : LIB_NOT_FOUND
      );
    }
    return Map.copyOf(result);
  }

  enum PathDiscoveryResult {
    LIB_FOUND,
    LIB_NOT_FOUND;
  }
}