package ru.hse.spb.sd.sharkova.interpreter

/**
 * This class represents exceptions thrown during input parsing
 * due to incorrect input format.
 * */
abstract class ParserException(message: String) : Exception(message)

class MismatchedQuotesException(message: String = "mismatched quotes") : ParserException(message)

class NotEnoughArgumentsException(message: String = "no arguments provided") : ParserException(message)

/**
 * This class represents exceptions thrown during interpretation
 * due to incorrect arguments, etc.
 * */
abstract class InterpreterException(message: String) : Exception(message)

class IncorrectArgumentException(commandName: String, argumentName: String, message: String) :
        InterpreterException("$commandName: $argumentName: $message")