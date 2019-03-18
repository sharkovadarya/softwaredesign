package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.IncorrectArgumentException
import java.io.File
import java.io.FileInputStream

abstract class WcCommand : Command {
    protected fun <T, K, V> tripleToString(triple: Triple<T, K, V>): String {
        return "${triple.first} ${triple.second} ${triple.third}"
    }
}

object WcPipeCommand : WcCommand() {
    private fun calculateWc(args: List<String>): Triple<Long, Long, Long> {
        var lineCount: Long = 0
        args.forEach { lineCount += it.split(System.lineSeparator()).size - 1 }
        val wordCount = args.stream().flatMap { it.split(Regex("\\s"))
                .filter { word -> word.isNotEmpty() }.stream() }.count()
        var byteCount: Long = 0
        args.forEach { byteCount += it.toByteArray().size }
        return Triple(lineCount, wordCount, byteCount)
    }

    override fun execute(arguments: List<String>): List<String> {
        return listOf(tripleToString(calculateWc(arguments)) + System.lineSeparator())
    }
}

object WcFileCommand : WcCommand() {
    private fun calculateWc(file: File): Triple<Long, Long, Long> {
        val byteCount = file.length()
        var lineCount: Long = 0
        var wordCount: Long = 0
        file.forEachLine {
            wordCount += it.split(" ").filter { line -> line.isNotEmpty() }.size
        }
        val fileInputStream = FileInputStream(file)
        while (fileInputStream.available() > 0) {
            val char = fileInputStream.read().toChar()
            // I can't use System.lineSeparator() because "\r\n" takes two characters;
            // I can't use File.readText() and split by System.lineSeparator()
            // because I can't read huge files into a single string.
            if (char == '\n') {
                lineCount += 1
            } else if (char == '\r') {
                val nextChar = fileInputStream.read().toChar()
                if (nextChar == '\n') {
                    lineCount += 1
                }
            }
        }
        return Triple(lineCount, wordCount, byteCount)
    }

    override fun execute(arguments: List<String>): List<String> {
        var totalLineCount: Long = 0
        var totalWordCount: Long = 0
        var totalByteCount: Long = 0


        val res = mutableListOf<String>()
        for (argument in arguments) {
            val file = File(argument)
            if (!file.exists()) {
                throw IncorrectArgumentException("wc", argument, "No such file or directory")
            } else if (file.isDirectory) {
                throw IncorrectArgumentException("wc", argument, "Is a directory")
            } else {
                val fileWc = calculateWc(file)
                totalLineCount += fileWc.first
                totalWordCount += fileWc.second
                totalByteCount += fileWc.third
                res.add(tripleToString(fileWc) + " ${file.name}\n")
            }
        }
        if (res.size > 1) {
            res.add(tripleToString(Triple(totalLineCount, totalWordCount, totalByteCount)) + " total\n")
        }
        return res
    }
}

