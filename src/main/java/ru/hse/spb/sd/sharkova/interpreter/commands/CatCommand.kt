package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.IncorrectArgumentException
import ru.hse.spb.sd.sharkova.interpreter.stream.ErrorStream
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stream.OutputStream
import java.io.File
import java.nio.charset.Charset

class CatCommand(arguments: List<String>,
                 inputStream: InputStream,
                 outputStream: OutputStream,
                 errorStream: ErrorStream) : Command(arguments, inputStream, outputStream, errorStream) {
    override fun execute() {
        if (arguments.isEmpty()) {
            outputStream.writeLines(inputStream.readLines())
            return
        }

        val result = mutableListOf<String>()

        for (argument in arguments) {
            val file = File(argument)
            if (!file.exists()) {
                writeError("cat", argument, "No such file or directory")
                //throw IncorrectArgumentException("cat", argument, "No such file or directory")
            } else if (file.isDirectory) {
                writeError("cat", argument, "Is a directory")
                //throw IncorrectArgumentException("cat", argument, "Is a directory")
            } else {
                val fileLines = file.readText(Charset.defaultCharset())
                        .split(Regex("(?<=${System.lineSeparator()})"))
                if (fileLines.last().isEmpty()) {
                    outputStream.writeLines(fileLines.dropLast(1))
                    result.addAll(fileLines.dropLast(1))
                } else {
                    outputStream.writeLines(fileLines)
                    result.addAll(fileLines)
                }
            }
        }
    }
}

