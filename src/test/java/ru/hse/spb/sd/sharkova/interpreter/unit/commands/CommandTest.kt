package ru.hse.spb.sd.sharkova.interpreter.unit.commands

import org.junit.After
import ru.hse.spb.sd.sharkova.interpreter.stream.ErrorStream
import ru.hse.spb.sd.sharkova.interpreter.stream.OutputStream

abstract class CommandTest {
    protected val outputStream = OutputStream()
    protected val errorStream = ErrorStream()


    @After
    fun clearStreams() {
        outputStream.clear()
        errorStream.clear()
    }
}