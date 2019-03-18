package ru.hse.spb.sd.sharkova.interpreter.commands

object EchoCommand : Command {
    override fun execute(arguments: List<String>): List<String> {
        return listOf(arguments.joinToString(" ") + System.lineSeparator())
    }
}
