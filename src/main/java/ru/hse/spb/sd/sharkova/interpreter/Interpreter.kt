package ru.hse.spb.sd.sharkova.interpreter

import ru.hse.spb.sd.sharkova.interpreter.commands.*
import ru.hse.spb.sd.sharkova.interpreter.stream.*

/**
 * This class interprets and executes commands with arguments.
 * Supported commands: echo, cat, wc, pwd, exit;
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

    /**
     * This method executes the 'wc' command,
     * which output the line, word and byte count for input (file or previous command result)
     * @param filenames list of files
     */
    fun executeWc(filenames: List<String>,
                  inputStream: InputStream,
                  outputStream: OutputStream,
                  errorStream: ErrorStream) {
        WcCommand(filenames, inputStream, outputStream, errorStream).execute()
    }

    /**
     * This method executes the 'pwd' command,
     * which outputs the working directory.
     * @return a list containing a single string which represents the current directory
     */
    fun executePwd(inputStream: InputStream,
                   outputStream: OutputStream,
                   errorStream: ErrorStream) =
            PwdCommand(inputStream, outputStream, errorStream).execute()

    /**
     * This method executed the 'exit' command,
     * which stops the interpreter.
     */
    fun executeExit(inputStream: InputStream,
                    outputStream: OutputStream,
                    errorStream: ErrorStream) =
            ExitCommand(inputStream, outputStream, errorStream).execute()

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
