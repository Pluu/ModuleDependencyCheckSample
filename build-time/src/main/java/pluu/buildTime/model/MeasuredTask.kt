package pluu.buildTime.model

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class MeasuredTask(
    val moduleName: String,
    val task: String,
    val startTime: Long,
    val endTime: Long,
    val state: State
) {
    val isCached: Boolean = state == State.IS_FROM_CACHE || state == State.UP_TO_DATE

    val duration: Duration = (endTime - startTime).milliseconds

    private fun Long.toTime(): String {
        return LocalTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault()).toString()
    }

    enum class State {
        UP_TO_DATE,
        IS_FROM_CACHE,
        EXECUTED,
    }
}