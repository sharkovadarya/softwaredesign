package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.*
import java.io.IOException

/**
 * This class represents an external command
 * which is executed not by this application but by the system.
 * @param arguments external command name and arguments
 */
class ExternalCommand(arguments: List<String>,
                      inputStream: InputStream,
                      outputStream: OutputStream,
                      errorStream: ErrorStream) : Command(arguments, inputStream, outputStream, errorStream) {
    override fun execute() {
        try {
            val processBuilder = ProcessBuilder(arguments)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectErrorStream(true)
                    .start()

            writeLines(processBuilder.inputStream.bufferedReader().readLines())
        } catch (e: IOException) {
            writeError(e.message ?: "Could not execute external command.")
        }
    }
}