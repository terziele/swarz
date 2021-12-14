/* (C)2021 */
package io.github.terziele.swarz.plugin.extensions;

import groovy.lang.Closure;
import java.util.ArrayList;
import java.util.List;
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

  public void setApis(List<ApiExtension> apis) {
    this.apis = apis;
  }

  public void api(Closure<ApiExtension> apiExtensionClosure) {
    var api = (ApiExtension) project.configure(new ApiExtension(project), apiExtensionClosure);
    apis.add(api);
  }
}
