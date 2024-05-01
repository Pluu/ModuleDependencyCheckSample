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
            val projectNames = inverseGraph.keys.associateBy { it.path }
            val requestModules = mutableListOf<Project>()

            moduleNames.forEach { name ->
                if (projectNames.containsKey(name)) {
                    requestModules.add(projectNames[name]!!)
                } else {
                    println("$name module is not exist")
                }
            }
            println("===============")
            println("Check modules from ${requestModules.joinToString { "'[${it.path}]'" }} module")

            val result = findAffectedModule(requestModules, inverseGraph)
            if (result.isNotEmpty()) {
                println("> Found ${result.size} modules.")
                result.sortedBy { it.path }.forEach {
                    println(">>> ${it.path}")
                }
            } else {
                println(">>> affected module is not found")
            }

            println("===============")
        }
    }

    private fun findAffectedModule(
        startPoint: List<Project>,
        inverseGraph: Map<Project, Dependency>
    ): Set<Project> {
        val checkedModule = mutableSetOf<Project>()
        val affectedModules = mutableSetOf<Project>()
        val queue = ArrayDeque<Project>()
        queue.addAll(startPoint)

        while (queue.isNotEmpty()) {
            val project = queue.removeFirst()
            if (checkedModule.contains(project) || skipModules.contains(project.name)) {
                continue
            }
            checkedModule.add(project)

            inverseGraph.entries.firstOrNull { entry ->
                project == entry.key
            }?.let { entry ->
                affectedModules.add(entry.key)
                affectedModules.addAll(entry.value.implementation)
                queue.addAll(entry.value.api)
            }
        }

        return affectedModules
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

    companion object {
        private val skipModules = listOf(":fake-lint")
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
        // ex) gradle checkPluu -PmoduleNames=:common
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