package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.stringWithNewline

class EchoTest : InterpreterTest() {
    @Test
    fun testEcho() {
        val res = parser.parseInput("echo text")
        assertEquals(listOf(stringWithNewline("text")), res)
    }

    @Test
    fun testEchoMultipleArguments() {
        val res = parser.parseInput("echo some more text")
        assertEquals(listOf(stringWithNewline("some more text")), res)
    }

    @Test
    fun testEchoWithSameNameArguments() {
        val res = parser.parseInput("echo echo echo echo")
        assertEquals(listOf(stringWithNewline("echo echo echo")), res)
    }
}