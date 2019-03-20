package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.stringWithNewline

class QuotesTest : InterpreterTest() {
    @Test
    fun testDoubleQuotesEcho() {
        val res = parser.parseInput("echo \"text with spaces\"")
        Assert.assertEquals(listOf(stringWithNewline("text with spaces")), res)
    }

    @Test
    fun testEchoWithQuotesAndPipeSymbolInside() {
        val res = parser.parseInput("echo \" \' | \"")
        Assert.assertEquals(listOf(stringWithNewline(" \' | ")), res)
    }

    @Test
    fun testMultipleDoubleQuotesEcho() {
        val res = parser.parseInput("echo \"\"text with spaces\"\" and more \"and even more\"")
        Assert.assertEquals(listOf(stringWithNewline("text with spaces and more and even more")), res)
    }

    @Test
    fun testDoubleQuotesWithSubstitutionEcho() {
        parser.parseInput("x=text")
        val res1 = parser.parseInput("echo \"\$x\"")
        Assert.assertEquals(listOf(stringWithNewline("text")), res1)
        val res2 = parser.parseInput("echo \"$\"x\"\"")
        Assert.assertEquals(listOf(stringWithNewline("\$ x")), res2)
    }

    @Test
    fun testSingleQuotes() {
        val res = parser.parseInput("echo \'text\'")
        Assert.assertEquals(listOf(stringWithNewline("text")), res)
    }

    @Test
    fun testQuotesAssignment() {
        parser.parseInput("x=\"text with spaces\"")
        val res = parser.parseInput("echo \$x")
        Assert.assertEquals(listOf(stringWithNewline("text with spaces")), res)
    }

    @Test
    fun testNestedQuotes() {
        val res1 = parser.parseInput("echo \'\"text\"\'")
        Assert.assertEquals(listOf(stringWithNewline("text")), res1)
        val res2 = parser.parseInput("echo \"\'text\'\"")
        Assert.assertEquals(listOf(stringWithNewline("text")), res2)
    }

    @Test
    fun testSingleQuotesWithSubstitutionEcho() {
        parser.parseInput("x=text")
        val res = parser.parseInput("echo \'\$x\'")
        Assert.assertEquals(listOf(stringWithNewline("\$x")), res)
    }

    @Test
    fun testGrepDoubleQuotes() {
        val res = parser.parseInput("grep \"is a\" src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "This is a Word.", "this is a sad,  sad world"), res)
    }
}