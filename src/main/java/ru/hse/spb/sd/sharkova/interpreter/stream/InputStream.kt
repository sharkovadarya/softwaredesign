package ru.hse.spb.sd.sharkova.interpreter.stream

/**
 * This class represents an input stream.
 */
class InputStream(private val lines: List<String> = emptyList()) {
    fun readLines(): List<String> = lines
}