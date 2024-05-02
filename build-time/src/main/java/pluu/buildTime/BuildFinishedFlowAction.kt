package pluu.buildTime

import org.gradle.StartParameter
import org.gradle.api.flow.BuildWorkResult
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.Input
import pluu.buildTime.model.MeasuredTask
import pluu.buildTime.report.HtmlUtils
import pluu.buildTime.report.HtmlUtils.copyToResource
import pluu.buildTime.utils.toDateTime
import pluu.buildTime.utils.toDurationFormat
import java.io.File

@Suppress("UnstableApiUsage")
internal class BuildFinishedFlowAction : FlowAction<BuildFinishedFlowAction.Parameters> {

    interface Parameters : FlowParameters {

        // This value will NOT update if project re-used configuration cache
        // Use ONLY for calculating configuration phase duration if it was executed
        @get:Input
        val initiationTime: Property<Long>

        @get:Input
        val configurationPhaseExecuted: Property<Provider<Boolean>>

        @get:Input
        val buildWorkResult: Property<Provider<BuildWorkResult>>

        @get:ServiceReference
        val buildTaskService: Property<BuildFinishService>

        @get:Input
        val startParameter: Property<StartParameter>
    }

    override fun execute(parameters: Parameters) {
        val init = parameters.initiationTime.get()
        val buildTaskService = parameters.buildTaskService.get()
        val buildStart = buildTaskService.buildStartTime
        val finish = System.currentTimeMillis()

        val configurationTime = if (parameters.configurationPhaseExecuted.get().get()) {
            buildStart - init
        } else {
            0
        }

        val html = getMetricRender(
            configurationTime,
            buildStart,
            finish,
            buildTaskService.tasks
        )

        val fileName = "d3-gtimeline.full.umd.cjs"
        copyToResource(fileName, "build/${fileName}")
        File("build", "a.html").writeText(html)
    }

    private fun getMetricRender(
        configurationTime: Long,
        buildStartTime: Long,
        buildFinishTime: Long,
        tasks: List<MeasuredTask>
    ): String {
        val renderedTemplate = HtmlUtils.getTemplate("modules-timeline-metric-template")

        val groupingTask = tasks.groupBy {
            it.moduleName
        }.toSortedMap()

        val result = buildString {
            groupingTask.entries.forEach { (key, value) ->
                value.forEach { timeline ->
                    appendLine(
                        "['${key}', '${timeline.task}', new Date(${timeline.startTime}), new Date(${timeline.endTime})],"
                    )
                }
            }
        }

        return renderedTemplate
            .replace("%configuration_time%", configurationTime.toDurationFormat())
            .replace("%build_start_time%", buildStartTime.toDateTime())
            .replace("%build_end_time%", buildFinishTime.toDateTime())
            .replace(
                "%build_time%",
                (buildFinishTime - buildStartTime).toDurationFormat()
            ).replace(
                "%build_time_with_configuration%",
                (buildFinishTime - buildStartTime + configurationTime).toDurationFormat()
            ).replace("%build_data%", result)
    }
}