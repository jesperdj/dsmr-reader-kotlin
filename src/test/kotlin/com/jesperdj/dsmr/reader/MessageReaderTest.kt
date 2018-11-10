package com.jesperdj.dsmr.reader

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MessageReaderTest {

    @Test
    fun testReadMessage() {
        val messages = mutableListOf<Message>()
        val reader = messageReader { messages += it }

        reader("junk before message\r\n")
        reader("/ISK5\\2M550T-1012\r\n")
        reader("\r\n")
        reader("1-3:0.2.8(50)\r\n")
        reader("!9731\r\n")
        reader("junk after message\r\n")

        assertThat(messages).hasSize(1)
        assertThat(messages[0].records).containsExactly(
                "/ISK5\\2M550T-1012\r\n",
                "\r\n",
                "1-3:0.2.8(50)\r\n",
                "!9731\r\n")
    }
}
