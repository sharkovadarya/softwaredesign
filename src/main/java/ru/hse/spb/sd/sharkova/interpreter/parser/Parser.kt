package ru.hse.spb.sd.sharkova.interpreter.parser

/**
 * Parser interface. Parses a line of input.
 * */
interface Parser {
    fun parseInput(input: String): List<String>
}