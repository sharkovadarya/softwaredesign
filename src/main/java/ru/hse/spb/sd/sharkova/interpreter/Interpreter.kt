package ru.hse.spb.sd.sharkova.interpreter

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.nio.charset.Charset

class Interpreter {
    private val noSuchFileOrDirectory = "No such file or directory"
    private val isADirectory = "Is a directory"
    private val noSuchFileOrDirectoryTemplate = "%s: %s: $noSuchFileOrDirectory"
    private val isADirectoryTemplate = "%s: %s: $isADirectory"

    private val errorList = mutableListOf<String>()

    fun executeEcho(args: List<String>): List<String> {
        return listOf(args.joinToString(" ") + "\n")
    }

    private fun executeBlockOfCodeForAllArguments(args: List<String>,
                                                  block: (arg: String, res: MutableList<String>) -> Any): List<String> {
        val result = mutableListOf<String>()
        for (arg in args) {
            block.invoke(arg, result)
        }

        return result
    }

    private fun createBlockOfCodeForFileCommand(commandName: String, function: (File, MutableList<String>) -> Any): (String, MutableList<String>) -> Any {
        return { filename: String, result: MutableList<String> ->
            val file = File(filename)
            if (!file.exists()) {
                errorList.add(noSuchFileOrDirectoryTemplate.format(commandName, filename))
            } else if (file.isDirectory) {
                errorList.add(isADirectoryTemplate.format(commandName, filename))
            } else {
                function.invoke(file, result)
            }
        }
    }

    fun executeCat(filenames: List<String>): List<String> {
        val block = createBlockOfCodeForFileCommand("cat")
        { file: File, result: MutableList<String> ->
            val reader = InputStreamReader(FileInputStream(file), Charset.defaultCharset())
            val stringBuilder = StringBuilder()
            val buffer = CharArray(500)
            var char = reader.read(buffer)
            while (char != -1) {
                var charList = buffer.toList().filter { it != '\u0000' }
                while (charList.isNotEmpty()) {
                    val newlinePosition = charList.indexOf('\n')
                    if (newlinePosition != -1) {
                        stringBuilder.append(charList.take(newlinePosition + 1).joinToString(""))
                        result.add(stringBuilder.toString())
                        stringBuilder.setLength(0)
                        charList = charList.drop(newlinePosition + 1)
                    } else {
                        stringBuilder.append(charList.joinToString(""))
                        break
                    }
                }
                buffer.fill('\u0000')
                char = reader.read(buffer)
            }
            if (stringBuilder.isNotEmpty()) {
                result.add(stringBuilder.toString())
            }
        }

        return executeBlockOfCodeForAllArguments(filenames, block)
    }


    private fun calculateWc(args: List<String>): Triple<Long, Long, Long> {
        var lineCount: Long = 0
        args.forEach { lineCount += it.chars().filter{ ch -> ch.toChar() == '\n' }.count() }
        val wordCount = args.stream().flatMap { it.split(Regex("\\s"))
                .filter { word -> word.isNotEmpty() }.stream() }.count()
        var byteCount: Long = 0
        args.forEach { byteCount += it.toByteArray().size }
        return Triple(lineCount, wordCount, byteCount)
    }

    private fun calculateFileWc(file: File): Triple<Long, Long, Long> {
        val byteCount = file.length()
        var lineCount: Long = 0
        var wordCount: Long = 0
        file.forEachLine {
            wordCount += it.split(" ").filter { line -> line.isNotEmpty() }.size
        }
        val fileInputStream = FileInputStream(file)
        while (fileInputStream.available() > 0) {
            val char = fileInputStream.read().toChar()
            if (char == '\n') {
                lineCount += 1
            }
        }
        return Triple(lineCount, wordCount, byteCount)
    }

    private fun <T, K, V> tripleToString(triple: Triple<T, K, V>): String {
        return "${triple.first} ${triple.second} ${triple.third}"
    }

    fun executeWc(args: List<String>): List<String> {
        return listOf(tripleToString(calculateWc(args)) + "\n")
    }

    fun executeFileWc(filenames: List<String>): List<String> {
        var totalLineCount: Long = 0
        var totalWordCount: Long = 0
        var totalByteCount: Long = 0
        val block = createBlockOfCodeForFileCommand("wc")
        { file: File, result: MutableList<String> ->
            val fileWc =  calculateFileWc(file)
            totalLineCount += fileWc.first
            totalWordCount += fileWc.second
            totalByteCount += fileWc.third
            result.add(tripleToString(fileWc) + " ${file.name}\n") }

        val res = mutableListOf<String>()
        res.addAll(executeBlockOfCodeForAllArguments(filenames, block))
        if (res.size > 1) {
            res.add(tripleToString(Triple(totalLineCount, totalWordCount, totalByteCount)) + " total\n")
        }
        return res
    }

    fun executePwd(): List<String> {
        return listOf(System.getProperty("user.dir"))
    }

    fun executeExit(): List<String> {
        System.exit(0)
        return emptyList()
    }

    fun getErrorList(): List<String> = errorList

    fun clearErrorList() = errorList.clear()
}