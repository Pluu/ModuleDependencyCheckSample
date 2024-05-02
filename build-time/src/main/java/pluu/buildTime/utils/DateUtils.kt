package pluu.buildTime.utils

import java.text.SimpleDateFormat
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

fun Long.toDateTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
    return format.format(date)
}

fun Long.toDurationFormat(): String {
    return milliseconds.toString(DurationUnit.SECONDS, 3)
}