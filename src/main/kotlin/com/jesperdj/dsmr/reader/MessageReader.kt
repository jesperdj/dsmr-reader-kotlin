package com.jesperdj.dsmr.reader

class MessageReader(private val consumer: (Message) -> Unit) : (String) -> Unit {

    companion object {
        private val START_OF_MESSAGE = Regex("/.{3}5.*\\r\\n")
        private val END_OF_MESSAGE = Regex("!\\p{XDigit}{4}\\r\\n")
    }

    private val records = mutableListOf<String>()

    override fun invoke(line: String) {
        if (records.isEmpty()) {
            if (line matches START_OF_MESSAGE) {
                records += line
            }
        } else {
            records += line
            if (line matches END_OF_MESSAGE) {
                consumer(Message(records.toList()))
                records.clear()
            }
        }
    }
}

fun messageReader(consumer: (Message) -> Unit) = MessageReader(consumer)
