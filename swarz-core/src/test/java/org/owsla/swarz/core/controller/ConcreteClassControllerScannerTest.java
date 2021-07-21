/* (C)2021 */
package org.owsla.swarz.core.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.owsla.swarz.core.controller.fixtures.SimpleClass;
import org.owsla.swarz.core.controller.fixtures.SimpleController;
import org.springframework.web.bind.annotation.RestController;

class ConcreteClassControllerScannerTest {
  private final ConcreteClassControllerScanner sut = createSut();

  @Test
  void test_public_inner_concrete_controller_found() throws Exception {
    var actual = sut.findControllers(PublicInnerConcreteController.class.getTypeName());

    assertNotNull(actual);
    assertEquals(1, actual.size());
    assertEquals(Set.of(PublicInnerConcreteController.class), actual);
  }

  @Test
  void test_public_concrete_controller_found() throws Exception {
    var actual = sut.findControllers(SimpleController.class.getTypeName());

    assertNotNull(actual);
    assertEquals(1, actual.size());
    assertEquals(Set.of(SimpleController.class), actual);
  }

  @Test
  void test_public_concrete_class_not_found() throws Exception {
    var actual = sut.findControllers(SimpleClass.class.getTypeName());

    assertNotNull(actual);
    assertEquals(0, actual.size());
  }

  @SneakyThrows
  private static ConcreteClassControllerScanner createSut() {
    return new ConcreteClassControllerScanner(ClassLoader.getSystemClassLoader());
  }

  @RestController
  public static class PublicInnerConcreteController {}
}
