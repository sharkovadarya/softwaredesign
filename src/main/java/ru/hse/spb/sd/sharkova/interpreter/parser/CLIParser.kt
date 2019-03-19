package ru.hse.spb.sd.sharkova.interpreter.parser

import ru.hse.spb.sd.sharkova.interpreter.Interpreter
import ru.hse.spb.sd.sharkova.interpreter.stream.ErrorStream
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stream.OutputStream
import java.util.regex.Pattern

/**
 * This class implements Parser interface
 * and supports double/single quotes, variable assignments,
 * substitutions and piping.
 * After the input has been parsed,
 * the result of the last executed command and the accumulated errors are returned.
 */
class CLIParser : Parser {
    private val identifierRegex = Regex("[_a-z][_a-z0-9]*")
    private val substitutionRegex = Regex("\\$$identifierRegex")
    private val substitutionPattern = Pattern.compile(substitutionRegex.pattern)
    private val identifierAssignmentPattern = Pattern.compile("^$identifierRegex=")
    private val quotesPattern = """(["]+[^"]+?["]+)|([']+[^']+?[']+)"""
    private val quotesRegex = Regex(quotesPattern)

    private val valuesStorage = ValuesStorage()
    private val outputStream = OutputStream()
    private val errorStream = ErrorStream()
    private val commandProcessor = CommandProcessor(Interpreter(), outputStream, errorStream)

    /**
     * Implementation of parseInput() method of Parser interface
     * which supports double/single quotes, substitutions and piping.
     */
    override fun parseInput(input: String): List<String> {
        val words1 = splitStringIntoWords(input)
        val words = mutableListOf<String>()
        words1.forEach {
            if (it.matches(quotesRegex))
                words.add(it)
            else
                words.addAll(it.split(Regex("((?<=\\|)|(?=\\|))")).filter { str -> str.isNotEmpty() })
        }

        var commandNamePos = 0
        while (commandNamePos < words.size) {
            val command = readCommand(words, commandNamePos)
            if (command.first == null) {
                commandNamePos = command.third
                continue
            }
            val commandName = command.first ?: continue
            val arguments = command.second
            commandNamePos = command.third

            val inputStream = outputStream.toInputStream()

            parseCommand(performSubstitution(commandName), arguments, inputStream)
        }

        return outputStream.getLines() + errorStream.getLines()
    }

    private fun readCommand(words: List<String>, commandNamePosition: Int): Triple<String?, List<String>, Int> {
        var commandNamePos = commandNamePosition

        val commandName = performSubstitution(words[commandNamePos++])

        val newPosition = parseVariableAssignment(commandName, words, commandNamePos)
        if (newPosition > commandNamePos) {
            commandNamePos = newPosition
            return Triple(null, emptyList(), commandNamePos)
        }

        val args = mutableListOf<String>()
        while (commandNamePos < words.size &&
                (words[commandNamePos].matches(quotesRegex) || !words[commandNamePos].contains("|"))) {
            args.add(processArgument(words[commandNamePos]))
            commandNamePos++
        }
        commandNamePos++

        return Triple(commandName, args, commandNamePos)
    }

    private fun processArgument(arg: String): String {
        return if (!checkQuotesAbsence(arg)) {
            extractQuotes(arg)
        } else {
            performSubstitution(arg)
        }
    }

    private fun parseVariableAssignment(command: String, words: List<String>, position: Int): Int {
        val assignmentMatcher = identifierAssignmentPattern.matcher(command)
        if (assignmentMatcher.find()) {
            val variableName = assignmentMatcher.group().dropLast(1)
            val assignedValue = if (command.last() == identifierAssignmentPattern.pattern().last()) {
                if (position < words.size) {
                    words[position]
                } else {
                    ""
                }
            } else {
                command.substring(assignmentMatcher.group().length)
            }

            storeVariableAssignment(variableName, assignedValue)
            return position + 1
        }

        return position
    }

