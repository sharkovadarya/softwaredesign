package ru.hse.spb.sd.sharkova.interpreter.stream

/**
 * This class represents an output stream.
 */
open class OutputStream {
    private val output = mutableListOf<String>()

    fun writeLine(line: String) = output.add(line)

    fun writeLines(lines: List<String>) = output.addAll(lines)

    fun getLines(): List<String> {
        val lines = output.toList()
        clear()
        return lines
    }

    fun clear() = output.clear()

    fun toInputStream(): InputStream {
        val inputStream = InputStream(output.toList())
        clear()
        return inputStream
    }
}

/**
 * This class represents an error stream.
 */
class ErrorStream : OutputStream()