package ru.hse.spb.sd.sharkova.interpreter.commands

import ru.hse.spb.sd.sharkova.interpreter.stream.*
import java.io.File
import java.io.FileInputStream

/**
 * This class represents the 'wc' command
 * which outputs the line, byte and word count of its input
 * received via file or pipe.
 */
class WcCommand(filenames: List<String>,
                inputStream: InputStream,
                outputStream: OutputStream,
                errorStream: ErrorStream) : Command(filenames, inputStream, outputStream, errorStream) {
    private fun <T, K, V> tripleToString(triple: Triple<T, K, V>): String {
        return "${triple.first} ${triple.second} ${triple.third}"
    }

    private fun calculatePipeWc(args: List<String>): Triple<Long, Long, Long> {
        var lineCount: Long = 0
        args.forEach { lineCount += it.split(System.lineSeparator()).size - 1 }
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

    private fun executePipeWc(lines: List<String>) =
            writeLine(tripleToString(calculatePipeWc(lines)) + System.lineSeparator())

    private fun executeFileWc(filenames: List<String>) {
        var totalLineCount: Long = 0
        var totalWordCount: Long = 0
        var totalByteCount: Long = 0


        val res = mutableListOf<String>()
        for (argument in filenames) {
            val file = File(argument)
            if (!file.exists()) {
                writeError("wc", argument, "No such file or directory")
            } else if (file.isDirectory) {
                writeError("wc", argument, "Is a directory")
            } else {
                val fileWc = calculateFileWc(file)
                totalLineCount += fileWc.first
                totalWordCount += fileWc.second
                totalByteCount += fileWc.third
                res.add(tripleToString(fileWc) + " ${file.name}${System.lineSeparator()}")
            }
        }
        if (res.size > 1) {
            res.add(tripleToString(Triple(totalLineCount, totalWordCount, totalByteCount)) +
                    " total${System.lineSeparator()}")
        }

        writeLines(res)
    }

    override fun execute() {
        if (arguments.isEmpty()) {
            executePipeWc(inputStream.readLines())
        } else {
            executeFileWc(arguments)
        }
    }
}
