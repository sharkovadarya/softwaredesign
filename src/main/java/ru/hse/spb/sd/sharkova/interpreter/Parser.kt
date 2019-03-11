package ru.hse.spb.sd.sharkova.interpreter

/**
 * Parser interface. Parses a line of input.
 * */
interface Parser {
    fun parseInput(input: String): List<String>
}