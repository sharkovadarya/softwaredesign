package ru.hse.spb.sd.sharkova.interpreter.parser

import ru.hse.spb.sd.sharkova.interpreter.Interpreter
import ru.hse.spb.sd.sharkova.interpreter.stream.ErrorStream
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stream.OutputStream

/**
 * This class implements Parser interface
 * and supports double/single quotes, variable assignments,
 * substitutions and piping.
 * After the input has been parsed,
 * the result of the last executed command and the accumulated errors are returned.
 */
class CLIParser : Parser {
    private val outputStream = OutputStream()
    private val errorStream = ErrorStream()
    private val commandProcessor = CommandProcessor(Interpreter(), outputStream, errorStream)
    private val commandReader = CommandReader()

    override fun parseInput(input: String): List<String> {
        val words = commandReader.processInput(input)

        var commandNamePos = 0
        while (commandNamePos < words.size) {
            val command = commandReader.readCommand(words, commandNamePos)
            if (command.first == null) {
                commandNamePos = command.third
                continue
            }
            val commandName = command.first ?: continue
            val arguments = command.second
            commandNamePos = command.third

            val inputStream = outputStream.toInputStream()

            parseCommand(commandName, arguments, inputStream)
        }

        return outputStream.getLines() + errorStream.getLines()
    }


    private fun parseCommand(word: String, arguments: List<String>, inputStream: InputStream) {
        commandProcessor.processCommand(word, arguments, inputStream)
    }
}