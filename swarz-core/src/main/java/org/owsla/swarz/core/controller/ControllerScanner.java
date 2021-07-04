package org.owsla.swarz.core.controller;

import java.util.Set;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RestController;

public interface ControllerScanner {

  /**
   * Scan classpath for classes annotated with {@link RestController}
   * @param source package name or class qualified name
   * @return found controllers. If none - returns empty set.
   */
  @NonNull
  Set<Class<?>> findControllers(@NonNull String source);

}
