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

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "eu.softpol.lib.jgpio")
public class InternalArchTest {

  @ArchTest
  static final ArchRule gpiod_and_gpiod2_should_not_depend_on_each_other =
      slices()
          .matching("eu.softpol.lib.jgpio.internal.(gpiod|gpiod2)..")
          .should()
          .notDependOnEachOther();

  @ArchTest
  static final ArchRule only_gpiod_should_use_ffm_libgpiod =
      classes()
          .that().resideInAPackage("eu.softpol.lib.jgpio.internal.ffm.libgpiod..")
          .should().onlyBeAccessed().byClassesThat()
          .resideInAnyPackage(
              "eu.softpol.lib.jgpio.internal.gpiod..",
              "eu.softpol.lib.jgpio.internal.ffm.libgpiod.."
          );

  @ArchTest
  static final ArchRule only_gpiod2_should_use_ffm_libgpiod2 =
      classes()
          .that().resideInAPackage("eu.softpol.lib.jgpio.internal.ffm.libgpiod2..")
          .should().onlyBeAccessed().byClassesThat()
          .resideInAnyPackage(
              "eu.softpol.lib.jgpio.internal.gpiod2..",
              "eu.softpol.lib.jgpio.internal.ffm.libgpiod2.."
          );

}
