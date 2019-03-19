package ru.hse.spb.sd.sharkova.interpreter.stream

class InputStream(private val lines: List<String> = emptyList()) {

    companion object {
        fun emptyStream(): InputStream = InputStream(emptyList())
    }

    fun readLines(): List<String> = lines
}