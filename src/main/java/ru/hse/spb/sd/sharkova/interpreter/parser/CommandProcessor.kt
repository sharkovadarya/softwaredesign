package ru.hse.spb.sd.sharkova.interpreter.parser

import ru.hse.spb.sd.sharkova.interpreter.Interpreter
import ru.hse.spb.sd.sharkova.interpreter.stream.ErrorStream
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stream.OutputStream

/**
 * This class matches a command assembled from its name and argument list
 * to a method which interprets it and invokes that method.
 * Supported commands: cat, echo, wc, pwd, exit;
 * other commands are treated as external commands.
 */
class CommandProcessor(private val interpreter: Interpreter,
                       private val outputStream: OutputStream,
                       private val errorStream: ErrorStream) {
    private val keywords = listOf("cat", "echo", "wc", "pwd", "exit")

    /**
     * This method passes a command to the interpreter and executes the matching method.
     * @param commandName name of the command
     * @param arguments list of command arguments
     * @param inputStream input stream for the command (might contain result of the previously executed command)
     */
    fun processCommand(commandName: String, arguments: List<String>, inputStream: InputStream) {
        if (!keywords.contains(commandName)) {
            processExternalCommand(commandName, arguments, inputStream)
        } else {
            processKeyword(commandName, arguments, inputStream)
        }
    }

    private fun processKeyword(word: String,
                               arguments: List<String>,
                               inputStream: InputStream) {
        when (word) {
            "cat" -> interpreter.executeCat(arguments, inputStream, outputStream, errorStream)
            "echo" -> interpreter.executeEcho(arguments, inputStream, outputStream, errorStream)
            "wc" -> interpreter.executeWc(arguments, inputStream, outputStream, errorStream)
            "pwd" -> interpreter.executePwd(inputStream, outputStream, errorStream)
            "exit" -> interpreter.executeExit(inputStream, outputStream, errorStream)
        }
    }

    private fun processExternalCommand(commandName: String, arguments: List<String>, inputStream: InputStream) {
        val externalCommand = mutableListOf(commandName)
        externalCommand.addAll(arguments)
        interpreter.executeExternalCommand(externalCommand, inputStream, outputStream, errorStream)
    }
}
