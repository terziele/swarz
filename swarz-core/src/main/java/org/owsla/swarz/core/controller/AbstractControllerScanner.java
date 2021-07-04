package org.owsla.swarz.core.controller;

import java.lang.annotation.Annotation;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RestController;

abstract class AbstractControllerScanner implements ControllerScanner {

  protected final ClassLoader classLoader;
  private final Class<? extends Annotation> restControllerAnnotation;

  @SuppressWarnings("unchecked")
  protected AbstractControllerScanner(@NonNull ClassLoader classLoader)
      throws Exception {
    this.classLoader = classLoader;
    this.restControllerAnnotation = (Class<? extends Annotation>) Class.forName(
        RestController.class.getCanonicalName(),
        false,
        classLoader);
  }

  protected boolean isController(@NonNull Class<?> clazz) {
    return clazz.isAnnotationPresent(restControllerAnnotation);
  }

}
