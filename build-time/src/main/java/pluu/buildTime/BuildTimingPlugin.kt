package pluu.buildTime

import org.gradle.StartParameter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.provider.Provider
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.internal.buildevents.BuildStartedTime
import org.gradle.invocation.DefaultGradle
import org.gradle.kotlin.dsl.create
import pluu.buildTime.lifecycle.ConfigurationPhaseObserver
import javax.inject.Inject

///////////////////////////////////////////////////////////////////////////
// Origin : https://github.com/Automattic/measure-builds-gradle-plugin
///////////////////////////////////////////////////////////////////////////

class BuildTimingPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry,
    private val flowScope: FlowScope,
    private val flowProviders: FlowProviders,
) : Plugin<Project> {
    override fun apply(target: Project) {
        val buildInitiatedTime =
            (target.gradle as DefaultGradle).services[BuildStartedTime::class.java].startTime
        val extension = target.extensions.create<BuildsTimeExtension>("pluuTime")

        target.afterEvaluate {
            if (extension.enable.get() == true) {
                val configurationProvider = project.providers.of(
                    ConfigurationPhaseObserver::class.java
                ) { }
                ConfigurationPhaseObserver.init()
                prepareBuildFinishedAction(
                    project.gradle.startParameter,
                    buildInitiatedTime,
                    configurationProvider
                )
            }
        }

        registerBuildFinishActions(target)
    }

    private fun prepareBuildFinishedAction(
        startParameter: StartParameter,
        buildInitiatedTime: Long,
        configurationPhaseObserver: Provider<Boolean>
    ) {
        flowScope.always(
            BuildFinishedFlowAction::class.java
        ) {
            parameters.apply {
                this.buildWorkResult.set(flowProviders.buildWorkResult)
                this.initiationTime.set(buildInitiatedTime)
                this.configurationPhaseExecuted.set(configurationPhaseObserver)
                this.startParameter.set(startParameter)
            }
        }
    }

    private fun registerBuildFinishActions(target: Project) {
        val serviceProvider = target.gradle.sharedServices.registerIfAbsent(
            "close-service",
            BuildFinishService::class.java
        ) { }
        registry.onTaskCompletion(serviceProvider)
    }
}