package ru.hse.spb.sd.sharkova.interpreter

abstract class ParserException(message: String) : Exception(message)

class MismatchedQuotesException(message: String = "mismatched quotes") : ParserException(message)