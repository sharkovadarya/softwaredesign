package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert.*
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.parser.CLIParser
import java.io.IOException

abstract class InterpreterTest {
    protected val parser = CLIParser()

    // method taken from: https://stackoverflow.com/a/41495542/7735110
    protected fun String.runCommand(): List<String> {
        try {
            val parts = this.split("\\s".toRegex())
            val process = ProcessBuilder(*parts.toTypedArray())
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectErrorStream(true)
                    .start()
            return process.inputStream.bufferedReader().readLines()
        } catch (e: IOException) {
            e.printStackTrace()
            fail()
        }

        return emptyList()
    }
}
