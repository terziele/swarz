/* (C)2021 */
package com.github.terziele.swarz.core;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileDocumentationStorageTest {

  @Test
  void test_of(@TempDir Path path) throws Exception {
    try (var sut = FileDocumentationStorage.of(path + "/testfile.json")) {
      sut.save("test");
    }

    var actual = Files.readString(Path.of(path.toString(), "testfile.json"));
    assertEquals("test", actual);
  }
}
