/*
 * Copyright 2024-2026 SOFT-POL
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

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/// Checks that the GraalVM native image metadata registers every downcall of the jextract
/// bindings - native images can only call native functions with descriptors registered at build
/// time.
class NativeImageMetadataTest {

  private static final Path GENERATED_SOURCES = Path.of("target", "generated-sources", "ffm");
  private static final Path METADATA = Path.of("src", "main", "resources", "META-INF",
      "native-image", "eu.soft-pol.lib.jgpio", "jgpio", "reachability-metadata.json");

  private static final Pattern DESCRIPTOR = Pattern.compile(
      "FunctionDescriptor\\.(of|ofVoid)\\(([^)]*)\\)");
  private static final Pattern ARGUMENT_SEPARATOR = Pattern.compile("\\s*,\\s*");
  // jextract layouts to the JNI canonical layout names (see java.lang.foreign.Linker) of the Linux
  // LP64 data model - libgpiod is Linux only. The JNI names keep the result independent of the
  // platform jextract ran on, e.g. size_t is C_LONG on Linux, but C_LONG_LONG on Windows.
  private static final Map<String, String> CANONICAL_TYPES = Map.of(
      "C_BOOL", "jboolean",
      "C_CHAR", "jbyte",
      "C_SHORT", "jshort",
      "C_INT", "jint",
      "C_LONG", "jlong",
      "C_LONG_LONG", "jlong",
      "C_FLOAT", "jfloat",
      "C_DOUBLE", "jdouble",
      "C_POINTER", "void*"
  );

  @Test
  void metadata_registersAllDowncalls() throws IOException {
    // GIVEN
    var expected = toMetadata(downcallsOfGeneratedBindings());

    // WHEN
    var actual = Files.readString(METADATA).replace("\r\n", "\n");

    // THEN
    assertThat(actual)
        .as("%s must register the downcalls of the bindings generated in %s",
            METADATA, GENERATED_SOURCES)
        .isEqualTo(expected);
  }

  private static Set<String> downcallsOfGeneratedBindings() throws IOException {
    var downcalls = new TreeSet<String>();
    try (Stream<Path> files = Files.walk(GENERATED_SOURCES)) {
      for (var file : files.filter(f -> f.toString().endsWith(".java")).toList()) {
        var matcher = DESCRIPTOR.matcher(Files.readString(file));
        while (matcher.find()) {
          var types = Arrays.stream(ARGUMENT_SEPARATOR.split(matcher.group(2).strip()))
              .filter(type -> !type.isEmpty())
              .map(NativeImageMetadataTest::canonicalType)
              .toList();
          if (matcher.group(1).equals("ofVoid")) {
            downcalls.add(toDowncall("void", types));
          } else {
            downcalls.add(toDowncall(types.getFirst(), types.subList(1, types.size())));
          }
        }
      }
    }
    assertThat(downcalls)
        .as("Downcalls found in %s", GENERATED_SOURCES)
        .isNotEmpty();
    return downcalls;
  }

  private static String canonicalType(String jextractLayout) {
    var layout = jextractLayout.substring(jextractLayout.lastIndexOf('.') + 1);
    var type = CANONICAL_TYPES.get(layout);
    if (type == null) {
      throw new AssertionError("No canonical type for " + jextractLayout);
    }
    return type;
  }

  private static String toDowncall(String returnType, List<String> parameterTypes) {
    return """
              {
                "returnType": "%s",
                "parameterTypes": [%s]
              }\
        """.formatted(
        returnType,
        parameterTypes.stream().map(type -> '"' + type + '"').collect(joining(", ")));
  }

  private static String toMetadata(Set<String> downcalls) {
    return """
        {
          "foreign": {
            "downcalls": [
        %s
            ]
          }
        }
        """.formatted(String.join(",\n", downcalls));
  }
}
