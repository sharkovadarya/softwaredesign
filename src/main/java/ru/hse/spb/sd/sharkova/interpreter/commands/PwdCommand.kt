package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.*

/**
 * This class represents the 'pwd' command
 * which outputs the current directory.
 */
class PwdCommand(inputStream: InputStream,
                 outputStream: OutputStream,
                 errorStream: ErrorStream) : Command(emptyList(), inputStream, outputStream, errorStream) {
    override fun execute() {
        writeLine(System.getProperty("user.dir"))
    }
}
