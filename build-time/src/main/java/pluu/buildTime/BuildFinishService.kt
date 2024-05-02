package pluu.buildTime

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.SuccessResult
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskSuccessResult
import pluu.buildTime.model.MeasuredTask
import pluu.buildTime.model.MeasuredTask.State.EXECUTED
import pluu.buildTime.model.MeasuredTask.State.IS_FROM_CACHE
import pluu.buildTime.model.MeasuredTask.State.UP_TO_DATE
import java.util.concurrent.ConcurrentLinkedQueue

abstract class BuildFinishService :
    BuildService<BuildServiceParameters.None>,
    OperationCompletionListener {

    private val measuredTasks = ConcurrentLinkedQueue<MeasuredTask>()
    val tasks: List<MeasuredTask>
        get() = measuredTasks.toList()

    val buildStartTime: Long = System.currentTimeMillis()

    override fun onFinish(event: FinishEvent?) {
        if (event is TaskFinishEvent) {
            if (event.result is SuccessResult) {
                val (name, taskName) = event.descriptor?.name.orEmpty().let { name ->
                    val (moduleName, taskName) = name.substringBeforeLast(":") to
                            name.substringAfterLast(":")
                    moduleName to ":${taskName}"
                }
                if (name.startsWith(":build-time")) return

                val result = event.result as TaskSuccessResult
                measuredTasks.add(
                    MeasuredTask(
                        moduleName = name,
                        task = taskName,
                        startTime = event.result.startTime,
                        endTime = event.result.endTime,
                        state = when {
                            result.isFromCache -> IS_FROM_CACHE
                            result.isUpToDate -> UP_TO_DATE
                            else -> EXECUTED
                        }
                    )
                )
            }
        }
    }
}