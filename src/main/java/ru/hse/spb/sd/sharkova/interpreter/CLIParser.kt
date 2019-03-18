package ru.hse.spb.sd.sharkova.interpreter


import com.beust.jcommander.Parameter
import com.beust.jcommander.JCommander
import java.util.regex.Pattern

/**
 * This class implements Parser interface for the following commands:
 * echo, wc, cat, grep, pwd, exit; other commands are considered external.
 * After the input has been parsed, the result of the last executed command is returned.
 * */
class CLIParser : Parser {
    private val keywords = listOf("cat", "echo", "wc", "pwd", "exit", "grep")
    private val identifierRegex = Regex("[_a-z][_a-z0-9]*")
    private val substitutionRegex = Regex("^\\$$identifierRegex")
    private val substitutionPattern = Pattern.compile("\\$$identifierRegex")
    private val identifierAssignmentPattern = Pattern.compile("^$identifierRegex=")
    private val quotesPattern = """(["]+[^"]+?["]+)|([']+[^']+?[']+)"""

    private val assignedVariables = HashMap<String, String>()
    private val errorList = mutableListOf<String>()

    private val interpreter = Interpreter()


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

    override fun parseInput(input: String): List<String> {
        val words1 = splitStringIntoWords(input)
        val words = mutableListOf<String>()
        words1.forEach {
            if (it.matches(Regex(quotesPattern)))
                words.add(it)
            else
                words.addAll(it.split(Regex("((?<=\\|)|(?=\\|))")).filter { str -> str.isNotEmpty() })
        }

        var commandNamePos = 0
        var result = emptyList<String>()
        while (commandNamePos < words.size) {
            val commandName = performSubstitution(words[commandNamePos++])

            val assignmentMatcher = identifierAssignmentPattern.matcher(commandName)
            if (assignmentMatcher.find()) {
                val variableName = assignmentMatcher.group().dropLast(1)
                // the assigned value is inside the quotes
                // so it became the next word after the splitting
                val assignedValue = if (commandName.last() == identifierAssignmentPattern.pattern().last()) {
                    if (commandNamePos < words.size) {
                        words[commandNamePos]
                    } else {
                        // TODO handle this
                        throw Exception()
                    }
                } else {
                    commandName.substring(assignmentMatcher.group().length)
                }

                storeVariableAssignment(variableName, assignedValue)
                commandNamePos++
                continue
            }

            val args = mutableListOf<String>()
            while (commandNamePos < words.size &&
                    (words[commandNamePos].matches(Regex(quotesPattern)) || !words[commandNamePos].contains("|"))) {
                args.add(processArgument(words[commandNamePos]))
                commandNamePos++
            }
            commandNamePos++
            result =
                    if (commandName.matches(Regex(quotesPattern)))
                        processExternalCommand(commandName, args)
                    else
                        parseCommand(commandName, args, result)
        }


        return result

    }

    private fun processArgument(arg: String): String {
        return if (!checkQuotesAbsence(arg)) {
            extractQuotes(arg)
        } else {
            performSubstitution(arg)
        }
    }

    /*override fun parseInput(input: String): List<String> {
        val commands = input.split(Regex("[\\s]*\\|[\\s]*")).filter { it.isNotEmpty() }

        var pos = 0
        var list: List<String> = emptyList()
        while (pos < commands.size) {
            val command = commands[pos]
            val str = extractQuotesFromEntireCommand(command)
            // if it was in quotes then it should be interpreted as a whole command
            val executeAsExternalCommand = str != command

            val words = splitStringIntoWords(str)
            val wh = mutableListOf<String>()
            words.forEach { wh.add(performSubstitution(extractQuotes(it))) }


            var nextPosition = if (!executeAsExternalCommand) extractVariableAssignments(words) else 0
            if (nextPosition >= words.size) {
                pos++
                continue
            }
            val word = performSubstitution(words[nextPosition++])

            val arguments = mutableListOf<String>()
            while (nextPosition < words.size) {
                var arg = words[nextPosition++]
                // if there are any quotes, all substitutions are performed inside quotes extraction
                arg = if (!checkQuotesAbsence(arg)) {
                    extractQuotes(arg)
                } else {
                    performSubstitution(arg)
                }
                // there should be no quotes remaining, thus the double check
                if (!checkQuotesAbsence(arg)) {
                    throw MismatchedQuotesException()
                }
                arguments.add(arg)
            }

            if (executeAsExternalCommand) {
                processExternalCommand(word, arguments)
                pos++
                continue
            }

            list = parseCommand(word, arguments, list)
            pos++
        }


        val result = mutableListOf<String>()
        result.addAll(list)
        result.addAll(errorList)
        return result
    }*/

