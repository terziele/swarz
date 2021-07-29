/* (C)2021 */
package org.owsla.swarz.plugin.classpath

import org.gradle.api.Project
import org.gradle.internal.impldep.org.jetbrains.annotations.NotNull

final class ClassPathScanner {

	static ClassLoader scan(@NotNull Project project) {
		def urls = []
		def classpaths = [
			project.configurations.compileClasspath.resolve()
		]
		if (project.configurations.hasProperty('runtimeClasspath')) {
			classpaths += project.configurations.runtimeClasspath.resolve()
		} else {
			classpaths += project.configurations.runtime.resolve()
		}

		classpaths.flatten().each {
			urls += it.toURI().toURL()
		}

		if (project.sourceSets.main.output.hasProperty('classesDirs')) {
			project.sourceSets.main.output.classesDirs.each {
				if (it.exists()) {
					urls += it.toURI().toURL()
				}
			}
		} else {
			urls += project.sourceSets.main.output.classesDir.toURI().toURL()
		}

		urls += project.sourceSets.main.output.resourcesDir.toURI().toURL()

		return new URLClassLoader(urls as URL[], getClass().getClassLoader())
	}
}
