package ru.hse.spb.sd.sharkova.interpreter.unit.commands

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.commands.WcCommand
import ru.hse.spb.sd.sharkova.interpreter.*
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stringWithNewline

class WcTest : CommandTest() {
    @Test
    fun testOneFileWc() {
        val command1 = constructCommand(listOf("src/test/resources/file1.txt"))
        val command2 = constructCommand(listOf("src/test/resources/file2.txt"))
        command1.execute()
        assertEquals(listOf(stringWithNewline("$file1Wc file1.txt")), outputStream.getLines())
        command2.execute()
        assertEquals(listOf(stringWithNewline("$file2Wc file2.txt")), outputStream.getLines())
    }

    @Test
    fun testMultipleFilesWc() {
        /*val command = constructCommand(listOf("src/test/resources/file1.txt",
                "src/test/resources/file2.txt"))
        command.execute()
        assertEquals(listOf("$file1Wc file1.txt", "$file2Wc file2.txt", "4 118 697 total")
                .map { stringWithNewline(it) }, outputStream.getLines())*/
        val command = constructCommand(listOf("src/test/resources/file1.txt",
                "src/test/resources/file3.txt"))
        command.execute()
        assertEquals(listOf("$file1Wc file1.txt", "$file3Wc file3.txt", "$totalWc13 total")
                .map { stringWithNewline(it) }, outputStream.getLines())
    }

    @Test
    fun testWcNonexistentFile() {
        val command = constructCommand(listOf("file1.txt"))
        command.execute()
        assertEquals(listOf(stringWithNewline("wc: file1.txt: No such file or directory")), errorStream.getLines())
    }

    @Test
    fun testWcDirectory() {
        val command = constructCommand(listOf("src/test"))
        command.execute()
        assertEquals(listOf(stringWithNewline("wc: src/test: Is a directory")), errorStream.getLines())
    }

    @Test
    fun testPipeWc() {
        val lines = listOf(stringWithNewline("line1"), "line 2")
        val command = constructCommand(emptyList(), InputStream(lines))
        command.execute()
        assertEquals(listOf(stringWithNewline("1 3 ${11 + System.lineSeparator().length}")), outputStream.getLines())
    }

    private fun constructCommand(arguments: List<String>, inputStream: InputStream = InputStream(emptyList())) =
            WcCommand(arguments, inputStream, outputStream, errorStream)
}