package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.*

class PipeTest : InterpreterTest() {
    @Test
    fun testPipeEchoCat() {
        val res = parser.parseInput("echo echo echo echo|cat src/test/resources/file1.txt")
        Assert.assertEquals(file1Lines, res)
    }

    @Test
    fun testPipeEchoCatNoArguments() {
        val res = parser.parseInput("echo text | cat")
        Assert.assertEquals(listOf(stringWithNewline("text")), res)
    }

    @Test
    fun testPipeCatEcho() {
        val res = parser.parseInput("cat src/test/resources/file1.txt | echo echo echo echo")
        Assert.assertEquals(listOf(stringWithNewline("echo echo echo")), res)
    }

    @Test
    fun testPipeCatWc() {
        val res = parser.parseInput("cat src/test/resources/file1.txt|wc")
        Assert.assertEquals(listOf(stringWithNewline(file1Wc)), res)
    }

    @Test
    fun testPipeCatMultipleFilesWc() {
        val res = parser.parseInput("cat src/test/resources/file1.txt src/test/resources/file2.txt   | wc")
        Assert.assertEquals(listOf(stringWithNewline("4 118 697")), res)
    }

    @Test
    fun testPipeEchoWc() {
        val res = parser.parseInput("echo echo echo | wc")
        Assert.assertEquals(listOf(stringWithNewline("1 2 10")), res)
    }

    @Test
    fun testPipeWcEcho() {
        val res = parser.parseInput("wc src/test/resources/file1.txt | echo echo echo")
        Assert.assertEquals(listOf(stringWithNewline("echo echo")), res)
    }

    @Test
    fun testPipeDoubleWc() {
        val res = parser.parseInput("wc src/test/resources/file1.txt | wc")
        Assert.assertEquals(listOf(stringWithNewline("1 4 17")), res)
    }

    @Test
    fun testWcNonexistentFileInPipe() {
        val res = parser.parseInput("echo some text | wc src/file1.txt | echo more text")
        Assert.assertEquals(listOf(stringWithNewline("more text"), "wc: src/file1.txt: No such file or directory"), res)
    }

    @Test
    fun testPipeGrep() {
        val res = parser.parseInput("cat src/test/resources/grep1.txt | grep word")
        Assert.assertEquals(listOf(stringWithNewline("this is a word"), "more words your way!"), res)
    }
}