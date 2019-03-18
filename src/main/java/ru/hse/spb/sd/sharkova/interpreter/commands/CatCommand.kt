package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.IncorrectArgumentException
import java.io.File
import java.nio.charset.Charset

object CatCommand : Command {
    override fun execute(arguments: List<String>): List<String> {
        val result = mutableListOf<String>()

        for (argument in arguments) {
            val file = File(argument)
            if (!file.exists()) {
                throw IncorrectArgumentException("cat", argument, "No such file or directory")
            } else if (file.isDirectory) {
                throw IncorrectArgumentException("cat", argument, "Is a directory")
            } else {
                val fileLines = file.readText(Charset.defaultCharset())
                        .split(Regex("(?<=${System.lineSeparator()})"))
                if (fileLines.last().isEmpty()) {
                    result.addAll(fileLines.dropLast(1))
                } else {
                    result.addAll(fileLines)
                }
            }
        }

        return result
    }
}

