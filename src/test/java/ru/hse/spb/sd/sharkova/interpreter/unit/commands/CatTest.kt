package ru.hse.spb.sd.sharkova.interpreter.unit.commands

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.commands.CatCommand
import ru.hse.spb.sd.sharkova.interpreter.file1Lines
import ru.hse.spb.sd.sharkova.interpreter.file2Lines
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stringWithNewline

class CatTest : CommandTest() {
    @Test
    fun testCat() {
        val command = constructCommand(listOf("src/test/resources/file1.txt"))
        command.execute()
        assertEquals(file1Lines, outputStream.getLines())
    }

    @Test
    fun testCatMultipleArguments() {
        val command = constructCommand(listOf("src/test/resources/file1.txt",
                "src/test/resources/file2.txt"))
        command.execute()
        val expected = mutableListOf<String>()
        expected.addAll(file1Lines)
        expected.addAll(file2Lines)

        assertEquals(expected, outputStream.getLines())
    }

    @Test
    fun testCatNonexistentFile() {
        val command = constructCommand(listOf("file1.txt"))
        command.execute()
        assertEquals(listOf(stringWithNewline("cat: file1.txt: No such file or directory")), errorStream.getLines())
    }

    @Test
    fun testCatDirectory() {
        val command = constructCommand(listOf("src/test/resources"))
        command.execute()
        assertEquals(listOf(stringWithNewline("cat: src/test/resources: Is a directory")), errorStream.getLines())
    }

    @Test
    fun testPipeCat() {
        val lines = listOf(stringWithNewline("line1"), "line2")
        val command = constructCommand(emptyList(), InputStream(lines))
        command.execute()
        assertEquals(lines, outputStream.getLines())
    }

    private fun constructCommand(arguments: List<String>, inputStream: InputStream = InputStream(emptyList())) =
            CatCommand(arguments, inputStream, outputStream, errorStream)
}