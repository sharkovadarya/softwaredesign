package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.*

/**
 * This class represents the 'exit' command
 * which exits the interpreter and shuts down the application.
 */
class ExitCommand(inputStream: InputStream,
                  outputStream: OutputStream,
                  errorStream: ErrorStream) : Command(emptyList(), inputStream, outputStream, errorStream) {
    override fun execute() {
        System.exit(0)
    }
}