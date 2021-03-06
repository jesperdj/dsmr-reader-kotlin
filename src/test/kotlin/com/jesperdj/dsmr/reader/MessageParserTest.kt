package com.jesperdj.dsmr.reader

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MessageParserTest {

    @Test
    fun testParseMessage() {
        val messages = mutableListOf<Message>()
        val parse = messageParser { messages += it }

        parse("junk before message\r\n")
        parse("/ISK5\\2M550T-1012\r\n")
        parse("\r\n")
        parse("1-3:0.2.8(50)\r\n")
        parse("!9731\r\n")
        parse("junk after message\r\n")

        assertThat(messages).hasSize(1)
        assertThat(messages[0].records).containsExactly(
                "/ISK5\\2M550T-1012\r\n",
                "\r\n",
                "1-3:0.2.8(50)\r\n",
                "!9731\r\n")
    }
}
