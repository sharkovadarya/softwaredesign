package ru.hse.spb.sd.sharkova.interpreter.unit.commands

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.commands.EchoCommand
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stringWithNewline

class EchoTest : CommandTest() {
    @Test
    fun testEchoSingleArgument() {
        val command = constructCommand(listOf("word"))
        command.execute()
        assertEquals(listOf(stringWithNewline("word")), outputStream.getLines())
    }

    @Test
    fun testEchoMultipleArguments() {
        val command = constructCommand(listOf("word", "another", "word"))
        command.execute()
        assertEquals(listOf(stringWithNewline("word another word")), outputStream.getLines())
    }

    @Test
    fun testEchoSameNameArguments() {
        val command = constructCommand(listOf("echo", "echo", "echo"))
        command.execute()
        assertEquals(listOf(stringWithNewline("echo echo echo")), outputStream.getLines())
    }

    private fun constructCommand(arguments: List<String>)  =
            EchoCommand(arguments, InputStream(emptyList()), outputStream, errorStream)
}