package ru.hse.spb.sd.sharkova.interpreter.integration

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.listStringsWithNewlines

class PwdTest : InterpreterTest() {
    // this can be run on systems with 'pwd' command present
    /*@Test
    fun testPwdRunOnSystem() {
        val res = parser.parseInput("pwd")
        assertEquals(listStringsWithNewlines("pwd".runCommand()), res)
    }*/

    @Test
    fun testPwd() {
        val res = parser.parseInput("pwd")
        assertEquals(listStringsWithNewlines(listOf(System.getProperty("user.dir"))), res)
    }
}