    private fun parseCommand(word: String, arguments: List<String>, inputStream: InputStream) {
        commandProcessor.processCommand(word, arguments, inputStream)
    }

    private fun performSubstitution(word: String): String {
        val result = StringBuilder()
        var startingPosition = 0
        var matcher = substitutionPattern.matcher(word)

        while (matcher.find()) {
            val substring = matcher.group()
            result.append(word.substring(startingPosition, startingPosition + matcher.start()))
            startingPosition += matcher.end()
            result.append(getSubstitution(substring))
            matcher = substitutionPattern.matcher(word.substring(startingPosition))
        }
        result.append(word.substring(startingPosition))

        return result.toString()
    }

    private fun getSubstitution(variableWithAssignment: String): String =
            valuesStorage.getValue(variableWithAssignment.drop(1))


    private fun storeVariableAssignment(variableName: String, assignedValue: String) {
        var variableValue = extractQuotes(assignedValue)
        if (substitutionRegex.matches(variableValue)) {
            variableValue = getSubstitution(variableValue)
        }
        valuesStorage.storeValue(variableName, variableValue)
    }

    private fun extractQuotes(string: String): String {
        var str = string.trim()
        var shouldPerformSubstitution = true

            fun extractQuotes(string: String, quote: Char): Triple<String, String, String>? {
            var i = string.indexOf(quote)
            var j = string.lastIndexOf(quote)
            if (i != -1 && j != -1) {
                if (i == j) {
                    return null
                }
                val precedingCharacters = string.substring(0, i)
                val proceedingCharacters = string.substring(j + 1, string.length)
                while (string[i] == quote && string[j] == quote && i < j) {
                    i++
                    j--
                }
                return Triple(precedingCharacters, string.substring(IntRange(i, j)), proceedingCharacters)
            }

            return null
        }

        while (true) {
            val extractedSingleQuotesString = extractQuotes(str, '\'')
            if (extractedSingleQuotesString != null) {
                str = extractedSingleQuotesString.first +
                        extractedSingleQuotesString.second +
                        extractedSingleQuotesString.third
            } else {
                val extractedDoubleQuotesString = extractQuotes(str, '\"')
                if (extractedDoubleQuotesString != null) {
                    val substr = if (shouldPerformSubstitution) {
                        performSubstitution(extractedDoubleQuotesString.second)
                    } else {
                        extractedDoubleQuotesString.second
                    }
                    str = extractedDoubleQuotesString.first + substr + extractedDoubleQuotesString.third
                    shouldPerformSubstitution = false
                } else {
                    break
                }
            }
        }

        return str
    }

    private fun splitStringIntoWords(str: String): List<String> {
        val wordsWithQuotes = str.splitByPattern(quotesPattern)
        if (wordsWithQuotes.isEmpty()) {
            return str.split(" ")
        }
        val lastPartWithoutQuotes = str.lastIndexOf(wordsWithQuotes.last()) + wordsWithQuotes.last().length
        val words = mutableListOf<String>()
        wordsWithQuotes.forEach { if (it[0] == '\"' || it[0] == '\'') {
            words.add(it)
        } else {
            words.addAll(it.split(" "))
        } }
        if (lastPartWithoutQuotes < str.length) {
            words.add(str.substring(lastPartWithoutQuotes))
        }
        return words.filter { it.isNotEmpty() }
    }

    private fun checkQuotesAbsence(string: String): Boolean {
        return !(string.contains("\"") || string.contains("\'"))
    }

    // method taken from https://stackoverflow.com/a/54525271/7735110
    private fun String.splitByPattern(pattern: String): List<String> {
        val indices = Regex(pattern).findAll(this)
                .map{ listOf(it.range.start, it.range.endInclusive) }.flatten().toMutableList()

        var lastIndex = 0
        return indices.mapIndexed { i, ele ->

            val end = if(i % 2 == 0) ele else ele + 1

            substring(lastIndex, end).apply {
                lastIndex = end
            }
        }
    }
}