/* (C)2021 */
package org.owsla.swarz.core;

import lombok.NonNull;

public interface DocumentationStorage {
  void save(@NonNull String docs, @NonNull String filename);
}
