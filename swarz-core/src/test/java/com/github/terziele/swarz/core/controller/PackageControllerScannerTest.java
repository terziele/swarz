/* (C)2021 */
package com.github.terziele.swarz.core.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.github.terziele.swarz.core.controller.fixtures.SimpleController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PackageControllerScannerTest {

  @Test
  void findControllers_collectFromPackage() throws Exception {
    var sut = new PackageControllerScanner(ClassLoader.getSystemClassLoader());

    var actual = sut.findControllers(SimpleController.class.getPackage().getName());

    assertNotNull(actual);
    assertFalse(actual.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", ".", "...", "asdaseqweqw...."})
  void findControllers_invalidPackage_returnEmptyCollection() throws Exception {
    var sut = new PackageControllerScanner(ClassLoader.getSystemClassLoader());

    var actual = sut.findControllers("");

    assertNotNull(actual);
    assertTrue(actual.isEmpty());
  }
}
