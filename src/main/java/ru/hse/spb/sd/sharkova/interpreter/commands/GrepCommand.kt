package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.*
import java.io.File

/**
 * This class represents the 'grep' command
 * which outputs the lines that match the provided regex.
 * @param filenames list of files from which matching lines will be displayed
 * @param regexString regex to match against
 * @param caseInsensitive set 'true' for case insensitive match
 * @param entireWord set 'true' to match entire words only
 * @param nLinesAfter display n lines after match *
 */
class GrepCommand(filenames: List<String>,
                  inputStream: InputStream,
                  outputStream: OutputStream,
                  errorStream: ErrorStream,
                  private var regexString: String,
                  private val caseInsensitive: Boolean = false,
                  private val entireWord: Boolean = false,
                  private val nLinesAfter: Int = 0) : Command(filenames, inputStream, outputStream, errorStream) {
    private val generalRegexTemplate = "(?s).*%s.*"
    private val caseInsensitiveRegexTemplate = "(?i)%s"
    private val wholeWordRegexTemplate = "\\b%s\\b"

    private fun grep(regexString: String, lines: List<String>): List<String> {
        val result = mutableListOf<String>()
        val regexForUsage = if (caseInsensitive) {
            Regex(caseInsensitiveRegexTemplate.format(generalRegexTemplate.format(regexString)))
        } else {
            Regex(generalRegexTemplate.format(regexString))
        }
        lines.forEach { if (regexForUsage.matches(it)) result.add(it) }
        return result
    }

    private fun wholeWordGrep(regexString: String, lines: List<String>): List<String> {
        return grep(wholeWordRegexTemplate.format(regexString), lines)
    }

    private fun afterMatchGrep(regexString: String, lines: List<String>): List<String> {
        val resultingLines = LinkedHashSet<String>()
        val newRegexString = generalRegexTemplate
                .format(if (entireWord) wholeWordRegexTemplate.format(regexString) else regexString)
        val regexForUsage = if (caseInsensitive) Regex(caseInsensitiveRegexTemplate.format(newRegexString))
        else Regex(newRegexString)
        for (i in 0 until lines.size) {
            if (regexForUsage.matches(lines[i])) {
                resultingLines.add(lines[i])
                for (j in 1..nLinesAfter) {
                    if (i + j >= lines.size) {
                        break
                    }
                    resultingLines.add(lines[i + j])
                }
            }
        }
        return resultingLines.toList()
    }

    private fun executeGrep(lines: List<String>) {
        writeLines(afterMatchGrep(regexString, lines))
    }

    private fun executeFileGrep(filenames: List<String>) {
        val result = mutableListOf<String>()

        for (filename in filenames) {
            val file = File(filename)
            if (!file.exists()) {
                writeError("grep", filename, "No such file or directory")
            } else if (file.isDirectory) {
                writeError("grep", filename, "Is a directory")
            } else {
                executeGrep(file.readLines())
                val fileResult = outputStream.getLines()
                if (filenames.size > 1) {
                    fileResult.forEach { result.add("${file.name}:$it") }
                } else {
                    fileResult.forEach { result.add(it) }
                }
                outputStream.clear()
            }
        }

        outputStream.clear()
        writeLines(result)
    }

    override fun execute() {
        if (arguments.isEmpty()) {
            executeGrep(inputStream.readLines())
        } else {
            executeFileGrep(arguments)
        }
    }
}