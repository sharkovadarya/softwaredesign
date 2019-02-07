package ru.hse.spb.sd.sharkova.interpreter

interface Parser {
    fun parseInput(input: String): List<String>
}