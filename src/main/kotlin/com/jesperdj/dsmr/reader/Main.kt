package com.jesperdj.dsmr.reader

import com.pi4j.io.serial.Baud
import com.pi4j.io.serial.DataBits
import com.pi4j.io.serial.FlowControl
import com.pi4j.io.serial.Parity
import com.pi4j.io.serial.SerialConfig
import com.pi4j.io.serial.SerialDataEvent
import com.pi4j.io.serial.SerialDataEventListener
import com.pi4j.io.serial.SerialFactory
import com.pi4j.io.serial.StopBits
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

val log: Logger = LogManager.getLogger()

fun main(args: Array<String>) {
    log.info("DSMR Reader")

    val serial = SerialFactory.createInstance()

    val serialConfig = SerialConfig()
            .device("/dev/ttyUSB0")
            .baud(Baud._115200)
            .dataBits(DataBits._8)
            .parity(Parity.NONE)
            .stopBits(StopBits._1)
            .flowControl(FlowControl.NONE)

    val queue: Deque<Message> = ConcurrentLinkedDeque()

    val parseAndEnqueue = lineParser(messageParser { message -> queue.addLast(message) })

    serial.addListener(SerialDataEventListener { event: SerialDataEvent ->
        try {
            parseAndEnqueue(event.asciiString)
        } catch (e: IOException) {
            log.error("Error while reading data", e)
        }
    })

    try {
        log.debug("Opening connection")
        serial.open(serialConfig)
        log.debug("Connection opened")
    } catch (e: IOException) {
        log.fatal("Error while opening connection", e)
        exitProcess(1)
    }

    val timer = fixedRateTimer(initialDelay = 10000L, period = 300000L) {
        log.debug("Saving messages")

        var count = 0
        var lastFilename: String? = null
        var out: BufferedWriter? = null

        var message = queue.pollFirst()
        while (message != null) {
            val filename = "%1\$tY%1\$tm%1\$td.dsmr".format(message.getDateTime())
            if (filename != lastFilename) {
                out?.close()
                out = OutputStreamWriter(FileOutputStream(filename, true), StandardCharsets.US_ASCII).buffered()
                log.info("Writing to file: $filename")
                lastFilename = filename
            }

            message.records.forEach(out!!::write)
            count++

            message = queue.pollFirst()
        }

        out?.close()
        log.info("Saved $count messages")
    }

    try {
        Thread.currentThread().join()
    } catch (e: InterruptedException) {
        timer.cancel()
    }
}
