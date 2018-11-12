package com.jesperdj.dsmr.reader

class MessageParser(private val consumer: (Message) -> Unit) : (String) -> Unit {

    companion object {
        private val START_OF_MESSAGE = Regex("/.{3}5.*\\r\\n")
        private val END_OF_MESSAGE = Regex("!\\p{XDigit}{4}\\r\\n")
    }

    private val records = mutableListOf<String>()

    override fun invoke(line: String) {
        if (records.isEmpty()) {
            if (line matches START_OF_MESSAGE) {
                log.trace("Start of message")
                records += line
            } else {
                log.debug("Discarding line outside of message: {}", line)
            }
        } else {
            records += line
            if (line matches END_OF_MESSAGE) {
                log.trace("End of message; {} records", records.size)
                consumer(Message(records.toList()))
                records.clear()
            }
        }
    }
}

fun messageParser(consumer: (Message) -> Unit) = MessageParser(consumer)
