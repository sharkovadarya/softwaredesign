package ru.hse.spb.sd.sharkova.interpreter.integration

// uncomment tests if needed
class MiscellaneousTest : InterpreterTest() {
    // run this one separately because it literally exits
    /*@Test
    fun testExit() {
        parser.parseInput("exit")
        // if we haven't exited the interpreter, the test will fail
        fail()
    }*/

    // run these on systems where 'ls' command is present
    /*@Test
    fun testExternalCommand() {
        val res = parser.parseInput("ls src")
        assertEquals("ls src".runCommand().sorted(), res.sorted())
    }

    @Test
    fun testExternalCommandErrorOutput() {
        val res = parser.parseInput("ls srd")
        assertEquals("ls srd".runCommand().sorted(), res.sorted())
    }*/
}