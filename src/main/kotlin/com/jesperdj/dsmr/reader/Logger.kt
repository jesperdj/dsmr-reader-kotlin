package com.jesperdj.dsmr.reader

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface Logger {
    enum class Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    fun log(level: Level, msg: String)

    fun trace(msg: String) {
        log(Level.TRACE, msg)
    }

    fun debug(msg: String) {
        log(Level.DEBUG, msg)
    }

    fun info(msg: String) {
        log(Level.INFO, msg)
    }

    fun warn(msg: String) {
        log(Level.WARN, msg)
    }

    fun error(msg: String) {
        log(Level.ERROR, msg)
    }
}

class ConsoleLogger : Logger {

    companion object {
        private val LOG_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    }

    override fun log(level: Logger.Level, msg: String) {
        print("%s %-5s %s%n".format(LOG_TIMESTAMP_FORMATTER.format(LocalDateTime.now()), level, msg))
    }
}
