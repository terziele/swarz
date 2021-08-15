/* (C)2021 */
package org.owsla.swarz.plugin.extensions

import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class ApiExtension {
	private final Project project

	private String name = "${project.name}"
	private String outputPath = "${project.buildDir}/api/${project.name}-api-${project.version}"
	private Set<String> controllersLocations = new HashSet<>();
	private String version = "${project.version}"

	ApiExtension(Project project) {
		this.project = project
	}

	String getName() {
		this.name
	}

	void setName(String name) {
		this.name = name
	}

	String getOutputPath() {
		return outputPath
	}

	void setOutputPath(String outputPath) {
		this.outputPath = outputPath
	}

	Set<String> getControllersLocations() {
		return controllersLocations
	}

	void setControllersLocations(Set<String> controllersLocations) {
		this.controllersLocations = controllersLocations
	}

	String getVersion() {
		return version
	}

	void setVersion(String version) {
		this.version = version
	}
}
