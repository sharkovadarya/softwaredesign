package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.ErrorStream
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stream.OutputStream

abstract class Command(protected val arguments: List<String>,
                       protected val inputStream: InputStream,
                       protected val outputStream: OutputStream,
                       private val errorStream: ErrorStream) {
    abstract fun execute()

    protected fun writeError(errorMessage: String) = errorStream.writeLine(errorMessage)

    protected fun writeError(commandName: String, argumentName: String, errorMessage: String) =
            writeError("$commandName: $argumentName: $errorMessage")
}







