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
import lombok.extern.slf4j.Slf4j;

/**
 * Excludes properties not included in specified {@link JsonView} Provides a support for {@link
 * com.fasterxml.jackson.databind.MapperFeature#DEFAULT_VIEW_INCLUSION}
 */
@Slf4j
public class JsonViewDefaultViewExclusionModelResolver extends ModelResolver {

  public JsonViewDefaultViewExclusionModelResolver() {
    super(Json.mapper().disable(DEFAULT_VIEW_INCLUSION));
  }

  @Override
  protected boolean hiddenByJsonView(Annotation[] annotations, AnnotatedType type) {
    if (type == null || type.getJsonViewAnnotation() == null) {
      log.debug("Type '{}' doesn't have any JsonView. Skipping property filtration.", type);
      return false;
    }

    var expectedViews =
        Optional.ofNullable(annotations).stream()
            .flatMap(Arrays::stream)
            .filter(JsonView.class::isInstance)
            .map(JsonView.class::cast)
            .map(JsonView::value)
            .flatMap(Arrays::stream)
            .collect(Collectors.toUnmodifiableSet());

    log.debug(
        "Property '{}#{}'. Expecting JsonViews: {}",
        type.getName(),
        type.getPropertyName(),
        expectedViews);

    if (expectedViews.isEmpty()) {
      // if there are no json view specified, then hide it
      log.debug(
          "Property '{}#{}' is expected to have some JsonView, but there is none. Hiding it.",
          type.getName(),
          type.getPropertyName());
      return true;
    }

    for (var presentingView : type.getJsonViewAnnotation().value()) {
      for (var expectedView : expectedViews) {
        // check if presenting view matches to expected
        if (presentingView.equals(expectedView) || presentingView.isAssignableFrom(expectedView)) {
          log.debug(
              "Property '{}#{}' has expected JsonView: {}",
              type.getName(),
              type.getPropertyName(),
              expectedView);
          return false;
        }
      }
    }

    log.debug(
        "Property '{}#{}' does not match to any expected views.",
        type.getName(),
        type.getPropertyName());
    return true;
  }
}
