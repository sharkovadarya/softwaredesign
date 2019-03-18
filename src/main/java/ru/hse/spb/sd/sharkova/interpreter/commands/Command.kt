package ru.hse.spb.sd.sharkova.interpreter.commands

interface Command {
    fun execute(arguments: List<String>): List<String>
}







