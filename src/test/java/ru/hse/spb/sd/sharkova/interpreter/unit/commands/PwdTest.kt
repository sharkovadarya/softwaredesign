package ru.hse.spb.sd.sharkova.interpreter.unit.commands

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.commands.PwdCommand
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stringWithNewline

class PwdTest : CommandTest() {

    @Test
    fun testPwd() {
        val command = constructCommand()
        command.execute()
        assertEquals(listOf(stringWithNewline(System.getProperty("user.dir"))), outputStream.getLines())
    }

    private fun constructCommand() = PwdCommand(InputStream(emptyList()), outputStream, errorStream)
}