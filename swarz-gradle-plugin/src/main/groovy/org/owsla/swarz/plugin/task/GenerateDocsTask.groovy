/* (C)2021 */
package org.owsla.swarz.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.owsla.swarz.plugin.classpath.ClassPathScanner
import org.owsla.swarz.plugin.extensions.SwarzExtension

class GenerateDocsTask extends DefaultTask {
	static final String TASK_NAME = "openApiDocumentation"

	@TaskAction
	void generateDocumentation() {
		def extension = project.extensions.getByName(SwarzExtension.EXTENSION_NAME) as SwarzExtension
		def classLoader = ClassPathScanner.scan(project)

		//TODO
	}
}
