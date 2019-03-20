package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.*

/**
 * This class represents a CLI command.
 * A command has arguments; input stream from which additional information,
 * such as the result of the previously executed command, can be read;
 * output stream to which the execution results can be written;
 * error stream to which errors that occured during the execution can be written.
 */
abstract class Command(protected val arguments: List<String>,
                       protected val inputStream: InputStream,
                       protected val outputStream: OutputStream,
                       private val errorStream: ErrorStream) {
    /**
     * This method executes the command.
     * During the execution, information from inputStream might be read,
     * information can be written to outputStream and errorStream.
     */
    abstract fun execute()

    protected fun writeLine(line: String) = outputStream.writeLine(line)

    protected fun writeLines(lines: List<String>) = outputStream.writeLines(lines)

    protected fun writeError(errorMessage: String) = errorStream.writeLine(errorMessage)

    protected fun writeError(commandName: String, argumentName: String, errorMessage: String) =
            writeError("$commandName: $argumentName: $errorMessage")
}







