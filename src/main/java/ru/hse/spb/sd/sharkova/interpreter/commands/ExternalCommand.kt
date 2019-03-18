package ru.hse.spb.sd.sharkova.interpreter.commands

object ExternalCommand: Command {
    override fun execute(arguments: List<String>): List<String> {
        val processBuilder = ProcessBuilder(arguments)
        processBuilder.start()
        return emptyList()
    }
}