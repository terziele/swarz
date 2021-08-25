/* (C)2021 */
package com.github.terziele.swarz.plugin;

import com.github.terziele.swarz.plugin.extensions.SwarzExtension;
import com.github.terziele.swarz.plugin.task.GenerateOpenApiDocumentationTask;
import java.util.Map;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SwarzOpenApiPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getExtensions().create(SwarzExtension.EXTENSION_NAME, SwarzExtension.class, project);

    var taskConfig =
        Map.of(
            "type", GenerateOpenApiDocumentationTask.class,
            "dependsOn", "classes",
            "group", "documentation",
            "description", "Generate OpenAPI3 documentation");

    project.task(taskConfig, "openApiDocumentation");
  }
}
