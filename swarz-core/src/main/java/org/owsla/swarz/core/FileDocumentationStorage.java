/* (C)2021 */
package org.owsla.swarz.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FileDocumentationStorage implements DocumentationStorage {
  private final OutputStream output;

  @Override
  public void save(@NonNull String docs) throws IOException {
    var docsBytes = docs.getBytes(StandardCharsets.UTF_8);
    try (var o = output) {
      o.write(docsBytes);
    }
  }

  @Override
  public void close() throws Exception {
    output.close();
  }

  @SneakyThrows
  public static FileDocumentationStorage of(@NonNull String outputFilePath) {
    var path = Paths.get(outputFilePath);
    var fileName = path.getFileName();

    var parentDir = path.getParent().toFile();
    if (!parentDir.exists()) {
      log.debug("'{}' directory does not exists. Create directory.", parentDir);
      var dirCreated = parentDir.mkdirs();
      log.debug("'{}' directory created successfully: {}", parentDir, dirCreated);
    }
    return new FileDocumentationStorage(new FileOutputStream(path.toFile()));
  }
}
