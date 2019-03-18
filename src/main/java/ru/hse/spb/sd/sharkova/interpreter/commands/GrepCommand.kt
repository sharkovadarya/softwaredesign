package ru.hse.spb.sd.sharkova.interpreter.commands

object GrepCommand : Command {
    private const val generalRegexTemplate = "(?s).*%s.*"
    private const val caseInsensitiveRegexTemplate = "(?i)%s"
    private const val wholeWordRegexTemplate = "\\b%s\\b"

    private var caseInsensitive = false
    private var entireWord = false
    private var nLinesAfter = 0
    private lateinit var regexString: String

    private fun grep(regexString: String, lines: List<String>): List<String> {
        val result = mutableListOf<String>()
        val regexForUsage = if (caseInsensitive) {
            Regex(caseInsensitiveRegexTemplate.format(generalRegexTemplate.format(regexString)))
        } else {
            Regex(generalRegexTemplate.format(regexString))
        }
        lines.forEach { if (regexForUsage.matches(it)) result.add(it) }
        return result
    }

    private fun wholeWordGrep(regexString: String, lines: List<String>): List<String> {
        return grep(wholeWordRegexTemplate.format(regexString), lines)
    }

    private fun afterMatchGrep(regexString: String, lines: List<String>): List<String> {
        val resultingLines = LinkedHashSet<String>()
        val newRegexString = generalRegexTemplate
                .format(if (entireWord) wholeWordRegexTemplate.format(regexString) else regexString)
        val regexForUsage = if (caseInsensitive) Regex(caseInsensitiveRegexTemplate.format(newRegexString))
        else Regex(newRegexString)
        for (i in 0 until lines.size) {
            if (regexForUsage.matches(lines[i])) {
                resultingLines.add(lines[i])
                for (j in 1..nLinesAfter) {
                    if (i + j >= lines.size) {
                        break
                    }
                    resultingLines.add(lines[i + j])
                }
            }
        }
        return resultingLines.toList()
    }

    override fun execute(arguments: List<String>): List<String> {
        var result = emptyList<String>()
        try {
            if (nLinesAfter > 0) {
                result = afterMatchGrep(regexString, arguments)
            } else {
                result = grep(regexString, arguments)
                if (entireWord) {
                    result = wholeWordGrep(regexString, result)
                }
            }

        } catch (e: UninitializedPropertyAccessException) {}

        return result
    }

    fun setArguments(caseInsensitive: Boolean, entireWord: Boolean, nLinesAfter: Int, regexString: String) {
        this.caseInsensitive = caseInsensitive
        this.entireWord = entireWord
        this.nLinesAfter = nLinesAfter
        this.regexString = regexString
    }

    fun clearArguments() {
        caseInsensitive = false
        entireWord = false
        nLinesAfter = 0
    }
}