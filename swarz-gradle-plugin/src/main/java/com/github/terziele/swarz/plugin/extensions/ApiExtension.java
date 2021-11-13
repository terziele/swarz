/* (C)2021 */
package com.github.terziele.swarz.plugin.extensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.gradle.api.Project;

public class ApiExtension {
  private final Project project;
  private String name;
  private String format = "json";
  private String outputPath;
  private Set<String> controllersLocations = new HashSet<>();
  private String version;
  private boolean excludeDefaultJsonViewFields = true;
  private Map<String, Object> properties = new HashMap<>();

  public ApiExtension(Project project) {
    this.project = project;
  }

  public String getName() {
    return name;
  }

  public ApiExtension setName(String name) {
    this.name = name;
    return this;
  }

  public String getFormat() {
    return format;
  }

  public ApiExtension setFormat(String format) {
    this.format = format;
    return this;
  }

  public String getOutputPath() {
    return outputPath;
  }

  public ApiExtension setOutputPath(String outputPath) {
    this.outputPath = outputPath;
    return this;
  }

  public Set<String> getControllersLocations() {
    return controllersLocations;
  }

  public ApiExtension setControllersLocations(Set<String> controllersLocations) {
    this.controllersLocations = controllersLocations;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public ApiExtension setVersion(String version) {
    this.version = version;
    return this;
  }

  public boolean isExcludeDefaultJsonViewFields() {
    return excludeDefaultJsonViewFields;
  }

  public ApiExtension setExcludeDefaultJsonViewFields(boolean excludeDefaultJsonViewFields) {
    this.excludeDefaultJsonViewFields = excludeDefaultJsonViewFields;
    return this;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public ApiExtension setProperties(Map<String, Object> properties) {
    this.properties = properties;
    return this;
  }
}
