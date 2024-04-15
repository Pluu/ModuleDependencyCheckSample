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
            println(key.path)
            println(">>> implementation: ${value.implementation.displayName()}")
            println(">>> api: ${value.api.displayName()}")
            println()
        }

        // Find module
        if (moduleNames.isNotEmpty()) {
            val findModule = moduleNames.first()

            val findProject = inverseGraph.keys.firstOrNull {
                findModule == it.path
            }
            if (findProject == null) {
                println("$findModule module is not exist")
                return
            }

            println("===============")
            println("Check modules from '[$findModule]' module")

            val result = findAffectedModule(findProject, inverseGraph)
            if (result.isNotEmpty()) {
                result.sortedBy { it.path }.forEach {
                    println(">>> ${it.path}")
                }
            } else {
                println(">>> $findModule is not found")
            }

            println("===============")
        }
    }

    private fun findAffectedModule(
        startPoint: Project,
        inverseGraph: Map<Project, Dependency>
    ): Set<Project> {
        val checkModule = mutableSetOf<Project>()
        val queue = ArrayDeque<Project>()
        queue.add(startPoint)

        while (queue.isNotEmpty()) {
            val project = queue.removeFirst()
            inverseGraph.entries.firstOrNull { entry ->
                project == entry.key
            }?.let { entry ->
                checkModule.add(entry.key)
                checkModule.addAll(entry.value.implementation)
                queue.addAll(entry.value.api)
            }
        }

        return checkModule
    }

    private fun inverseGenerateGraph(rootProject: Project): Map<Project, Dependency> {
        val queue = ArrayDeque<Project>()
        queue.add(rootProject)

        val projects = mutableSetOf<Project>()
        val dependencies = mutableMapOf<Project, Dependency>()

        while (queue.isNotEmpty()) {
            val project = queue.removeFirst()
            queue.addAll(project.childProjects.values)
            projects.add(project)

            project.configurations.forEach { config ->
                config.dependencies.filterIsInstance<ProjectDependency>()
                    .map { it.dependencyProject }
                    .forEach { dependency ->
                        projects.add(dependency)
                        dependencies.putIfAbsent(dependency, Dependency())
                        val configName = config.name.lowercase()
                        if (configName.endsWith("implementation")) {
                            dependencies[dependency]?.implementation?.add(project)
                        } else if (configName.endsWith("api")) {
                            dependencies[dependency]?.api?.add(project)
                        }
                    }
            }
        }
        return dependencies.filterValues { it.isNotEmpty() }
    }
}

internal data class Dependency(
    val implementation: MutableList<Project> = mutableListOf(),
    val api: MutableList<Project> = mutableListOf(),
) {
    fun dependencies(): List<Project> = implementation + api

    fun isNotEmpty(): Boolean = implementation.isNotEmpty() || api.isNotEmpty()
}

internal fun List<Project>.displayName(): String =
    joinToString(", ") { it.path }.ifEmpty { "Empty" }

internal fun Project.registerTask() {
    project.afterEvaluate {
        project.tasks.register<DependencyCheckTask>("checkPluu") {
            moduleNames = (properties["moduleNames"] as String)
                .split(",")
                .map { it.trim() }
        }

        project.tasks.register<DependencyCheckTask>("checkPluuFixed") {
            moduleNames = listOf(":common")
        }
    }
}