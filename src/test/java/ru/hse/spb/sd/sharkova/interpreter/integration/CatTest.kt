package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.*

class CatTest : InterpreterTest() {
    @Test
    fun testCat() {
        val res = parser.parseInput("cat src/test/resources/file1.txt")
        assertEquals(file1Lines, res)
    }

    // you can run this test on linux or in IntelliJ IDEA via its testing options.
    // do not run this test on Windows via gradle, it messes up the encoding in file2Lines
    /*@Test
    fun testCatMultipleArgumentsFile2() {
        val res = parser.parseInput("cat src/test/resources/file1.txt src/test/resources/file2.txt")
        val expected = mutableListOf<String>()
        expected.addAll(file1Lines)
        expected.addAll(file2Lines)

        assertEquals(expected, res)
    }*/

    @Test
    fun testCatMultipleArguments() {
        val res = parser.parseInput("cat src/test/resources/file1.txt src/test/resources/file3.txt")
        val expected = mutableListOf<String>()
        expected.addAll(file1Lines)
        expected.addAll(file3Lines)

        assertEquals(expected, res)
    }

    @Test
    fun testCatNonexistentFile() {
        val res = parser.parseInput("cat file1.txt")
        assertEquals(listOf(stringWithNewline("cat: file1.txt: No such file or directory")), res)
    }

    @Test
    fun testCatDirectory() {
        val res = parser.parseInput("cat src/test/resources")
        assertEquals(listOf(stringWithNewline("cat: src/test/resources: Is a directory")), res)
    }
}