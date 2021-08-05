/* (C)2021 */
package org.owsla.swarz.plugin.extensions;

import groovy.transform.ToString
import org.gradle.api.Project

@ToString(includeNames = true)
class SwarzExtension {
	public static final String EXTENSION_NAME = "swarz"
	private final Project project
	private final List<ApiExtension> apis = new ArrayList<>()

	List<ApiExtension> getApis() {
		return apis.clone();
	}

	void api(Closure<ApiExtension> apiExtensionClosure) {
		apis.add(project.configure(new ApiExtension(project), apiExtensionClosure) as ApiExtension)
	}
}
