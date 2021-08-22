/* (C)2021 */
package org.owsla.swarz.core.controller;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("UnstableApiUsage")
public class PackageControllerScanner extends AbstractControllerScanner {

  private static final Pattern PACKAGE_PATTERN =
      Pattern.compile("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_]$");

  private final ClassPath classPath;

  public PackageControllerScanner(@NonNull ClassLoader classLoader) throws Exception {
    super(classLoader);
    this.classPath = ClassPath.from(classLoader);
  }

  /**
   * @param source package name
   * @return found controllers in package or empty set.
   * @inheritDoc
   */
  @Override
  public @NonNull Set<Class<?>> findControllers(@NonNull String source) {
    if (isNotPackage(source)) {
      log.debug("Scanner does not support this kind of source: {}", source);
      return Set.of();
    }
    Set<Class<?>> controllers =
        classPath.getTopLevelClassesRecursive(source).stream()
            .map(ClassInfo::load)
            .filter(super::isController)
            .collect(Collectors.toUnmodifiableSet());

    log.info("Found {} controllers in package '{}'", controllers.size(), source);
    return controllers;
  }

  private boolean isNotPackage(String source) {
    return !PACKAGE_PATTERN.matcher(source).matches();
  }
}
