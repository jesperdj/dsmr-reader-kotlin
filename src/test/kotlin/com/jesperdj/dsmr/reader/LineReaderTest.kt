package com.jesperdj.dsmr.reader

import com.pi4j.io.serial.SerialDataEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class LineReaderTest {

    @Test
    fun testReceiveSingleLine() {
        val lines = mutableListOf<String>()
        val reader = lineReader { lines += it }

        reader(mockEvent("test\r\n"))

        assertThat(lines).containsExactly("test\r\n")
    }

    @Test
    fun testReceiveMultipleLines() {
        val lines = mutableListOf<String>()
        val reader = lineReader { lines += it }

        reader(mockEvent("first line\r\nsecond line\r\n"))

        assertThat(lines).containsExactly("first line\r\n", "second line\r\n")
    }

    @Test
    fun testReceiveInParts() {
        val lines = mutableListOf<String>()
        val reader = lineReader { lines += it }

        reader(mockEvent("abc"))
        reader(mockEvent("\r\ndefg"))
        reader(mockEvent("hij\r\nklmn"))
        reader(mockEvent("\r\n"))

        assertThat(lines).containsExactly("abc\r\n", "defghij\r\n", "klmn\r\n")
    }

    private fun mockEvent(text: String): SerialDataEvent {
        val event = mock(SerialDataEvent::class.java)
        `when`(event.asciiString).thenReturn(text)
        return event
    }
}
