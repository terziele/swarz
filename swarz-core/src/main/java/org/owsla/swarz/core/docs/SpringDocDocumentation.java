/* (C)2021 */
package org.owsla.swarz.core.docs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.api.OpenApiResource;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;

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
      return context
          .getBean(OpenApiResource.class)
          .openapiJson(new MockHttpServletRequest(), "test");
    } catch (Exception e) {
      throw new RuntimeException("Unable to generate the OAS3 documentation", e);
    }
  }
}
