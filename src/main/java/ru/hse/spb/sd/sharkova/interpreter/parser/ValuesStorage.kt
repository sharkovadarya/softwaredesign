package ru.hse.spb.sd.sharkova.interpreter.parser

/**
 * This class stores variable names and values assigned to them.
 */
class ValuesStorage {
    private val assignedVariables = HashMap<String, String>()

    /**
     * This method retrieves a variable value.
     * @param variable variable name
     * @return variable value if present, empty string if not
     */
    fun getValue(variable: String): String = assignedVariables[variable] ?: ""

    /**
     * This method stores a variable and the value assigned to it.
     */
    fun storeValue(variableName: String, variableValue: String) {
        assignedVariables[variableName] = variableValue
    }
}