package ru.hse.spb.sd.sharkova.interpreter

import ru.hse.spb.sd.sharkova.interpreter.commands.*
import java.io.File

/**
 * This class interprets and executes commands with arguments.
 * Supported commands: echo, cat, wc, grep, pwd, exit;
 * other commands are handled as external commands.
 * */
class Interpreter {

    /**
     * This method executes the 'echo' command,
     * which outputs its arguments and adds a newline at the end.
     * @param args list of command arguments
     * @return a list with a single string which contains arguments joined by a space with a newline at the end
     */
    fun executeEcho(args: List<String>): List<String> {
        return EchoCommand.execute(args)
    }

    /**
     * This method executes the 'cat' command,
     * which outputs the content of the provided files.
     * @param filenames list of files
     * @return list of lines from provided files
     */
    fun executeCat(filenames: List<String>): List<String> {
        return CatCommand.execute(filenames)
    }

    /**
     * This method executes the 'wc' command for input received via pipe.
     * 'wc' outputs the number of lines, words, and bytes in the input.
     * @param args input lines
     * @return a line containing the number of lines, words, and bytes in the input with a newline attached
     */
    fun executePipeWc(args: List<String>): List<String> {
        return WcPipeCommand.execute(args)
    }

    /**
     * This method executed the 'wc' command for files.
     * 'wc' outputs the number of lines, words, and bytes in the input.
     * @param filenames list of files
     * @return a list of lines containing line, word, and byte count for each file and total count
     */
    fun executeFileWc(filenames: List<String>): List<String> {
        return WcFileCommand.execute(filenames)
    }

    /**
     * This method executes the 'grep' command for input received via pipe.
     * 'grep' outputs lines that contain the provided regular expressions.
     * @param regexString regex to match
     * @param lines input lines
     * @param caseInsensitive makes matching case insensitive
     * @param entireWord only match the exact whole word, no substrings
     * @param nLinesAfter output extra n lines after
     * @return list of lines that contain the regex
     */
    fun executePipeGrep(regexString: String, lines: List<String>,
                        caseInsensitive: Boolean = false, entireWord: Boolean = false,
                        nLinesAfter: Int = 0): List<String> {
        GrepCommand.setArguments(caseInsensitive, entireWord, nLinesAfter, regexString)
        val result = GrepCommand.execute(lines)
        GrepCommand.clearArguments()
        return result
    }

    /**
     * This method executes the 'grep' command for file input.
     * 'grep' outputs lines that contain the provided regular expressions.
     * @param regexString regex to match
     * @param filenames files for matching
     * @param caseInsensitive makes matching case insensitive
     * @param entireWord only match the exact whole word, no substrings
     * @param nLinesAfter output extra n lines after
     * @return list of lines that contain the regex
     */
    fun executeFileGrep(regexString: String, filenames: List<String>,
                        caseInsensitive: Boolean = false, entireWord: Boolean = false,
                        nLinesAfter: Int = 0): List<String> {
        val result = mutableListOf<String>()

        GrepCommand.setArguments(caseInsensitive, entireWord, nLinesAfter, regexString)
        for (filename in filenames) {
            val file = File(filename)
            if (!file.exists()) {
                throw IncorrectArgumentException("grep", filename, "No such file or directory")
            } else if (file.isDirectory) {
                throw IncorrectArgumentException("grep", filename, "Is a directory")
            } else {
                val fileResult = GrepCommand.execute(file.readLines())
                if (filenames.size > 1) {
                    fileResult.forEach { result.add("${file.name}:$it") }
                } else {
                    fileResult.forEach { result.add(it) }
                }
            }
        }

        GrepCommand.clearArguments()

        return result
    }

    /**
     * This method executes the 'pwd' command,
     * which outputs the working directory.
     * @return a list containing a single string which represents the current directory
     */
    fun executePwd(): List<String> {
        return PwdCommand.execute(emptyList())
    }

    /**
     * This method executed the 'exit' command,
     * which stops the interpreter.
     */
    fun executeExit(): List<String> {
        return ExitCommand.execute(emptyList())
    }

    /**
     * This method executes a command which isn't among the commands listed above.
     * @param externalCommand command and its arguments
     */
    fun executeExternalCommand(externalCommand: List<String>): List<String> {
        return ExternalCommand.execute(externalCommand)
    }
}