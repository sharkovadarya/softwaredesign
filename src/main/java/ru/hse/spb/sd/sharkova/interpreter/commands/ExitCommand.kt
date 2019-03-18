package ru.hse.spb.sd.sharkova.interpreter.commands

object ExitCommand : Command {
    override fun execute(arguments: List<String>): List<String> {
        System.exit(0)
        return emptyList()
    }
}