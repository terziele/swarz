/* (C)2021 */
package com.github.terziele.swarz.core.custom;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomiser;

@RequiredArgsConstructor
public class ApiVersionCustomizer implements OpenApiCustomiser {
  @NonNull private final String apiName;
  @NonNull private final String version;

  @Override
  public void customise(OpenAPI openApi) {
    openApi.getInfo().title(apiName).version(version);
  }
}
