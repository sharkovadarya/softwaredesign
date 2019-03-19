package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert
import org.junit.Test

class WcTest : InterpreterTest() {
    @Test
    fun testOneFileWc() {
        val res1 = parser.parseInput("wc src/test/resources/file1.txt")
        Assert.assertEquals(listOf(stringWithNewline("$file1Wc file1.txt")), res1)
        val res2 = parser.parseInput("wc src/test/resources/file2.txt")
        Assert.assertEquals(listOf(stringWithNewline("$file2Wc file2.txt")), res2)
    }

    @Test
    fun testMultipleFilesWc() {
        val res = parser.parseInput("wc src/test/resources/file1.txt src/test/resources/file2.txt")
        Assert.assertEquals(listOf("$file1Wc file1.txt", "$file2Wc file2.txt", "4 118 697 total")
                .map { stringWithNewline(it) }, res)
    }

    @Test
    fun testWcNonexistentFile() {
        val res = parser.parseInput("wc file1.txt")
        Assert.assertEquals(listOf("wc: file1.txt: No such file or directory"), res)
    }

    @Test
    fun testWcDirectory() {
        val res = parser.parseInput("wc src/test")
        Assert.assertEquals(listOf("wc: src/test: Is a directory"), res)
    }
}