    private fun extractQuotesFromEntireCommand(string: String): String {
        var str = string.trim()
        if (str.first() != '\"' && str.first() != '\'') {
            return str
        }
        if (str.first() == '\"' && str.last() == '\"') {
            str = performSubstitution(str)
        }
        var i = 0
        var j = str.lastIndex
        while (i <= j) {
            while (str[i] == '\"' && str[j] == '\"') {
                i++
                j--
            }
            while (str[i] == '\'' && str[j] == '\"') {
                i++
                j--
            }
            if (!(str[i] == '\"' && str[j] == '\"' || str[i] == '\'' && str[j] == '\'')) {
                break
            }
        }
        return str.substring(IntRange(i, j))
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

    private fun getSubstitution(variable: String): String = assignedVariables[variable.drop(1)] ?: ""


    private fun storeVariableAssignment(variableName: String, assignedValue: String) {
        var variableValue = extractQuotes(assignedValue)
        if (substitutionRegex.matches(variableValue)) {
            variableValue = getSubstitution(variableValue)
        }
        assignedVariables[variableName] = variableValue
    }

    private fun extractVariableAssignments(words: List<String>): Int {
        var nextCommandPosition = 0
        var i = 0
        while (i < words.size) {
            val identifierAssignmentMatcher = identifierAssignmentPattern.matcher(words[i])
            if (identifierAssignmentMatcher.find()) {
                val variableName = identifierAssignmentMatcher.group().dropLast(1) // delete the '='
                var assignedValue = words[i].substring(variableName.length + 1)
                if (assignedValue.isEmpty()) {
                    i++
                    if (i < words.size && (words[i].first() == '\"' || words[i].first() == '\'')) {
                        assignedValue = words[i]
                    }
                }

                var variableValue = extractQuotes(assignedValue)
                if (substitutionRegex.matches(variableValue)) {
                    variableValue = getSubstitution(variableValue)
                }
                assignedVariables[variableName] = variableValue

                nextCommandPosition = i + 1
            } else {
                nextCommandPosition = i
                break
            }

            i++
        }

        return nextCommandPosition
    }

    private fun processGrep(arguments: List<String>, previousResult: List<String>): List<String> {
        val args = GrepArguments()
        val argsArray = arguments.toTypedArray()
        JCommander.newBuilder().addObject(args).build().parse(*argsArray)
        val result: List<String>

        val caseInsensitive = args.caseInsensitive
        val entireWord = args.entireWords
        val nLinesAfter = args.nLinesAfter
        if (args.parameters.isEmpty()) {
            throw NotEnoughArgumentsException()
        }
        val regexString = args.parameters.first()
        val filenames = args.parameters.drop(1)

        if (filenames.isEmpty() && previousResult.isEmpty()) {
            throw NotEnoughArgumentsException("no file/text input provided")
        }

        result = if (filenames.isEmpty()) {
            interpreter.executePipeGrep(regexString, previousResult, caseInsensitive, entireWord, nLinesAfter)
        } else {
            interpreter.executeFileGrep(regexString, filenames, caseInsensitive, entireWord, nLinesAfter)
        }
        return result
    }

    private fun processKeyword(word: String,
                               arguments: List<String>,
                               previousResult: List<String>): List<String> {
        return try {
            when (word) {
                "cat" -> { if (arguments.isEmpty()) previousResult else interpreter.executeCat(arguments) }
                "echo" -> { interpreter.executeEcho(arguments) }
                "wc" -> {
                    if (arguments.isEmpty() && previousResult.isEmpty()) {
                        listOf("wc: No arguments provided")
                    } else {
                        if (arguments.isNotEmpty()) {
                            interpreter.executeFileWc(arguments)
                        } else {
                            interpreter.executePipeWc(previousResult)
                        }
                    }
                }
                "pwd" -> { interpreter.executePwd() }
                "exit" -> { interpreter.executeExit() }
                "grep" -> { processGrep(arguments, previousResult) }
                else -> {emptyList()}
            }
        } catch (e: InterpreterException) {
            val errorMessage = e.message
            if (errorMessage != null) {
                errorList.add(errorMessage)
            }
            emptyList()
        }
    }

    private fun processExternalCommand(commandName: String, arguments: List<String>): List<String> {
        val externalCommand = mutableListOf(commandName)
        externalCommand.addAll(arguments)
        interpreter.executeExternalCommand(externalCommand)
        // TODO return command output
        return emptyList()
    }

    private fun parseCommand(word: String, arguments: List<String>, previousResult: List<String>): List<String> {
        var res = listOf<String>()

        if (!keywords.contains(word)) {
            if (substitutionRegex.matches(word)) {
                val substitution = getSubstitution(word)
                res = parseCommand(substitution, emptyList(), previousResult)
            } else {
                processExternalCommand(word, arguments)
            }
        } else {
            res = processKeyword(word, arguments, previousResult)
        }

        return res
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