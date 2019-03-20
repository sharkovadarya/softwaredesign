package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.*

/**
 * This class represents the 'echo' command
 * which outputs its arguments.
 * @param arguments list of arguments to output
 */
class EchoCommand(arguments: List<String>,
                  inputStream: InputStream,
                  outputStream: OutputStream,
                  errorStream: ErrorStream) : Command(arguments, inputStream, outputStream, errorStream) {
    override fun execute() {
        writeLine(arguments.joinToString(" ") + System.lineSeparator())
    }
}
