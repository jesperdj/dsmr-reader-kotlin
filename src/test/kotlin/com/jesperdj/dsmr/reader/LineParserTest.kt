package com.jesperdj.dsmr.reader

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LineParserTest {

    @Test
    fun testParseSingleLine() {
        val lines = mutableListOf<String>()
        val parse = lineParser { lines += it }

        parse("test\r\n")

        assertThat(lines).containsExactly("test\r\n")
    }

    @Test
    fun testParseMultipleLines() {
        val lines = mutableListOf<String>()
        val parse = lineParser { lines += it }

        parse("first line\r\nsecond line\r\n")

        assertThat(lines).containsExactly("first line\r\n", "second line\r\n")
    }

    @Test
    fun testParseInParts() {
        val lines = mutableListOf<String>()
        val parse = lineParser { lines += it }

        parse("abc")
        parse("\r\ndefg")
        parse("hij\r\nklmn")
        parse("\r\n")

        assertThat(lines).containsExactly("abc\r\n", "defghij\r\n", "klmn\r\n")
    }
}
