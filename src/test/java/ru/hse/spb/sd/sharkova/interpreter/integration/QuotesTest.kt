package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.listStringsWithNewlines
import ru.hse.spb.sd.sharkova.interpreter.stringWithNewline

class QuotesTest : InterpreterTest() {
    @Test
    fun testDoubleQuotesEcho() {
        val res = parser.parseInput("echo \"text with spaces\"")
        assertEquals(listOf(stringWithNewline("text with spaces")), res)
    }

    @Test
    fun testEchoWithQuotesAndPipeSymbolInside() {
        val res = parser.parseInput("echo \" \' | \"")
        assertEquals(listOf(stringWithNewline(" \' | ")), res)
    }

    @Test
    fun testMultipleDoubleQuotesEcho() {
        val res = parser.parseInput("echo \"\"text with spaces\"\" and more \"and even more\"")
        assertEquals(listOf(stringWithNewline("text with spaces and more and even more")), res)
    }

    @Test
    fun testDoubleQuotesWithSubstitutionEcho() {
        parser.parseInput("x=text")
        val res1 = parser.parseInput("echo \"\$x\"")
        assertEquals(listOf(stringWithNewline("text")), res1)
        val res2 = parser.parseInput("echo \"$\"x\"\"")
        assertEquals(listOf(stringWithNewline("\$ x")), res2)
    }

    @Test
    fun testSingleQuotes() {
        val res = parser.parseInput("echo \'text\'")
        assertEquals(listOf(stringWithNewline("text")), res)
    }

    @Test
    fun testQuotesAssignment() {
        parser.parseInput("x=\"text with spaces\"")
        val res = parser.parseInput("echo \$x")
        assertEquals(listOf(stringWithNewline("text with spaces")), res)
    }

    @Test
    fun testNestedQuotes() {
        val res1 = parser.parseInput("echo \'\"text\"\'")
        assertEquals(listOf(stringWithNewline("\"text\"")), res1)
        val res2 = parser.parseInput("echo \"\'text\'\"")
        assertEquals(listOf(stringWithNewline("\'text\'")), res2)
    }

    @Test
    fun testSingleQuotesWithSubstitutionEcho() {
        parser.parseInput("x=text")
        val res = parser.parseInput("echo \'\$x\'")
        assertEquals(listOf(stringWithNewline("\$x")), res)
    }
}