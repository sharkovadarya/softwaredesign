package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.file1Lines
import ru.hse.spb.sd.sharkova.interpreter.listStringsWithNewlines
import ru.hse.spb.sd.sharkova.interpreter.stringWithNewline
import kotlin.math.exp

class SubstitutionTest : InterpreterTest() {
    @Test
    fun testVariableSubstitutionEcho() {
        parser.parseInput("x=texttext")
        val res = parser.parseInput("echo \$x")
        assertEquals(listOf(stringWithNewline("texttext")), res)
    }

    @Test
    fun testMultipleVariableSubstitutionEcho() {
        parser.parseInput("x=texttext")
        parser.parseInput("y=x")
        val res1 = parser.parseInput("echo \$y")
        assertEquals(listOf(stringWithNewline("x")), res1)
        parser.parseInput("y=\$x")
        val res2 = parser.parseInput("echo \$y")
        assertEquals(listOf(stringWithNewline("texttext")), res2)
    }

    @Test
    fun testSubstituteEchoWithSpaces() {
        parser.parseInput("x=text")
        val res = parser.parseInput("echo \"   \$x\"")
        assertEquals(listOf(stringWithNewline("   text")), res)
    }

    @Test
    fun testEchoWithVariousPositionSubstitutions() {
        parser.parseInput("x=tt")
        parser.parseInput("y=echo")
        val res = parser.parseInput("\$y text\$xтекст\$x")
        assertEquals(listOf(stringWithNewline("textttтекстtt")), res)
    }

    @Test
    fun testNoIdentifierDollarSignEcho() {
        val res = parser.parseInput("echo \$не_идентификатор")
        assertEquals(listOf(stringWithNewline("\$не_идентификатор")), res)
    }


    @Test
    fun testSubstituteFilenameCat() {
        parser.parseInput("x=src/test/resources/file1.txt")
        val res = parser.parseInput("cat \$x")
        assertEquals(file1Lines, res)
    }

    @Test
    fun testSubstitutePwd() {
        parser.parseInput("x=pwd")
        val res = parser.parseInput("\$x")
        // the line below can be used on systems where "pwd" command is present
        //assertEquals(listStringsWithNewlines("pwd".runCommand()), res)
        val expected = listStringsWithNewlines(listOf(System.getProperty("user.dir")))
        assertEquals(expected, res)
    }

    @Test
    fun testSubstitutePwdTwoVariables() {
        parser.parseInput("a=p")
        parser.parseInput("b=wd")
        val res1 = parser.parseInput("\$a\$b")
        val res2 = parser.parseInput("p\$b")
        // the line below can be used on systems where "pwd" command is present
        //val expected = listStringsWithNewlines("pwd".runCommand())
        val expected = listStringsWithNewlines(listOf(System.getProperty("user.dir")))
        assertEquals(expected, res1)
        assertEquals(expected, res2)
    }

    @Test
    fun testAssignedCommandName() {
        parser.parseInput("x=echo")
        val res = parser.parseInput("\$x text")
        assertEquals(listOf(stringWithNewline("text")), res)
    }
}