/* (C)2021 */
package org.owsla.swarz.plugin.extensions;

import groovy.lang.Closure;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.Project;

public class SwarzExtension {
  public static final String EXTENSION_NAME = "swarz";
  private final Project project;
  private List<ApiExtension> apis = new ArrayList<>();

  public SwarzExtension(Project project) {
    this.project = project;
  }

  public List<ApiExtension> getApis() {
    return List.copyOf(apis);
  }

  public void api(Closure<ApiExtension> apiExtensionClosure) {
    apis.add(
        DefaultGroovyMethods.asType(
            project.configure(new ApiExtension(project), apiExtensionClosure), ApiExtension.class));
  }

  public void setApis(List<ApiExtension> apis) {
    this.apis = apis;
  }
}
