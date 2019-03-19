package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.ErrorStream
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stream.OutputStream

class EchoCommand(arguments: List<String>,
                  inputStream: InputStream,
                  outputStream: OutputStream,
                  errorStream: ErrorStream) : Command(arguments, inputStream, outputStream, errorStream) {
    override fun execute(){
        outputStream.writeLine(arguments.joinToString(" ") + System.lineSeparator())
    }
}
