package com.jesperdj.dsmr.reader

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Message(val records: List<String>) {

    companion object {
        private val DATE_TIME_RECORD = Regex("0-0:1.0.0\\((\\d{12})W\\)\\r\\n")
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    }

    fun getDateTime(): LocalDateTime? {
        for (record in records) {
            val result = DATE_TIME_RECORD.matchEntire(record)
            if (result != null) {
                return LocalDateTime.parse(result.groups[1]!!.value, DATE_TIME_FORMATTER)
            }
        }

        return null
    }
}
