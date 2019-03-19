package ru.hse.spb.sd.sharkova.interpreter

import ru.hse.spb.sd.sharkova.interpreter.commands.*
import ru.hse.spb.sd.sharkova.interpreter.stream.*
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
     */
    fun executeEcho(args: List<String>,
                    inputStream: InputStream,
                    outputStream: OutputStream,
                    errorStream: ErrorStream) =
            EchoCommand(args, inputStream, outputStream, errorStream).execute()

    /**
     * This method executes the 'cat' command,
     * which outputs the content of the provided files.
     * @param filenames list of files
     */
    fun executeCat(filenames: List<String>,
                   inputStream: InputStream,
                   outputStream: OutputStream,
                   errorStream: ErrorStream) =
            CatCommand(filenames, inputStream, outputStream, errorStream).execute()

    // TODO javadoc
    /***/
    fun executeWc(args: List<String>,
                  inputStream: InputStream,
                  outputStream: OutputStream,
                  errorStream: ErrorStream) {
        WcCommand(args, inputStream, outputStream, errorStream).execute()
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
    fun executeGrep(regexString: String,
                    lines: List<String>,
                    caseInsensitive: Boolean = false,
                    entireWord: Boolean = false,
                    nLinesAfter: Int = 0,
                    inputStream: InputStream,
                    outputStream: OutputStream,
                    errorStream: ErrorStream) {
        val grepCommand = GrepCommand(lines, inputStream, outputStream, errorStream,
                regexString, caseInsensitive, entireWord, nLinesAfter)
        grepCommand.execute()
    }

    /**
     * This method executes the 'pwd' command,
     * which outputs the working directory.
     * @return a list containing a single string which represents the current directory
     */
    fun executePwd(inputStream: InputStream,
                   outputStream: OutputStream,
                   errorStream: ErrorStream) =
            PwdCommand(emptyList(), inputStream, outputStream, errorStream).execute()

    /**
     * This method executed the 'exit' command,
     * which stops the interpreter.
     */
    fun executeExit(inputStream: InputStream,
                    outputStream: OutputStream,
                    errorStream: ErrorStream) =
            ExitCommand(emptyList(), inputStream, outputStream, errorStream).execute()

    /**
     * This method executes a command which isn't among the commands listed above.
     * @param externalCommand command and its arguments
     */
    fun executeExternalCommand(externalCommand: List<String>,
                               inputStream: InputStream,
                               outputStream: OutputStream,
                               errorStream: ErrorStream) =
            ExternalCommand(externalCommand, inputStream, outputStream, errorStream).execute()
}