package pluu.buildTime.report

import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

fun URL.openSafeStream(): InputStream {
    return openConnection().apply { useCaches = false }.getInputStream()
}

private fun Any.getTextResourceContent(fileName: String): String {
    return javaClass.getResource("/$fileName")!!
        .openSafeStream()
        .bufferedReader()
        .use { it.readText() }
}

private fun Any.getResourceContent(fileName: String): InputStream {
    return javaClass.getResource("/$fileName")!!
        .openSafeStream()
}

object HtmlUtils {
    /**
     * Return the text resource file content as String.
     */
    fun getTemplate(fileName: String): String {
        return getTextResourceContent("$fileName.html")
    }

    fun copyToResource(fileName: String, path: String) {
        getResourceContent("$fileName").use { input ->
            FileOutputStream(path).use { output ->
                input.copyTo(output)
            }
        }
    }
}