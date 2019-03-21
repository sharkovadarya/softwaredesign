package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.*

class PipeTest : InterpreterTest() {
    @Test
    fun testPipeEchoCat() {
        val res = parser.parseInput("echo echo echo echo|cat src/test/resources/file1.txt")
        assertEquals(file1Lines, res)
    }

    @Test
    fun testPipeEchoCatNoArguments() {
        val res = parser.parseInput("echo text | cat")
        assertEquals(listOf(stringWithNewline("text")), res)
    }

    @Test
    fun testPipeCatEcho() {
        val res = parser.parseInput("cat src/test/resources/file1.txt | echo echo echo echo")
        assertEquals(listOf(stringWithNewline("echo echo echo")), res)
    }

    @Test
    fun testPipeCatWc() {
        val res = parser.parseInput("cat src/test/resources/file1.txt|wc")
        assertEquals(listOf(stringWithNewline(file1Wc)), res)
    }

    @Test
    fun testPipeCatMultipleFilesWc() {
        val res = parser.parseInput("cat src/test/resources/file1.txt src/test/resources/file2.txt   | wc")
        assertEquals(listOf(stringWithNewline("4 118 697")), res)
    }

    @Test
    fun testPipeEchoWc() {
        val res = parser.parseInput("echo echo echo | wc")
        assertEquals(listOf(stringWithNewline("1 2 10")), res)
    }

    @Test
    fun testPipeWcEcho() {
        val res = parser.parseInput("wc src/test/resources/file1.txt | echo echo echo")
        assertEquals(listOf(stringWithNewline("echo echo")), res)
    }

    @Test
    fun testPipeDoubleWc() {
        val res = parser.parseInput("wc src/test/resources/file1.txt | wc")
        assertEquals(listOf(stringWithNewline("1 4 17")), res)
    }

    @Test
    fun testWcNonexistentFileInPipe() {
        val res = parser.parseInput("echo some text | wc src/file1.txt | echo more text")
        assertEquals(listStringsWithNewlines(listOf("more text",
                "wc: src/file1.txt: No such file or directory")), res)
    }

    @Test
    fun testNoCommandAfterPipe() {
        val res = parser.parseInput("echo text |           ")
        assertEquals(listOf(stringWithNewline("Incorrect input: no command after command separator")), res)
    }
}
