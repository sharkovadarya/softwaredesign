package ru.hse.spb.sd.sharkova.interpreter.parser

/**
 * Parser interface. Parses a line of input.
 * */
interface Parser {
    /**
     * This method parses the input string and returns the output.
     * @param input string to be parsed
     * @return the command(s) output in a list of strings
     */
    fun parseInput(input: String): List<String>
}