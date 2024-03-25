package tasks

import org.gradle.api.Plugin
import org.gradle.api.Project

class DependencyCheckPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.registerTask()
    }
}