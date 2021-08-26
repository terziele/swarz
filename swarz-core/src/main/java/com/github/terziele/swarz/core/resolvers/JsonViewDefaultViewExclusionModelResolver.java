/* (C)2021 */
package com.github.terziele.swarz.core.resolvers;

import static com.fasterxml.jackson.databind.MapperFeature.DEFAULT_VIEW_INCLUSION;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.NotImplementedException;

/** Excludes properties not included in specified {@link JsonView} */
public class JsonViewDefaultViewExclusionModelResolver extends ModelResolver {

  public JsonViewDefaultViewExclusionModelResolver() {
    super(Json.mapper().disable(DEFAULT_VIEW_INCLUSION));
  }

  @Override
  protected boolean hiddenByJsonView(Annotation[] annotations, AnnotatedType type) {
    if (type == null || type.getJsonViewAnnotation() == null) {
      return false;
    }

    var jsonViews =
        Optional.ofNullable(annotations).stream()
            .flatMap(Arrays::stream)
            .filter(JsonView.class::isInstance)
            .collect(Collectors.toUnmodifiableList());

    if (jsonViews.isEmpty()) {
      // if there are no json view specified, then hide it
      return true;
    }

    // todo
    throw new NotImplementedException("TODO");
  }
}
