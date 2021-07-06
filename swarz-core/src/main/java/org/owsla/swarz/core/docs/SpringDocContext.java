/* (C)2021 */
package org.owsla.swarz.core.docs;

import com.fasterxml.jackson.module.kotlin.KotlinModule;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
    @NonNull private ClassLoader classLoader;
    @NonNull private List<ModelResolver> additionalModelResolvers;
    @NonNull private List<Class<?>> controllers;
    @NonNull private SpringDocConfigProperties springDocProperties;
    @NonNull private Properties additionalProperties;

    public Builder classLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
      return this;
    }

    public Builder additionalModelResolvers(List<ModelResolver> additionalModelResolvers) {
      this.additionalModelResolvers = additionalModelResolvers;
      return this;
    }

    public Builder controllers(List<Class<?>> controllers) {
      this.controllers = controllers;
      return this;
    }

    public Builder springDocProperties(SpringDocConfigProperties springDocProperties) {
      this.springDocProperties = springDocProperties;
      return this;
    }

    public Builder additionalProperties(Properties additionalProperties) {
      this.additionalProperties = additionalProperties;
      return this;
    }

    public SpringDocContext build() {
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

      for (var controller : controllers) {
        context.registerBean(controller, createController(controller));
      }

      var resolvers =
          Objects.requireNonNullElse(additionalModelResolvers, List.<ModelResolver>of());
      for (var resolver : resolvers) {
        Supplier<ModelResolver> resolverSupplier = () -> resolver;
        context.registerBean(resolver.getClass(), resolverSupplier);
      }

      context.refresh();
      return context;
    }

    private <T> Supplier<T> createController(Class<T> clazz) {
      return () -> Mockito.mock(clazz);
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
