package ru.hse.spb.sd.sharkova.interpreter.commands

object PwdCommand : Command {
    override fun execute(arguments: List<String>): List<String> {
        return listOf(System.getProperty("user.dir"))
    }
}
