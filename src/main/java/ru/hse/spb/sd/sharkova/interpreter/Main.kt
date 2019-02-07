package ru.hse.spb.sd.sharkova.interpreter

/**
 * This method reads input, parses it, and executes its commands.
 */
fun main(args: Array<String>) {
    val parser = CLIParser()

    while (true) {
        val command = readLine() ?: return
        try {
            val res = parser.parseInput(command)
            res.forEach { println(it) }
        } catch (e: ParserException) {
            println(e.message)
        }
    }
}