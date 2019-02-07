package ru.hse.spb.sd.sharkova.interpreter

interface CLICommand {
    fun execute(arguments: List<String>): List<String>
}
