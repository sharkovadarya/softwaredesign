package ru.hse.spb.sd.sharkova.interpreter.unit.commands

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.commands.GrepCommand
import ru.hse.spb.sd.sharkova.interpreter.listStringsWithNewlines
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stringWithNewline

class GrepTest : CommandTest() {
    @Test
    fun testGrepSingleFile() {
        val command = constructCommand(listOf("src/test/resources/grep1.txt"), "word")
        command.execute()
        assertEquals(listStringsWithNewlines(listOf("this is a word", "more words your way!")),
                outputStream.getLines())
    }

    @Test
    fun testGrepMultipleFiles() {
        val command = constructCommand(listOf("src/test/resources/grep1.txt",
                "src/test/resources/grep2.txt"), "word")
        command.execute()
        assertEquals(listStringsWithNewlines(listOf("grep1.txt:this is a word",
                "grep1.txt:more words your way!", "grep2.txt:and this word")), outputStream.getLines())
    }

    @Test
    fun testGrepCaseInsensitive() {
        val command = constructCommand(listOf("src/test/resources/grep1.txt"), "word", true)
        command.execute()
        assertEquals(listStringsWithNewlines(listOf("this is a word", "This is a Word.",
                "These are some sad, sad WORDS!!!", "more words your way!")), outputStream.getLines())
    }

    @Test
    fun testGrepNLinesAfter() {
        val command = constructCommand(listOf("src/test/resources/ident2.txt"), "1", nLinesAfter = 2)
        command.execute()
        assertEquals(listStringsWithNewlines(listOf("1", "", "1 ", "1", "10", "  ", ".")), outputStream.getLines())
    }

    @Test
    fun testGrepEntireWord() {
        val command = constructCommand(listOf("src/test/resources/grep1.txt"), "word", entireWord = true)
        command.execute()
        assertEquals(listStringsWithNewlines(listOf("this is a word")), outputStream.getLines())
    }

    @Test
    fun testGrepCaseInsensitiveEntireWordNLinesAfter() {
        val command = constructCommand(listOf("src/test/resources/grep1.txt",
                "src/test/resources/grep2.txt"), "word", true, true, 1)
        command.execute()
        assertEquals(listStringsWithNewlines(listOf("grep1.txt:this is a word", "grep1.txt:This is a Word.",
                "grep1.txt:this is a sad,  sad world", "grep2.txt:and this word",
                "grep2.txt:in this text with     spaces")), outputStream.getLines())
    }

    @Test
    fun testGrepRegex() {
        val command = constructCommand(listOf("src/test/resources/grep1.txt"),
                "wor.*d", caseInsensitive = true)
        command.execute()
        assertEquals(listStringsWithNewlines(listOf("this is a word", "This is a Word.", "this is a sad,  sad world",
                "These are some sad, sad WORDS!!!", "more words your way!")), outputStream.getLines())
    }

    @Test
    fun testGrepNonexistentFile() {
        val command = constructCommand(listOf("file1.txt"), "word")
        command.execute()
        assertEquals(listOf(stringWithNewline("grep: file1.txt: No such file or directory")), errorStream.getLines())
    }

    @Test
    fun testGrepNegativeNAfterLines() {
        val command = constructCommand(listOf("src/test/resources/file1.txt"), "word", nLinesAfter = -10)
        command.execute()
        assertEquals(listOf(stringWithNewline("grep: Incorrect arguments")), errorStream.getLines())
    }

    private fun constructCommand(arguments: List<String>,
                                 regexString: String,
                                 caseInsensitive: Boolean = false,
                                 entireWord: Boolean = false,
                                 nLinesAfter: Int = 0,
                                 inputStream: InputStream = InputStream(emptyList())) =
            GrepCommand(arguments, inputStream, outputStream, errorStream,
                    regexString, caseInsensitive, entireWord, nLinesAfter)
}