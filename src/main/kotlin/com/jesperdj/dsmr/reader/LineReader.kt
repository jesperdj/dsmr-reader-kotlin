package com.jesperdj.dsmr.reader

import com.pi4j.io.serial.SerialDataEvent

class LineReader(private val consumer: (String) -> Unit) : (SerialDataEvent) -> Unit {

    private val builder: StringBuilder = StringBuilder(256)

    override fun invoke(event: SerialDataEvent) {
        builder.append(event.asciiString)

        var i = builder.indexOf("\r\n")
        while (i != -1) {
            consumer(builder.substring(0, i + 2))
            builder.delete(0, i + 2)
            i = builder.indexOf("\r\n")
        }
    }
}

fun lineReader(consumer: (String) -> Unit) = LineReader(consumer)
