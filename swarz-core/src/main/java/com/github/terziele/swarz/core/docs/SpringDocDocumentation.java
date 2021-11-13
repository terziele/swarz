/* (C)2021 */
package com.github.terziele.swarz.core.docs;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.api.OpenApiResource;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;

@Builder
@RequiredArgsConstructor
public class SpringDocDocumentation implements Documentation {
  @NonNull private final String name;
  @NonNull private final ApplicationContext context;
  @NonNull private final Documentation.As format;

  @Override
  public @NonNull String getName() {
    return name;
  }

  @Override
  public @NonNull String get() throws RuntimeException {
    try {
      var apiResource = context.getBean(OpenApiResource.class);

      switch (format) {
        case JSON:
          return apiResource.openapiJson(new MockHttpServletRequest(), "swarz");
        case YAML:
          return apiResource.openapiYaml(new MockHttpServletRequest(), "swarz");
        default:
          throw new IllegalArgumentException("Unexpected format: " + format);
      }
    } catch (Exception e) {
      throw new RuntimeException("Unable to generate the OAS3 documentation", e);
    }
  }
}
