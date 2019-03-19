package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert.*
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.parser.CLIParser
import java.io.IOException

abstract class InterpreterTest {
    protected val file1Lines = listOf("text", "line", "", "the previous line was an empty one").map { stringWithNewline(it) }
    protected val file2Lines = listOf("So long as there shall exist, by virtue of law and custom, " +
            "decrees of damnation pronounced by society, artificially creating hells amid the civilization of earth, " +
            "and adding the element of human fate to divine destiny; " +
            "so long as the three great problems of the century—the degradation of man through pauperism, " +
            "the corruption of woman through hunger, the crippling of children through lack of light—are unsolved; " +
            "so long as social asphyxia is possible in any part of the world;—in other words, " +
            "and with a still wider significance, so long as ignorance and poverty exist on earth, " +
            "books of the nature of Les Misérables cannot fail to be of use.")
    protected val file1Wc = "4 9 46"
    protected val file2Wc = "0 109 651"
    
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

    protected fun stringWithNewline(string: String) = string + System.lineSeparator()
}