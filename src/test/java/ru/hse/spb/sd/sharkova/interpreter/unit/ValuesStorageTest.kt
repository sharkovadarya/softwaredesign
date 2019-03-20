package ru.hse.spb.sd.sharkova.interpreter.unit

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.sd.sharkova.interpreter.parser.ValuesStorage

class ValuesStorageTest {
    @Test
    fun testRetrieveStoredValue() {
        val storage = ValuesStorage()
        storage.storeValue("var", "val")
        assertEquals("val", storage.getValue("var"))
    }

    @Test
    fun testRetrieveNonexistentValue() {
        val storage = ValuesStorage()
        storage.storeValue("var", "val")
        assertEquals("", storage.getValue("Var"))
    }

    @Test
    fun testReplaceCurrentValue() {
        val storage = ValuesStorage()
        storage.storeValue("var", "val")
        assertEquals("val", storage.getValue("var"))
        storage.storeValue("var", "Val")
        assertEquals("Val", storage.getValue("var"))
    }
}