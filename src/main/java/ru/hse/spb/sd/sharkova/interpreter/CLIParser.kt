package ru.hse.spb.sd.sharkova.interpreter


import com.beust.jcommander.Parameter
import com.beust.jcommander.JCommander
import ru.hse.spb.sd.sharkova.interpreter.stream.ErrorStream
import ru.hse.spb.sd.sharkova.interpreter.stream.InputStream
import ru.hse.spb.sd.sharkova.interpreter.stream.OutputStream
import java.util.regex.Pattern

class ValuesStorage {
    private val assignedVariables = HashMap<String, String>()

    fun getValue(variable: String): String = assignedVariables[variable] ?: ""

    fun storeValue(variableName: String, variableValue: String) {
        assignedVariables[variableName] = variableValue
    }
}

class CommandProcessor(private val interpreter: Interpreter,
                       private val outputStream: OutputStream,
                       private val errorStream: ErrorStream) {
    private val keywords = listOf("cat", "echo", "wc", "pwd", "exit", "grep")

    private fun processGrep(arguments: List<String>, inputStream: InputStream) {
        val args = GrepArguments()
        val argsArray = arguments.toTypedArray()
        JCommander.newBuilder().addObject(args).build().parse(*argsArray)

        val caseInsensitive = args.caseInsensitive
        val entireWord = args.entireWords
        val nLinesAfter = args.nLinesAfter
        if (args.parameters.isEmpty()) {
            throw NotEnoughArgumentsException()
        }
        val regexString = args.parameters.first()
        val filenames = args.parameters.drop(1)

        interpreter.executeGrep(regexString, filenames, caseInsensitive, entireWord, nLinesAfter,
                inputStream, outputStream, errorStream)
    }

    private fun processKeyword(word: String,
                       arguments: List<String>,
                       inputStream: InputStream) {

        when (word) {
            "cat" -> interpreter.executeCat(arguments, inputStream, outputStream, errorStream)
            "echo" -> interpreter.executeEcho(arguments, inputStream, outputStream, errorStream)
            "wc" -> interpreter.executeWc(arguments, inputStream, outputStream, errorStream)
            "pwd" -> interpreter.executePwd(inputStream, outputStream, errorStream)
            "exit" -> interpreter.executeExit(inputStream, outputStream, errorStream)
            "grep" -> processGrep(arguments, inputStream)

        }
    }

    private fun processExternalCommand(commandName: String, arguments: List<String>, inputStream: InputStream) {
        val externalCommand = mutableListOf(commandName)
        externalCommand.addAll(arguments)
        interpreter.executeExternalCommand(externalCommand, inputStream, outputStream, errorStream)
    }

    fun processCommand(commandName: String, arguments: List<String>, inputStream: InputStream) {
        if (!keywords.contains(commandName)) {
            processExternalCommand(commandName, arguments, inputStream)
        } else {
            processKeyword(commandName, arguments, inputStream)
        }
    }

    private class GrepArguments {
        @Parameter(names = ["-i"], description = "Case insensitive")
        var caseInsensitive = false

        @Parameter(names = ["-w"], description = "Match only entire words")
        var entireWords = false

        @Parameter(names = ["-A"], description = "Print n lines after match")
        var nLinesAfter = 0

        @Parameter(description = "File list", variableArity = true)
        var parameters = mutableListOf<String>()
    }
}

class CLIStringProcessor {}

/**
 * This class implements Parser interface for the following commands:
 * echo, wc, cat, grep, pwd, exit; other commands are considered external.
 * After the input has been parsed, the result of the last executed command is returned.
 * */
class CLIParser : Parser {
    private val identifierRegex = Regex("[_a-z][_a-z0-9]*")
    private val substitutionRegex = Regex("^\\$$identifierRegex")
    private val substitutionPattern = Pattern.compile(substitutionRegex.pattern)
    private val identifierAssignmentPattern = Pattern.compile("^$identifierRegex=")
    private val quotesPattern = """(["]+[^"]+?["]+)|([']+[^']+?[']+)"""
    private val quotesRegex = Regex(quotesPattern)

    private val valuesStorage = ValuesStorage()
    private val outputStream = OutputStream()
    private val errorStream = ErrorStream()
    private val commandProcessor = CommandProcessor(Interpreter(), outputStream, errorStream)

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
            }
            val commandName = command.first ?: continue
            val arguments = command.second
            commandNamePos = command.third

            val inputStream = outputStream.toInputStream()

            parseCommand(commandName, arguments, inputStream)
        }

        return outputStream.getLines() + errorStream.getLines()
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

    private fun getSubstitution(variableWithAssignment: String): String =
            valuesStorage.getValue(variableWithAssignment.drop(1))


    private fun storeVariableAssignment(variableName: String, assignedValue: String) {
        var variableValue = extractQuotes(assignedValue)
        if (substitutionRegex.matches(variableValue)) {
            variableValue = getSubstitution(variableValue)
        }
        valuesStorage.storeValue(variableName, variableValue)
    }

    private fun parseCommand(word: String, arguments: List<String>, inputStream: InputStream = InputStream.emptyStream()) {
        if (substitutionRegex.matches(word)) {
            val substitution = getSubstitution(word)
            parseCommand(substitution, arguments, inputStream)
        } else {
            commandProcessor.processCommand(word, arguments, inputStream)
        }
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