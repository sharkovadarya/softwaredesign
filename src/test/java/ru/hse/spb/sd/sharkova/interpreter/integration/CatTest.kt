package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert
import org.junit.Test

class CatTest : InterpreterTest() {
    @Test
    fun testCat() {
        val res = parser.parseInput("cat src/test/resources/file1.txt")
        Assert.assertEquals(file1Lines, res)
    }

    @Test
    fun testCatMultipleArguments() {
        val res = parser.parseInput("cat src/test/resources/file1.txt src/test/resources/file2.txt")
        val expected = mutableListOf<String>()
        expected.addAll(file1Lines)
        expected.addAll(file2Lines)

        Assert.assertEquals(expected, res)
    }

    @Test
    fun testCatNonexistentFile() {
        val res = parser.parseInput("cat file1.txt")
        Assert.assertEquals(listOf("cat: file1.txt: No such file or directory"), res)
    }

    @Test
    fun testCatDirectory() {
        val res = parser.parseInput("cat src/test/resources")
        Assert.assertEquals(listOf("cat: src/test/resources: Is a directory"), res)
    }
}