/* (C)2021 */
package org.owsla.swarz.core.controller;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeControllerScanner implements ControllerScanner {
  @NonNull private final List<ControllerScanner> scanners;

  @Override
  public @NonNull Set<Class<?>> findControllers(@NonNull String source) {
    return scanners.stream()
        .map(scanner -> scanner.findControllers(source))
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableSet());
  }
}
