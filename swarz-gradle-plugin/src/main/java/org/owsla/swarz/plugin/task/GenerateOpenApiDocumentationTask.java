/* (C)2021 */
package org.owsla.swarz.plugin.task;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.owsla.swarz.core.FileDocumentationStorage;
import org.owsla.swarz.core.Swarz;
import org.owsla.swarz.core.controller.CompositeControllerScanner;
import org.owsla.swarz.core.controller.ConcreteClassControllerScanner;
import org.owsla.swarz.core.controller.PackageControllerScanner;
import org.owsla.swarz.core.docs.Documentation;
import org.owsla.swarz.core.docs.SpringDocContext;
import org.owsla.swarz.core.docs.SpringDocDocumentation;
import org.owsla.swarz.plugin.classpath.ClassPathScanner;
import org.owsla.swarz.plugin.extensions.ApiExtension;
import org.owsla.swarz.plugin.extensions.SwarzExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.SpringDocConfigProperties;

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

    var scanner = createControllerScanner(classLoader);
    LOGGER.debug("Scanning location for controllers.");
    var controllers =
        api.getControllersLocations().stream()
            .map(scanner::findControllers)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet());
    LOGGER.debug("{} controllers were found", controllers.size());

    LOGGER.debug("Building Spring application context");
    var context =
        SpringDocContext.builder()
            .additionalModelResolvers(List.of())
            .classLoader(classLoader)
            .additionalProperties(new Properties())
            .controllers(controllers)
            .springDocProperties(new SpringDocConfigProperties())
            .build();
    LOGGER.debug("SpringDoc application context built");

    var docs =
        SpringDocDocumentation.builder()
            .name(api.getName())
            .context(context)
            .format(Documentation.As.JSON)
            .build();

    LOGGER.debug("Configuring storage");

    var outputPath = getOutputPath(api);
    LOGGER.debug("Output path: {}", outputPath);
    var storage = FileDocumentationStorage.of(outputPath);

    var swarz = Swarz.builder().docs(docs).storage(storage).build();

    try {
      swarz.generateDocumentation();
    } catch (Exception e) {
      throw new GradleException("Documentation generation failed", e);
    }

    LOGGER.info("Generation complete in {}ms", System.currentTimeMillis() - start);
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
        + api.getExtension();
  }
}
