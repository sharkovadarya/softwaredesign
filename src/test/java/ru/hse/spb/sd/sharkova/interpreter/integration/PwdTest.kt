package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert
import org.junit.Test

class PwdTest : InterpreterTest() {
    @Test
    fun testPwd() {
        val res = parser.parseInput("pwd")
        Assert.assertEquals("pwd".runCommand(), res)
    }
}