package ru.hse.spb.sd.sharkova.interpreter.unit.commands

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.commands.ExternalCommand
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream

class ExternalCommandTest : CommandTest() {

    // run the following tests only on systems where 'ls' command is present

    /*@Test
    fun testLs() {
        val command = constructCommand(listOf("ls", "src"))
        command.execute()
        assertEquals(listOf("main", "test").sorted(), outputStream.getLines().sorted())
    }

    @Test
    fun testLsWithBadArgument() {
        val command = constructCommand(listOf("ls", "srd"))
        command.execute()
        assertEquals(listOf("ls: cannot access 'srd': No such file or directory"), outputStream.getLines())
    }*/

    @Test
    fun testNonexistentCommand() {
        val command = constructCommand(listOf("fdkjsafhdksjh"))
        command.execute()
        // the exact message probably depends on the system?
        assertTrue(errorStream.getLines().isNotEmpty())

    }

    private fun constructCommand(arguments: List<String>, inputStream: InputStream = InputStream(emptyList())) =
            ExternalCommand(arguments, inputStream, outputStream, errorStream)
}