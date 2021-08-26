/* (C)2021 */
package com.github.terziele.swarz.core.controller;

import java.util.Set;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcreteClassControllerScanner extends AbstractControllerScanner {

  private static final Pattern CLASS_PATTERN =
      Pattern.compile("([a-z0-9]*\\.)+([A-Z0-9]\\w*\\$?)+");

  public ConcreteClassControllerScanner(@NonNull ClassLoader classLoader) throws Exception {
    super(classLoader);
  }

  @Override
  public @NonNull Set<Class<?>> findControllers(@NonNull String source) {
    if (isNotConcreteClass(source)) {
      log.debug("Scanner does not supports this kind of source: {}", source);
      return Set.of();
    }
    try {
      var found = Class.forName(source, false, super.classLoader);
      if (isController(found)) {
        return Set.of(found);
      }
      log.debug("{} is not a controller class", source);
    } catch (ClassNotFoundException e) {
      log.error("Failed to load controller: {}", source, e);
    }
    return Set.of();
  }

  private boolean isNotConcreteClass(String source) {
    return !CLASS_PATTERN.matcher(source).matches();
  }
}
