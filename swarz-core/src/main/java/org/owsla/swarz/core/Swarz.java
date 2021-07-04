package org.owsla.swarz.core;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@RequiredArgsConstructor
public class Swarz {
  @NonNull
  private final Documentation docs;
  @NonNull
  private final DocumentationStorage storage;
  @NonNull
  private final String documentationFilename;

  public void generateDocumentation() {
    log.info("Generating OpenAPI documentation.");
    var start = System.currentTimeMillis();
    var generated = docs.get();
    log.info("Documentation generation completed in {}", System.currentTimeMillis() - start);

    log.info("Saving documentation '{}' to storage as '{}'", docs.getName(), documentationFilename);
    start = System.currentTimeMillis();
    storage.save(generated, documentationFilename);
    log.info("Documentation saving completed in {}", System.currentTimeMillis() - start);
  }

}
