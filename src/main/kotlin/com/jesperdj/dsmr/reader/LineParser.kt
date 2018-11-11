package com.jesperdj.dsmr.reader

class LineParser(private val consumer: (String) -> Unit) : (String) -> Unit {

    private val builder: StringBuilder = StringBuilder(256)

    override fun invoke(text: String) {
        builder.append(text)

        var i = builder.indexOf("\r\n")
        while (i != -1) {
            consumer(builder.substring(0, i + 2))
            builder.delete(0, i + 2)
            i = builder.indexOf("\r\n")
        }
    }
}

fun lineParser(consumer: (String) -> Unit) = LineParser(consumer)
