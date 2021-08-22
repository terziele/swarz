/* (C)2021 */
package org.owsla.swarz.plugin.classpath;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.UnknownConfigurationException;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.internal.impldep.org.jetbrains.annotations.NotNull;

public final class ClassPathScanner {
  private static final ClassLoader DEFAULT_CLASS_LOADER = ClassPathScanner.class.getClassLoader();

  private static final Set<String> SCANNED_CONFIGURATIONS =
      Set.of("compileClasspath", "runtimeClasspath", "runtime");

  public static ClassLoader scan(@NotNull Project project) {
    var files = scanConfigurations(project.getConfigurations());
    var java = getJavaClassesAndResoures(project);
    files.addAll(java);

    return new URLClassLoader(files.toArray(new URL[0]), DEFAULT_CLASS_LOADER);
  }

  private static Set<URL> getJavaClassesAndResoures(Project project) {
    var java = project.getExtensions().getByType(JavaPluginExtension.class);
    var main = java.getSourceSets().getByName("main").getOutput();
    var result =
        main.getClassesDirs().getFiles().stream().map(convertToUrl()).collect(Collectors.toSet());

    result.add(convertToUrl().apply(main.getResourcesDir()));

    return result;
  }

  private static List<URL> scanConfigurations(@NotNull ConfigurationContainer configs) {
    return SCANNED_CONFIGURATIONS.stream()
        .map(
            name -> {
              try {
                return configs.getByName(name);
              } catch (UnknownConfigurationException e) {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .map(Configuration::resolve)
        .flatMap(Collection::stream)
        .map(convertToUrl())
        .collect(Collectors.toList());
  }

  private static Function<File, URL> convertToUrl() {
    return file -> {
      try {
        return file.toURI().toURL();
      } catch (MalformedURLException e) {
        throw new GradleException("Unable to convert file to URL",e);
      }
    };
  }
}
