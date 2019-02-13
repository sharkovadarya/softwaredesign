package ru.hse.spb.sd.sharkova.interpreter

abstract class ParserException(message: String) : Exception(message)

class MismatchedQuotesException(message: String = "mismatched quotes") : ParserException(message)

class NoArgumentsException(message: String = "no arguments provided") : ParserException(message)

abstract class InterpreterException(message: String) : Exception(message)

class IncorrectArgumentException(commandName: String, argumentName: String, message: String) :
        InterpreterException("$commandName: $argumentName: $message")