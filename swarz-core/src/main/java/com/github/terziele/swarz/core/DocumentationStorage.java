/* (C)2021 */
package com.github.terziele.swarz.core;

import java.io.IOException;
import lombok.NonNull;

public interface DocumentationStorage extends AutoCloseable {
  void save(@NonNull String docs) throws IOException;
}
