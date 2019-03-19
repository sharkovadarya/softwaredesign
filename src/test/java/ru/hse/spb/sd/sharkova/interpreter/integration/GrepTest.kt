package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert
import org.junit.Test

class GrepTest : InterpreterTest() {
    @Test
    fun testGrepSingleFile() {
        val res = parser.parseInput("grep word src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "more words your way!"), res)
    }

    @Test
    fun testGrepMultipleFiles() {
        val res = parser.parseInput("grep word src/test/resources/grep1.txt src/test/resources/grep2.txt")
        Assert.assertEquals(listOf("grep1.txt:this is a word", "grep1.txt:more words your way!", "grep2.txt:and this word"), res)
    }

    @Test
    fun testGrepCaseInsensitive() {
        val res = parser.parseInput("grep -i word src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "This is a Word.",
                "These are some sad, sad WORDS!!!", "more words your way!"), res)
    }

    @Test
    fun testGrepWholeWords() {
        val res = parser.parseInput("grep -w word src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word"), res)
    }

    @Test
    fun testGrepNLinesAfter() {
        val res = parser.parseInput("grep -A 2 word src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "This is a Word.",
                "this is a sad,  sad world", "more words your way!"), res)
    }

    @Test
    fun testGrepCaseInsensitiveWholeWord() {
        val res1 = parser.parseInput("grep -w -i word src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "This is a Word."), res1)
        // arguments input order doesn't matter
        val res2 = parser.parseInput("grep -i -w word src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "This is a Word."), res2)
    }

    @Test
    fun testGrepCaseInsensitiveNLinesAfter() {
        val res = parser.parseInput("grep -i -A 2 this src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "This is a Word.", "this is a sad,  sad world",
                "These are some sad, sad WORDS!!!", "43 49825 34 0965 26 48 23.4 89,9"), res)
    }

    @Test
    fun testGrepWholeWordNLinesAfter() {
        val res = parser.parseInput("grep -w -A 2 word src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "This is a Word.", "this is a sad,  sad world"), res)
    }

    @Test
    fun testGrepCaseInsensitiveWholeWordNLinesAfter() {
        val res = parser
                .parseInput("grep -i -w -A 1 word src/test/resources/grep1.txt src/test/resources/grep2.txt")
        Assert.assertEquals(listOf("grep1.txt:this is a word", "grep1.txt:This is a Word.",
                "grep1.txt:this is a sad,  sad world", "grep2.txt:and this word",
                "grep2.txt:in this text with     spaces"), res)
    }

    @Test
    fun testGrepRegularExpression1() {
        val res = parser.parseInput("grep \"sad,[ ]*sad\" src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a sad,  sad world", "These are some sad, sad WORDS!!!"), res)
    }

    @Test
    fun testGrepRegularExpression2() {
        val res = parser.parseInput("grep \"wor.*d\" src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "this is a sad,  sad world", "more words your way!"), res)
    }

    @Test
    fun testGrepRegularExpressionCaseInsensitive() {
        val res = parser.parseInput("grep -i \"wor.*d\" src/test/resources/grep1.txt")
        Assert.assertEquals(listOf("this is a word", "This is a Word.", "this is a sad,  sad world",
                "These are some sad, sad WORDS!!!", "more words your way!"), res)
    }

    @Test
    fun testGrepArgumentsOrder() {
        val res1 = parser.parseInput("grep -i word src/test/resources/grep1.txt")
        val res2 = parser.parseInput("grep word -i src/test/resources/grep1.txt")
        val res3 = parser.parseInput("grep word src/test/resources/grep1.txt -i")
        Assert.assertEquals(res1, res2)
        Assert.assertEquals(res2, res3)
    }

    @Test
    fun testDoubleGrep() {
        val res = parser.parseInput("grep -i word src/test/resources/grep1.txt | grep this")
        Assert.assertEquals(listOf("this is a word"), res)
    }

    @Test
    fun testGrepNonexistentFile() {
        val res = parser.parseInput("grep -i word grep1.txt")
        Assert.assertEquals(listOf("grep: grep1.txt: No such file or directory"), res)
    }
}