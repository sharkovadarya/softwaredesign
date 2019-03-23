package ru.hse.spb.sd.sharkova.interpreter

import ru.hse.spb.sd.sharkova.interpreter.parser.CLIParser

/**
 * This method reads input, parses it, and executes its commands.
 */
fun main() {
    val parser = CLIParser()

    while (true) {
        val command = readLine() ?: return
        val res = parser.parseInput(command)
        res.forEach { print(it) }
    }
}