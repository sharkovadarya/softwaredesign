package ru.hse.spb.sd.sharkova.interpreter.parser

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import ru.hse.spb.sd.sharkova.interpreter.Interpreter
import ru.hse.spb.sd.sharkova.interpreter.stream.ErrorStream
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stream.OutputStream

/**
 * This class matches a command assembled from its name and argument list
 * to a method which interprets it and invokes that method.
 * Supported commands: cat, echo, wc, pwd, exit, grep;
 * other commands are treated as external commands.
 */
class CommandProcessor(private val interpreter: Interpreter,
                       private val outputStream: OutputStream,
                       private val errorStream: ErrorStream) {
    private val keywords = listOf("cat", "echo", "wc", "pwd", "exit", "grep")

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

    private fun processGrep(arguments: List<String>, inputStream: InputStream) {
        val args = GrepArguments()
        val argsArray = arguments.toTypedArray()
        JCommander.newBuilder().addObject(args).build().parse(*argsArray)

        val caseInsensitive = args.caseInsensitive
        val entireWord = args.entireWords
        val nLinesAfter = args.nLinesAfter
        if (args.parameters.isEmpty()) {
            errorStream.writeLine("grep: not enough arguments.")
            return
        }

        val regexString = args.parameters.first()
        val filenames = args.parameters.drop(1)

        interpreter.executeGrep(regexString, filenames, caseInsensitive, entireWord, nLinesAfter,
                inputStream, outputStream, errorStream)
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
            "grep" -> processGrep(arguments, inputStream)

        }
    }

    private fun processExternalCommand(commandName: String, arguments: List<String>, inputStream: InputStream) {
        val externalCommand = mutableListOf(commandName)
        externalCommand.addAll(arguments)
        interpreter.executeExternalCommand(externalCommand, inputStream, outputStream, errorStream)
    }

    private class GrepArguments {
        @Parameter(names = ["-i"], description = "Case insensitive")
        var caseInsensitive = false

        @Parameter(names = ["-w"], description = "Match only entire words")
        var entireWords = false

        @Parameter(names = ["-A"], description = "Print n lines after match")
        var nLinesAfter = 0

        @Parameter(description = "File list", variableArity = true)
        var parameters = mutableListOf<String>()
    }
}