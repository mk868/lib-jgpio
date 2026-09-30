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
package eu.softpol.lib.jgpio.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.function.Consumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class JgpioMockBuilderTest {

  @Nested
  class Builder {

    @Test
    void build_noChips_returnsEmptyChipList() {
      // GIVEN
      JgpioMock jgpio = JgpioMock.builder()
          .build();

      // WHEN
      var chips = jgpio.jgpio().getChips();

      // THEN
      assertThat(chips).isEmpty();
    }

    @Nested
    class DuplicateChips {

      @Test
      void throwWhenNameIsDuplicated() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder()
            .chip("gpiochip0", c -> {
            });

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip("gpiochip0", c -> {
        }))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("name 'gpiochip0'");
      }

      @Test
      void throwWhenSameChipDefinedByNameAndNumber() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder()
            .chip("gpiochip0", c -> {
            });

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip(0, c -> {
        }))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("gpiochip0");
      }

      @Test
      void throwWhenSameNameInDifferentDirectory() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder()
            .chip(Path.of("/dev/gpiochip0"), c -> {
            });

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip(Path.of("/other/gpiochip0"), c -> {
        }))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("name 'gpiochip0'");
      }

      @Test
      void throwWhenNumberIsDuplicatedWithDifferentName() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder()
            .chip("gpiochip1", c -> {
            });

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip("gpiochip01", c -> {
        }))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("number 1");
      }

      @Test
      void chipBuilderNotCalledForDuplicate() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder()
            .chip("gpiochip0", c -> {
            });
        var called = new boolean[1];

        // WHEN
        assertThatThrownBy(() -> builder.chip("gpiochip0", c -> called[0] = true))
            .isInstanceOf(IllegalArgumentException.class);

        // THEN
        assertThat(called[0]).isFalse();
      }

      @Test
      void sameLabel_allowed() {
        // GIVEN-WHEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.label("pinctrl"))
            .chip("gpiochip1", c -> c.label("pinctrl"))
            .build();

        // THEN
        assertThat(mock.jgpio().getChips()).hasSize(2);
        assertThat(mock.jgpio().openChipByLabel("pinctrl").name()).isEqualTo("gpiochip0");
      }
    }

    @Nested
    class ChipByName {

      @Test
      void returnsBuilderForChaining() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN
        JgpioMock.Builder result = builder.chip("gpiochip0", c -> {
        });

        // THEN
        assertThat(result).isSameAs(builder);
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenNameIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip((String) null, c -> {
        }))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("name");
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenChipBuilderIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip("gpiochip0", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("chipBuilder");
      }

      @ParameterizedTest
      @ValueSource(strings = {"foo", "gpiochip", "gpiochipX", "gpiochip-1", "xgpiochip1",
          "gpiochip1gpiochip", "a/gpiochip0", "gpiochip99999999999"})
      void throwWhenNameIsNotGpiochipN(String name) {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip(name, c -> {
        }))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("gpiochipN");
      }
    }

    @Nested
    class ChipByNumber {

      @Test
      void throwWhenNumberIsNegative() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip(-1, c -> {
        }))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("number");
      }

      @Test
      void returnsBuilderForChaining() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN
        JgpioMock.Builder result = builder.chip(0, c -> {
        });

        // THEN
        assertThat(result).isSameAs(builder);
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenChipBuilderIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip(0, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("chipBuilder");
      }
    }

    @Nested
    class ChipByPath {

      @Test
      void returnsBuilderForChaining() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN
        JgpioMock.Builder result = builder.chip(Path.of("/dev/gpiochip0"), c -> {
        });

        // THEN
        assertThat(result).isSameAs(builder);
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenPathIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip((Path) null, c -> {
        }))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("path");
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenChipBuilderIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip(Path.of("/dev/gpiochip0"), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("chipBuilder");
      }

      @Test
      void throwWhenFileNameIsNotGpiochipN() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip(Path.of("/dev/foo"), c -> {
        }))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("gpiochipN");
      }

      @Test
      void throwWhenPathHasNoFileName() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();
        Path root = Path.of("/").getRoot();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip(root, c -> {
        }))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no file name");
      }
    }
  }

  @Nested
  class ChipBuilder {

    @Nested
    class Label {

      @Test
      void returnsChipBuilderForChaining() {
        // GIVEN-WHEN-THEN
        JgpioMock.builder()
            .chip("gpiochip0", c -> {
              JgpioMock.ChipBuilder result = c.label("some-label");
              assertThat(result).isSameAs(c);
            })
            .build();
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenLabelIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(() -> builder.chip("gpiochip0", c -> c.label(null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("label");
      }
    }

    @Nested
    class Line {

      @Test
      void returnsChipBuilderForChaining() {
        // GIVEN-WHEN-THEN
        JgpioMock.builder()
            .chip("gpiochip0", c -> {
              JgpioMock.ChipBuilder result = c.line(0, l -> {
              });
              assertThat(result).isSameAs(c);
            })
            .build();
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenLineBuilderIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(
            () -> builder.chip("gpiochip0", c -> c.line(0, (Consumer<JgpioMock.LineBuilder>) null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("lineBuilder");
      }

      @Test
      void throwWhenOffsetIsNegative() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(
            () -> builder.chip("gpiochip0", c -> c.line(-1, l -> {
            })))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("offset");
      }

      @Test
      void throwWhenOffsetIsDuplicated() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(
            () -> builder.chip("gpiochip0", c -> c
                .line(3, l -> l.name("first"))
                .line(3, l -> l.name("second"))))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("offset 3");
      }

      @Test
      void throwWhenOffsetIsDuplicatedByNameShortcut() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(
            () -> builder.chip("gpiochip0", c -> c
                .line(3, l -> {
                })
                .line(3, "gpio3")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("offset 3");
      }

      @Test
      void sameOffsetOnDifferentChips_allowed() {
        // GIVEN-WHEN
        JgpioMock mock = JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(3, "a"))
            .chip("gpiochip1", c -> c.line(3, "b"))
            .build();

        // THEN
        assertThat(mock.chip("gpiochip0").line(3).name()).isEqualTo("a");
        assertThat(mock.chip("gpiochip1").line(3).name()).isEqualTo("b");
      }
    }

    @Nested
    class LineWithName {

      @Test
      void returnsChipBuilderForChaining() {
        // GIVEN-WHEN-THEN
        JgpioMock.builder()
            .chip("gpiochip0", c -> {
              JgpioMock.ChipBuilder result = c.line(0, "gpio0");
              assertThat(result).isSameAs(c);
            })
            .build();
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenNameIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(
            () -> builder.chip("gpiochip0", c -> c.line(0, (String) null)))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("name");
      }
    }
  }

  @Nested
  class LineBuilder {

    @Nested
    class Name {

      @Test
      void returnsLineBuilderForChaining() {
        // GIVEN-WHEN-THEN
        JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(0, l -> {
              JgpioMock.LineBuilder result = l.name("gpio0");
              assertThat(result).isSameAs(l);
            }))
            .build();
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenNameIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(
            () -> builder.chip("gpiochip0", c -> c.line(0, l -> {
              l.name(null);
            })))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("name");
      }
    }

    @Nested
    class InputLevels {

      @Test
      void returnsLineBuilderForChaining() {
        // GIVEN-WHEN-THEN
        JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(0, l -> {
              JgpioMock.LineBuilder result = l.inputLevel(InputLevel.HIGH);
              assertThat(result).isSameAs(l);
            }))
            .build();
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenInputLevelIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(
            () -> builder.chip("gpiochip0", c -> c.line(0, l -> {
              l.inputLevel(null);
            })))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("level");
      }
    }

    @Nested
    class ExternallyRequested {

      @Test
      void returnsLineBuilderForChaining() {
        // GIVEN-WHEN-THEN
        JgpioMock.builder()
            .chip("gpiochip0", c -> c.line(0, l -> {
              JgpioMock.LineBuilder result = l.externallyRequested(MockLineRequest.asInput("app"));
              assertThat(result).isSameAs(l);
            }))
            .build();
      }

      @Test
      @SuppressWarnings("NullAway")
      void throwWhenExternallyRequestedIsNull() {
        // GIVEN
        JgpioMock.Builder builder = JgpioMock.builder();

        // WHEN-THEN
        assertThatThrownBy(
            () -> builder.chip("gpiochip0", c -> c.line(0, l -> {
              l.externallyRequested(null);
            })))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("request");
      }
    }
  }
}
