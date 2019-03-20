package ru.hse.spb.sd.sharkova.interpreter.unit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.parser.CommandReader

class CommandReaderTest {
    private val commandReader = CommandReader()

    @Test
    fun testReadEcho() {
        val commandInput = listOf("echo", "word", "another", "word", "\"in quotes\"")
        val echoTriple = commandReader.readCommand(commandInput, 0)
        assertEquals("echo", echoTriple.first)
        assertEquals(listOf("word", "another", "word", "in quotes"), echoTriple.second)
        assertEquals(6, echoTriple.third)
    }

    @Test
    fun testReadCat() {
        val commandInput = listOf("cat", "src/test/resources/file1.txt", "src/test/resources/file2.txt")
        val catTriple = commandReader.readCommand(commandInput, 0)
        assertEquals("cat", catTriple.first)
        assertEquals(listOf("src/test/resources/file1.txt", "src/test/resources/file2.txt"), catTriple.second)
        assertEquals(4, catTriple.third)
    }

    @Test
    fun testPipeFirstCommand() {
        val commandInput = listOf("echo", "text", "\"more text\"", "|", "cat")
        val echoTriple = commandReader.readCommand(commandInput, 0)
        assertEquals("echo", echoTriple.first)
        assertEquals(listOf("text", "more text"), echoTriple.second)
        assertEquals(4, echoTriple.third)
    }

    @Test
    fun testPipeSecondCommand() {
        val commandInput = listOf("echo", "text", "\"more text\"", "|", "cat")
        val catTriple = commandReader.readCommand(commandInput, 4)
        assertEquals("cat", catTriple.first)
        assertEquals(emptyList<String>(), catTriple.second)
        assertEquals(6, catTriple.third)
    }

    @Test
    fun testAssignment() {
        val commandInput = listOf("x=text")
        val triple = commandReader.readCommand(commandInput, 0)
        assertEquals(null, triple.first)
    }

    @Test
    fun testSubstitutionInCommandName() {
        commandReader.readCommand(listOf("a=p"), 0)
        commandReader.readCommand(listOf("b=wd"), 0)
        val command = commandReader.readCommand(listOf("\$a\$b"), 0)
        assertEquals("pwd", command.first)
        assertTrue(command.second.isEmpty())
        assertEquals(2, command.third)
    }

    @Test
    fun testSubstitutionInArguments() {
        commandReader.readCommand(listOf("x=text"), 0)
        val command = commandReader.readCommand(listOf("echo", "\"\$x\"", "more", "text"), 0)
        assertEquals("echo", command.first)
        assertEquals(listOf("text", "more", "text"), command.second)
        assertEquals(5, command.third)
    }
}