package ru.hse.spb.sd.sharkova.interpreter

import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class InterpreterTest {
    private val file1Lines = listOf("text\n", "line\n", "\n", "the previous line was an empty one\n")
    private val file2Lines = listOf("So long as there shall exist, by virtue of law and custom, " +
            "decrees of damnation pronounced by society, artificially creating hells amid the civilization of earth, " +
            "and adding the element of human fate to divine destiny; " +
            "so long as the three great problems of the century—the degradation of man through pauperism, " +
            "the corruption of woman through hunger, the crippling of children through lack of light—are unsolved; " +
            "so long as social asphyxia is possible in any part of the world;—in other words, " +
            "and with a still wider significance, so long as ignorance and poverty exist on earth, " +
            "books of the nature of Les Misérables cannot fail to be of use.")
    private val file1Wc = "4 9 46"
    private val file2Wc = "0 109 651"

    @Test
    fun testEcho() {
        val res = Parser.parseInput("echo text")
        assertEquals(listOf("text\n"), res)
    }

    @Test
    fun testEchoMultipleArguments() {
        val res = Parser.parseInput("echo some more text")
        assertEquals(listOf("some more text\n"), res)
    }

    @Test
    fun testEchoWithSameNameArguments() {
        val res = Parser.parseInput("echo echo echo echo")
        assertEquals(listOf("echo echo echo\n"), res)
    }

    @Test
    fun testCat() {
        val res = Parser.parseInput("cat src/test/resources/file1.txt")
        assertEquals(file1Lines, res)
    }

    @Test
    fun testCatMultipleArguments() {
        val res = Parser.parseInput("cat src/test/resources/file1.txt src/test/resources/file2.txt")
        val expected = mutableListOf<String>()
        expected.addAll(file1Lines)
        expected.addAll(file2Lines)

        assertEquals(expected, res)
    }

    @Test
    fun testOneFileWc() {
        val res1 = Parser.parseInput("wc src/test/resources/file1.txt")
        assertEquals(listOf("$file1Wc file1.txt\n"), res1)
        val res2 = Parser.parseInput("wc src/test/resources/file2.txt")
        assertEquals(listOf("$file2Wc file2.txt\n"), res2)
    }

    @Test
    fun testMultipleFilesWc() {
        val res = Parser.parseInput("wc src/test/resources/file1.txt src/test/resources/file2.txt")
        assertEquals(listOf("$file1Wc file1.txt\n", "$file2Wc file2.txt\n", "4 118 697 total\n"), res)
    }

    @Test
    fun testPwd() {
        val res = Parser.parseInput("pwd")
        val expected = "pwd".runCommand()
        assertEquals(listOf(expected), res)
    }

    // run this one separately because it literally exits
    /*@Test
    fun testExit() {
        Parser.parseInput("exit")
        // if we haven't exited the interpreter, the test will fail
        fail()
    }*/

    @Test
    fun testPipeEchoCat() {
        val res = Parser.parseInput("echo echo echo echo|cat src/test/resources/file1.txt")
        assertEquals(file1Lines, res)
    }

    @Test
    fun testPipeCatEcho() {
        val res = Parser.parseInput("cat src/test/resources/file1.txt | echo echo echo echo")
        assertEquals(listOf("echo echo echo\n"), res)
    }

    @Test
    fun testPipeCatWc() {
        val res = Parser.parseInput("cat src/test/resources/file1.txt|wc")
        assertEquals(listOf(file1Wc + "\n"), res)
    }

    @Test
    fun testPipeCatMultipleFilesWc() {
        val res = Parser.parseInput("cat src/test/resources/file1.txt src/test/resources/file2.txt   | wc")
        assertEquals(listOf("4 118 697\n"), res)
    }

    @Test
    fun testPipeEchoWc() {
        val res = Parser.parseInput("echo echo echo | wc")
        assertEquals(listOf("1 2 10\n"), res)
    }

    @Test
    fun testPipeWcEcho() {
        val res = Parser.parseInput("wc src/test/resources/file1.txt | echo echo echo")
        assertEquals(listOf("echo echo\n"), res)
    }

    @Test
    fun testPipeDoubleWc() {
        val res = Parser.parseInput("wc src/test/resources/file1.txt | wc")
        assertEquals(listOf("1 4 17\n"), res)
    }

    @Test
    fun testCatNonexistentFile() {
        val res = Parser.parseInput("cat file1.txt")
        assertEquals(listOf("cat: file1.txt: No such file or directory"), res)
    }

    @Test
    fun testCatDirectory() {
        val res = Parser.parseInput("cat src/test/resources")
        assertEquals(listOf("cat: src/test/resources: Is a directory"), res)
    }

    @Test
    fun testWcNonexistentFile() {
        val res = Parser.parseInput("wc file1.txt")
        assertEquals(listOf("wc: file1.txt: No such file or directory"), res)
    }

    @Test
    fun testWcDirectory() {
        val res = Parser.parseInput("wc src/test")
        assertEquals(listOf("wc: src/test: Is a directory"), res)
    }

    @Test
    fun testWcNonexistentFileInPipe() {
        val res = Parser.parseInput("echo some text | wc src/file1.txt | echo more text")
        assertEquals(listOf("more text\n", "wc: src/file1.txt: No such file or directory"), res)
    }

    @Test
    fun testVariableSubstitutionEcho() {
        Parser.parseInput("x=texttext")
        val res = Parser.parseInput("echo \$x")
        assertEquals(listOf("texttext\n"), res)
    }

    @Test
    fun testMultipleVariableSubstitutionEcho() {
        Parser.parseInput("x=texttext")
        Parser.parseInput("y=x")
        val res1 = Parser.parseInput("echo \$y")
        assertEquals(listOf("x\n"), res1)
        Parser.parseInput("y=\$x")
        val res2 = Parser.parseInput("echo \$y")
        assertEquals(listOf("texttext\n"), res2)
    }

    @Test
    fun testSubstituteFilenameCat() {
        val res = Parser.parseInput("x=src/test/resources/file1.txt | cat \$x")
        assertEquals(file1Lines, res)
    }

    @Test
    fun testSubstitutePwd() {
        val res = Parser.parseInput("x=pwd \$x")
        val expected = "pwd".runCommand()
        assertEquals(listOf(expected), res)
    }

    @Test
    fun testNoIdentifierDollarSignEcho() {
        val res = Parser.parseInput("echo \$не_идентификатор")
        assertEquals(listOf("\$не_идентификатор\n"), res)
    }

    @Test
    fun testDoubleQuotesEcho() {
        val res = Parser.parseInput("echo \"text with spaces\"")
        assertEquals(listOf("text with spaces\n"), res)
    }

    @Test
    fun testMultipleDoubleQuotesEcho() {
        val res = Parser.parseInput("echo \"\"text with spaces\"\" and more \"and even more\"")
        assertEquals(listOf("text with spaces and more and even more\n"), res)
    }

    @Test (expected = MismatchedQuotesException::class)
    fun testMismatchedDoubleQuotesEcho() {
        Parser.parseInput("echo \"\"x\"")
    }

    @Test
    fun testDoubleQuotesWithSubstitutionEcho() {
        Parser.parseInput("x=text")
        val res1 = Parser.parseInput("echo \"\$x\"")
        assertEquals(listOf("text\n"), res1)
        val res2 = Parser.parseInput("echo \"$\"x\"\"")
        assertEquals(listOf("\$ x\n"), res2)
    }

    @Test
    fun testSingleQuotes() {
        val res = Parser.parseInput("echo \'text\'")
        assertEquals(listOf("text\n"), res)
    }

    @Test
    fun testNestedQuotes() {
        val res1 = Parser.parseInput("echo \'\"text\"\'")
        assertEquals(listOf("text\n"), res1)
        val res2 = Parser.parseInput("echo \"\'text\'\"")
        assertEquals(listOf("text\n"), res2)
    }

    @Test
    fun testSingleQuotesWithSubstitutionEcho() {
        Parser.parseInput("x=text")
        val res = Parser.parseInput("echo \'\$x\'")
        assertEquals(listOf("\$x\n"), res)
    }

    // method taken from: https://stackoverflow.com/a/41495542/7735110
    private fun String.runCommand(): String {
        try {
            val parts = this.split("\\s".toRegex())
            val process = ProcessBuilder(*parts.toTypedArray())
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()
            return process.inputStream.bufferedReader().readLine()
        } catch (e: IOException) {
            e.printStackTrace()
            fail()
        }

        return ""
    }
}