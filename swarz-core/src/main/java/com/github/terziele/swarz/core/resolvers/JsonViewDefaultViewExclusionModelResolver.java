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
    if (type == null) {
      log.debug("Passed type is null. Skipping property filtration");
      return false;
    }
    if (type.getJsonViewAnnotation() == null) {
      log.debug(
          "Type '{}' doesn't have any JsonView. Skipping property filtration.", type.getName());
      return false;
    }

    var presentingViews =
        Optional.ofNullable(annotations).stream()
            .flatMap(Arrays::stream)
            .filter(JsonView.class::isInstance)
            .map(JsonView.class::cast)
            .map(JsonView::value)
            .flatMap(Arrays::stream)
            .collect(Collectors.toUnmodifiableSet());

    log.debug(
        "Property '{}#{}'. Expecting JsonViews: {}",
        type.getType().getTypeName(),
        type.getPropertyName(),
        presentingViews);

    if (presentingViews.isEmpty()) {
      log.debug(
          "Property '{}#{}'. No JsonView found but expected. Hiding...",
          type.getType().getTypeName(),
          type.getPropertyName());
      return true;
    }

    for (var expected : type.getJsonViewAnnotation().value()) {
      for (var presentView : presentingViews) {
        // check if presenting view matches to expected
        if (expected.equals(presentView) || presentView.isAssignableFrom(expected)) {
          log.debug(
              "Property '{}#{}' has expected JsonView: {}",
              type.getType().getTypeName(),
              type.getPropertyName(),
              presentView);
          return false;
        }
      }
    }

    log.debug(
        "Property '{}' does not match to any expected views. Hiding it.",
        type.getType().getTypeName());
    return true;
  }
}
