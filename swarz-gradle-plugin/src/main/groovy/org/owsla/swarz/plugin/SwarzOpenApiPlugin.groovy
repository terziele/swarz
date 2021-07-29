/* (C)2021 */
package org.owsla.swarz.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.owsla.swarz.plugin.extensions.SwarzExtension
import org.owsla.swarz.plugin.task.GenerateDocsTask

class SwarzOpenApiPlugin implements Plugin<Project> {
	@Override
	void apply(Project project) {
		project.extensions.create(SwarzExtension.EXTENSION_NAME, SwarzExtension, project)

		project.task(
				type: GenerateDocsTask,
				dependsOn: 'classes',
				group: 'documentation',
				description: "Generate OpenAPI 3 documentation",
				GenerateDocsTask.TASK_NAME, {
				}) as GenerateDocsTask
	}
}
