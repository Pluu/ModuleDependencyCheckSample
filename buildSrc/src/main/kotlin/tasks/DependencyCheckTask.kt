package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

abstract class DependencyCheckTask : DefaultTask() {

    @get:Input
    abstract var moduleNames: List<String>

    @TaskAction
    fun report() {
        val inverseGraph =
            inverseGenerateGraph(project.rootProject) // Assuming rootProject is defined

        println("[All graph]")
        inverseGraph.forEach { (key, value) ->
            println(key.name)
            println("> ${value.joinToString(", ")}")
        }

        // Find module
        if (moduleNames.isNotEmpty()) {
            val findModule = moduleNames.first()

            println()
            println("===============")
            println("Check modules from '[$findModule]' module")
            val checkModule = mutableSetOf<Project>()

            inverseGraph.entries.firstOrNull { entry ->
                findModule == entry.key.name
            }?.let { entry ->
                checkModule.add(entry.key)
                checkModule.addAll(entry.value)
            }

            checkModule.sortedBy { it.name }.forEach {
                println("> $it")
            }
            println("===============")
            println()
        }

    }

    private fun inverseGenerateGraph(rootProject: Project): Map<Project, List<Project>> {
        val queue = ArrayDeque<Project>()
        queue.add(rootProject)

        val projects = mutableSetOf<Project>()
        val dependencies = mutableMapOf<Project, MutableList<Project>>()

        while (queue.isNotEmpty()) {
            val project = queue.removeFirst()
            queue.addAll(project.childProjects.values)
            projects.add(project)

            project.configurations.forEach { config ->
                config.dependencies.filterIsInstance<ProjectDependency>()
                    .map { it.dependencyProject }
                    .forEach { dependency ->
                        projects.add(dependency)
                        dependencies.putIfAbsent(dependency, mutableListOf())
                        val configName = config.name.lowercase()
                        if (configName.endsWith("implementation")) {
                            dependencies[dependency]?.add(project)
                        } else if (configName.endsWith("api")) {
                            dependencies[dependency]?.add(project)
                        }
                    }
            }
        }
        return dependencies.filterValues { it.isNotEmpty() }
    }
}

internal fun Project.registerTask() {
    project.afterEvaluate {
        project.tasks.register<DependencyCheckTask>("checkPluu") {
            moduleNames = (properties["moduleNames"] as String)
                .split(",")
                .map { it.trim() }
        }

        project.tasks.register<DependencyCheckTask>("checkPluuFixed") {
            moduleNames = listOf("common")
        }
    }
}