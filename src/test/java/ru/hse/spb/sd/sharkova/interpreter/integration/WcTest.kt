package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.*

// uncomment everything related to file2 on linux systems o
// or while running tests not on gradle but in IntelliJ IDEA
class WcTest : InterpreterTest() {
    @Test
    fun testOneFileWc() {
        val res1 = parser.parseInput("wc src/test/resources/file1.txt")
        assertEquals(listOf(stringWithNewline("$file1Wc file1.txt")), res1)
        /*val res2 = parser.parseInput("wc src/test/resources/file2.txt")
        assertEquals(listOf(stringWithNewline("$file2Wc file2.txt")), res2)*/
        val res3 = parser.parseInput("wc src/test/resources/file3.txt")
        assertEquals(listOf(stringWithNewline("$file3Wc file3.txt")), res3)
    }

    @Test
    fun testMultipleFilesWc() {
        //val res12 = parser.parseInput("wc src/test/resources/file1.txt src/test/resources/file2.txt")
        val res13 = parser.parseInput("wc src/test/resources/file1.txt src/test/resources/file3.txt")
        /*assertEquals(listOf("$file1Wc file1.txt", "$file2Wc file2.txt", totalWc12)
                .map { stringWithNewline(it) }, res12)*/
        assertEquals(listOf("$file1Wc file1.txt", "$file3Wc file3.txt", "$totalWc13 total")
                .map { stringWithNewline(it) }, res13)
    }

    @Test
    fun testWcNonexistentFile() {
        val res = parser.parseInput("wc file1.txt")
        assertEquals(listOf(stringWithNewline("wc: file1.txt: No such file or directory")), res)
    }

    @Test
    fun testWcDirectory() {
        val res = parser.parseInput("wc src/test")
        assertEquals(listOf(stringWithNewline("wc: src/test: Is a directory")), res)
    }
}