package ru.hse.spb.sd.sharkova.interpreter

import java.util.regex.Pattern

class Parser {
    companion object {
        private val keywords = listOf("cat", "echo", "wc", "pwd", "exit")
        private val identifierRegex = Regex("[_a-z][_a-z0-9]*")
        private val substitutionRegex = Regex("^\\$$identifierRegex")
        private val identifierAssignmentPattern = Pattern.compile("^$identifierRegex=")

        private val assignedVariables = HashMap<String, String>()

        private val interpreter = Interpreter()

        private fun extractVariableAssignments(words: List<String>): Int {
            var nextCommandPosition = 0
            for (i in 0 until words.size) {
                val identifierAssignmentMatcher = identifierAssignmentPattern.matcher(words[i])
                if (identifierAssignmentMatcher.find()) {
                    val variableName = identifierAssignmentMatcher.group().dropLast(1) // delete the '='
                    var variableValue = words[0].substring(identifierAssignmentMatcher.group().length)
                    if (substitutionRegex.matches(variableValue)) {
                        variableValue = assignedVariables[variableValue.drop(1)] ?: ""
                    }
                    assignedVariables[variableName] = variableValue

                    nextCommandPosition = i + 1
                } else {
                    nextCommandPosition = i
                    break
                }
            }

            return nextCommandPosition
        }

        private fun processKeyword(word: String,
                                   arguments: List<String>,
                                   previousResult: List<String>): List<String> {
            return when (word) {
                "cat" -> { interpreter.executeCat(arguments) }
                "echo" -> { interpreter.executeEcho(arguments) }
                "wc" -> {
                    if (arguments.isEmpty() && previousResult.isEmpty()) {
                        listOf("wc: No arguments provided")
                    } else {
                        if (arguments.isNotEmpty()) {
                            interpreter.executeFileWc(arguments)
                        } else {
                            interpreter.executeWc(previousResult)
                        }
                    }
                }
                "pwd" -> { interpreter.executePwd() }
                "exit" -> { interpreter.executeExit() }
                else -> {emptyList()}
            }
        }

        private fun processExternalCommand(commandName: String, arguments: List<String>) {
            val externalCommand = mutableListOf(commandName)
            externalCommand.addAll(arguments)
            val processBuilder = ProcessBuilder(externalCommand)
            processBuilder.start()
        }

        private fun parseCommand(word: String, arguments: List<String>, previousResult: List<String>): List<String> {
            var res = listOf<String>()

            if (!keywords.contains(word)) {
                if (substitutionRegex.matches(word)) {
                    val variableName = word.drop(1)
                    val substitution = assignedVariables[variableName] ?: ""
                    res = parseCommand(substitution, emptyList(), previousResult)
                } else {
                    processExternalCommand(word, arguments)
                }
            } else {
                res = processKeyword(word, arguments, previousResult)
            }

            return res
        }

        fun parseInput(input: String): List<String> {
            val commands = input.split(Regex("[ ]*\\|[ ]*")).filter { it.isNotEmpty() }

            var pos = 0
            var list: List<String> = emptyList()
            while (pos < commands.size) {
                val command = commands[pos]
                val words = command.split(" ")

                var nextPosition = extractVariableAssignments(words)
                if (nextPosition >= words.size) {
                    pos++
                    continue
                }
                val word = words[nextPosition++]

                val arguments = mutableListOf<String>()
                while (nextPosition < words.size) {
                    var arg = words[nextPosition++]
                    if (substitutionRegex.matches(arg)) {
                        val variableName = arg.drop(1)
                        arg = assignedVariables[variableName] ?: ""
                    }
                    arguments.add(arg)
                }

                list = parseCommand(word, arguments, list)
                /*if (!keywords.contains(word)) {
                    if (substitutionRegex.matches(word)) {
                        val variableName = word.drop(1)
                        if (assignedVariables.containsKey(variableName)) {
                            val substitution = assignedVariables[variableName]
                            if (substitution != null) {
                                if (keywords.contains(substitution)) {
                                    res = processKeyword(substitution, arguments, res)
                                } else {
                                    processExternalCommand(substitution, emptyList())
                                }
                            }
                        }
                    } else {
                        processExternalCommand(word, arguments)
                    }
                } else {
                    res = processKeyword(word, arguments, res)
                }*/
                pos++
            }


            val result = mutableListOf<String>()
            result.addAll(list)
            result.addAll(interpreter.getErrorList())
            interpreter.clearErrorList()
            return result
        }
    }
}