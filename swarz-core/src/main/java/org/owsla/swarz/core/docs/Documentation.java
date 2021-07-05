/* (C)2021 */
package org.owsla.swarz.core.docs;

import lombok.NonNull;

public interface Documentation {

  @NonNull
  String getName();

  @NonNull
  String get() throws RuntimeException;

  enum As {
    JSON,
    YAML
  }
}
