/* (C)2021 */
package org.owsla.swarz.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.owsla.swarz.core.Swarz
import org.owsla.swarz.core.controller.CompositeControllerScanner
import org.owsla.swarz.core.controller.ConcreteClassControllerScanner
import org.owsla.swarz.core.controller.PackageControllerScanner
import org.owsla.swarz.core.docs.Documentation
import org.owsla.swarz.core.docs.SpringDocContext
import org.owsla.swarz.core.docs.SpringDocDocumentation
import org.owsla.swarz.plugin.classpath.ClassPathScanner
import org.owsla.swarz.plugin.extensions.SwarzExtension
import org.springdoc.core.SpringDocConfigProperties

class GenerateDocsTask extends DefaultTask {
	static final String TASK_NAME = "openApiDocumentation"

	@TaskAction
	void generateDocumentation() {
		def start = System.currentTimeMillis()
		def extension = project.extensions.getByName(SwarzExtension.EXTENSION_NAME) as SwarzExtension
		def classLoader = ClassPathScanner.scan(project)

		logger.info("Generating OpenAPI 3 documentation...")

		extension.apis.each {api ->
			logger.info("Generating documentation for '{}'...", api.name)

			def scanner = new CompositeControllerScanner([
				new ConcreteClassControllerScanner(classLoader),
				new PackageControllerScanner(classLoader)
			])
			logger.debug("Scanning location for controllers.")
			def controllers = api.controllersLocations
					.collect {loc -> scanner.findControllers(loc)}
					.flatten()
			logger.debug("{} controllers were found", controllers.size())

			logger.debug("Building Spring application context")
			def context = SpringDocContext.builder()
					.additionalModelResolvers(List.of())
					.classLoader(classLoader)
					.additionalProperties(new Properties())
					.controllers(controllers as List<Class<?>>)
					.springDocProperties(new SpringDocConfigProperties())
					.build()
			logger.debug("SpringDoc application context built")

			def docs = SpringDocDocumentation.builder()
					.name(api.name)
					.context(context)
					.format(Documentation.As.JSON)
					.build()

			logger.debug("Configuring storage")
			//todo
			def storage = null

			def swarz = Swarz.builder()
					.docs(docs)
					.storage(storage)
					.documentationFilename(api.filename)
					.build()

			try {
				swarz.generateDocumentation()
			} catch(Exception e) {
				throw new GradleException("Documentation generation failed", e);
			}

			logger.info("Generation complete in {}ms", start - System.currentTimeMillis())
		}
	}
}
