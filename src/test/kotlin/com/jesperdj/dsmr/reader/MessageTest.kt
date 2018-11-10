package com.jesperdj.dsmr.reader

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

class MessageTest {

    @Test
    fun testGetDateTime() {
        val message = Message(listOf("/ISK5\\2M550T-1012\r\n", "\r\n", "0-0:1.0.0(181104123918W)\r\n", "!2324\r\n"))
        assertThat(message.getDateTime()).isEqualTo(LocalDateTime.of(2018, 11, 4, 12, 39, 18))
    }

    @Test
    fun testGetDateTimeNoRecord() {
        val message = Message(listOf("/ISK5\\2M550T-1012\r\n", "\r\n", "!2324\r\n"))
        assertThat(message.getDateTime()).isNull()
    }
}
