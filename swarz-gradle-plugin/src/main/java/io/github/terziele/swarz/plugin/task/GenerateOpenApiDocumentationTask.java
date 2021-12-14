/* (C)2021 */
package io.github.terziele.swarz.plugin.task;

import com.github.terziele.swarz.core.FileDocumentationStorage;
import com.github.terziele.swarz.core.Swarz;
import com.github.terziele.swarz.core.controller.CompositeControllerScanner;
import com.github.terziele.swarz.core.controller.ConcreteClassControllerScanner;
import com.github.terziele.swarz.core.controller.PackageControllerScanner;
import com.github.terziele.swarz.core.docs.Documentation;
import com.github.terziele.swarz.core.docs.SpringDocContext;
import com.github.terziele.swarz.core.docs.SpringDocDocumentation;
import com.github.terziele.swarz.core.resolvers.JsonViewDefaultViewExclusionModelResolver;
import io.github.terziele.swarz.plugin.classpath.ClassPathScanner;
import io.github.terziele.swarz.plugin.extensions.ApiExtension;
import io.github.terziele.swarz.plugin.extensions.SwarzExtension;
import io.swagger.v3.core.jackson.ModelResolver;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateOpenApiDocumentationTask extends DefaultTask {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(GenerateOpenApiDocumentationTask.class);
  private Project project = getProject();

  @TaskAction
  public void generate() throws Exception {
    var extension =
        (SwarzExtension) project.getExtensions().getByName(SwarzExtension.EXTENSION_NAME);
    var classLoader = ClassPathScanner.scan(project);

    LOGGER.info("Generating OpenAPI 3 documentation...");

    for (ApiExtension api : extension.getApis()) {
      generateApi(api, classLoader);
    }
  }

  private void generateApi(ApiExtension api, ClassLoader classLoader) {
    LOGGER.info("Generating documentation for '{}'...", api.getName());
    var start = System.currentTimeMillis();

    var controllers = getControllers(api, classLoader);
    var additionalModelResolvers = collectAdditionalModelResolvers(api);
    var additionalProperties = getProperties(api);

    LOGGER.debug("API {}. Building Spring application context", api.getName());
    var context =
        SpringDocContext.builder()
            .apiName(api.getName())
            .version(api.getVersion())
            .additionalModelResolvers(additionalModelResolvers)
            .classLoader(classLoader)
            .additionalProperties(additionalProperties)
            .controllers(controllers)
            .build();
    LOGGER.debug("API {}. SpringDoc application context built", api.getName());

    var format = resolveDocumenationFormat(api);

    var docs =
        SpringDocDocumentation.builder()
            .name(api.getName())
            .context(context)
            .format(format)
            .build();

    LOGGER.debug("API {}. Configuring storage", api.getName());

    var storage = createDocumentationStorage(api);

    var swarz = Swarz.builder().docs(docs).storage(storage).build();

    try {
      swarz.generateDocumentation();
    } catch (Exception e) {
      throw new GradleException("Documentation generation failed", e);
    }

    LOGGER.info(
        "API {}. Generation complete in {}ms", api.getName(), System.currentTimeMillis() - start);
  }

  private Properties getProperties(ApiExtension api) {
    LOGGER.debug("API {}. Adding additional properties: {}", api.getName(), api.getProperties());
    var additionalProperties = new Properties();
    additionalProperties.putAll(api.getProperties());
    return additionalProperties;
  }

  private FileDocumentationStorage createDocumentationStorage(ApiExtension api) {
    var outputPath = getOutputPath(api);
    LOGGER.debug("API {}. Output path: {}", api.getName(), outputPath);
    return FileDocumentationStorage.of(outputPath);
  }

  private Documentation.As resolveDocumenationFormat(ApiExtension api) {
    var format = Documentation.As.JSON;
    if (api.getFormat().equalsIgnoreCase("yaml")) {
      format = Documentation.As.YAML;
    }
    LOGGER.debug("API {}. Documentation format is {}", api.getName(), format);
    return format;
  }

  private ArrayList<ModelResolver> collectAdditionalModelResolvers(ApiExtension api) {
    var additionalModelResolvers = new ArrayList<ModelResolver>();
    if (api.isExcludeDefaultJsonViewFields()) {
      LOGGER.debug("API {}, DEFAULT_VIEW_EXCLUSION enabled", api.getName());
      additionalModelResolvers.add(new JsonViewDefaultViewExclusionModelResolver());
    }
    return additionalModelResolvers;
  }

  private Set<Class<?>> getControllers(ApiExtension api, ClassLoader classLoader) {
    var scanner = createControllerScanner(classLoader);
    LOGGER.debug("API {}. Scanning location for controllers.", api.getName());
    var controllers =
        api.getControllersLocations().stream()
            .map(scanner::findControllers)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet());
    LOGGER.debug("API {}. {} controllers were found", api.getName(), controllers.size());
    return controllers;
  }

  private CompositeControllerScanner createControllerScanner(ClassLoader classLoader) {
    try {
      return new CompositeControllerScanner(
          List.of(
              new ConcreteClassControllerScanner(classLoader),
              new PackageControllerScanner(classLoader)));
    } catch (Exception e) {
      throw new GradleException("Unable to create controller scanners", e);
    }
  }

  private String getOutputPath(ApiExtension api) {
    if (StringUtils.isNotBlank(api.getOutputPath())) {
      return api.getOutputPath();
    }
    var apiName = StringUtils.isNotBlank(api.getName()) ? "-" + api.getName() : "";
    var version =
        StringUtils.isNotBlank(api.getVersion()) ? api.getVersion() : project.getVersion();
    return project.getBuildDir()
        + "/"
        + "api/"
        + project.getName()
        + apiName
        + '-'
        + version
        + '.'
        + api.getFormat();
  }
}
