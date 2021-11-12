/* (C)2021 */
package com.github.terziele.swarz.plugin.extensions;

import java.util.HashSet;
import java.util.Set;
import org.gradle.api.Project;

public class ApiExtension {
  private final Project project;
  private String name;
  private String extension = "json";
  private String outputPath;
  private Set<String> controllersLocations = new HashSet<>();
  private String version;
  private boolean defaultJsonViewExclusion = true;

  public ApiExtension(Project project) {
    this.project = project;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean getDefaultJsonViewExclusion() {
    return defaultJsonViewExclusion;
  }

  public void setDefaultJsonViewExclusion(boolean jsonViewExclusion) {
    this.defaultJsonViewExclusion = jsonViewExclusion;
  }

  public String getOutputPath() {
    return outputPath;
  }

  public void setOutputPath(String outputPath) {
    this.outputPath = outputPath;
  }

  public Set<String> getControllersLocations() {
    return controllersLocations;
  }

  public void setControllersLocations(Set<String> controllersLocations) {
    this.controllersLocations = controllersLocations;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }
}
