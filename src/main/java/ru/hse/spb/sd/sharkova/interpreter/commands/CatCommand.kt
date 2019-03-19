package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.*
import java.io.File
import java.nio.charset.Charset

/**
 * This class represents the 'cat' command
 * which outputs the content of the provided file
 * or the result of the previously executed command.
 * @param filenames names of the files with content to be displayed
 */
class CatCommand(filenames: List<String>,
                 inputStream: InputStream,
                 outputStream: OutputStream,
                 errorStream: ErrorStream) : Command(filenames, inputStream, outputStream, errorStream) {
    override fun execute() {
        if (arguments.isEmpty()) {
            writeLines(inputStream.readLines())
            return
        }

        for (argument in arguments) {
            val file = File(argument)
            if (!file.exists()) {
                writeError("cat", argument, "No such file or directory")
            } else if (file.isDirectory) {
                writeError("cat", argument, "Is a directory")
            } else {
                val fileLines = file.readText(Charset.defaultCharset())
                        .split(Regex("(?<=${System.lineSeparator()})"))
                if (fileLines.last().isEmpty()) {
                    writeLines(fileLines.dropLast(1))
                } else {
                    writeLines(fileLines)
                }
            }
        }
    }
}

