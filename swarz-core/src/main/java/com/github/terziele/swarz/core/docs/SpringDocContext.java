/* (C)2021 */
package com.github.terziele.swarz.core.docs;

import static org.apache.commons.lang3.Validate.notNull;

import com.fasterxml.jackson.module.kotlin.KotlinModule;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import java.util.*;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.webmvc.core.SpringDocWebMvcConfiguration;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

@Slf4j
public class SpringDocContext extends GenericWebApplicationContext
    implements AnnotationConfigRegistry {
  private final AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(this);
  private final ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this);

  @Override
  public void register(Class<?> @NonNull ... componentClasses) {
    log.debug("Register component classes: {}", Arrays.asList(componentClasses));
    reader.register(componentClasses);
  }

  @Override
  public void scan(String @NonNull ... basePackages) {
    log.debug("Add packages to scanner: {}", Arrays.asList(basePackages));
    scanner.scan(basePackages);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    @NotNull private ClassLoader classLoader;
    private final List<ModelResolver> additionalModelResolvers = new ArrayList<>();
    @NotNull private List<Class<?>> controllers;
    @NotNull private SpringDocConfigProperties springDocProperties;
    @NotNull private Properties additionalProperties;

    public Builder classLoader(@NonNull ClassLoader classLoader) {
      this.classLoader = classLoader;
      return this;
    }

    public Builder additionalModelResolvers(@NonNull List<ModelResolver> additionalModelResolvers) {
      this.additionalModelResolvers.addAll(additionalModelResolvers);
      return this;
    }

    public Builder controllers(@NonNull Collection<Class<?>> controllers) {
      this.controllers = List.copyOf(controllers);
      return this;
    }

    public Builder springDocProperties(@NonNull SpringDocConfigProperties springDocProperties) {
      this.springDocProperties = springDocProperties;
      return this;
    }

    public Builder additionalProperties(@NonNull Properties additionalProperties) {
      this.additionalProperties = additionalProperties;
      return this;
    }

    public SpringDocContext build() {
      notNull(classLoader, "class loader");
      notNull(additionalModelResolvers, "additional model resolvers");
      notNull(controllers, "controllers");
      notNull(springDocProperties, "springdoc properties");
      notNull(additionalProperties, "additional properties");

      var context = new SpringDocContext();

      context.setClassLoader(classLoader);
      context.register(SpringDocWebMvcConfiguration.class);
      context.register(SpringDocConfiguration.class);
      context.registerBean(
          RequestMappingInfoHandlerMapping.class, requestMappingInfoHandlerMapping());
      context.registerBean(SpringDocConfigProperties.class, () -> springDocProperties);

      Json.mapper().registerModule(new KotlinModule());

      // SpringDoc configuration is only enabled if we have that property enabled.
      additionalProperties.setProperty("springdoc.api-docs.enabled", "true");
      var env = context.getEnvironment();
      env.getPropertySources()
          .addFirst(
              new PropertiesPropertySource("swarz-additional-properties", additionalProperties));

      var beanFactory = context.getBeanFactory();

      log.debug("Register controllers: {}", controllers);
      for (var controller : controllers) {
        beanFactory.registerSingleton(controller.getCanonicalName(), Mockito.mock(controller));
      }

      var resolvers =
          Objects.requireNonNullElse(additionalModelResolvers, List.<Class<ModelResolver>>of());

      log.debug("Register additional ModelResolvers: {}", resolvers);
      for (var resolver : resolvers) {
        beanFactory.registerSingleton(resolver.getClass().getCanonicalName(), resolver);
      }

      context.refresh();
      return context;
    }

    private static Supplier<RequestMappingInfoHandlerMapping> requestMappingInfoHandlerMapping() {
      return () -> {
        var configurationSupport = new WebMvcConfigurationSupport();
        return configurationSupport.requestMappingHandlerMapping(
            configurationSupport.mvcContentNegotiationManager(),
            configurationSupport.mvcConversionService(),
            configurationSupport.mvcResourceUrlProvider());
      };
    }
  }
}
