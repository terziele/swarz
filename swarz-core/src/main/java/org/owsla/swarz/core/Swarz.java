/* (C)2021 */
package org.owsla.swarz.core;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.owsla.swarz.core.docs.Documentation;

@Slf4j
@Builder
@RequiredArgsConstructor
public class Swarz {
  @NonNull private final Documentation docs;
  @NonNull private final DocumentationStorage storage;

  @SneakyThrows
  public void generateDocumentation() {
    log.info("Generating OpenAPI documentation.");
    var start = System.currentTimeMillis();
    var generated = docs.get();
    log.info("Documentation generation completed in {}", System.currentTimeMillis() - start);

    log.info("Saving documentation '{}' to storage.", docs.getName());
    start = System.currentTimeMillis();
    storage.save(generated);
    log.info("Documentation saving completed in {}", System.currentTimeMillis() - start);
  }
}
