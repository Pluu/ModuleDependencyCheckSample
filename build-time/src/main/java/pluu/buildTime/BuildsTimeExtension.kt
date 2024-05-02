package pluu.buildTime

import org.gradle.api.Project
import org.gradle.api.provider.Property

abstract class BuildsTimeExtension(project: Project) {

    private val objects = project.objects

    /**
     * Enable or disable the plugin.
     */
    val enable: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
}