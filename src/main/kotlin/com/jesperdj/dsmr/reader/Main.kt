package com.jesperdj.dsmr.reader

import com.pi4j.io.serial.Baud
import com.pi4j.io.serial.DataBits
import com.pi4j.io.serial.FlowControl
import com.pi4j.io.serial.Parity
import com.pi4j.io.serial.SerialConfig
import com.pi4j.io.serial.SerialDataEventListener
import com.pi4j.io.serial.SerialFactory
import com.pi4j.io.serial.StopBits
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.concurrent.fixedRateTimer

val log: Logger = ConsoleLogger()

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

    val initiate: TimerTask.() -> Unit = {
        log.debug("Opening connection")
        serial.open(serialConfig)
        log.debug("Connection opened")
    }

    val read = lineReader(messageReader {
        queue.addLast(it)
        serial.close()
        log.debug("Connection closed")
    })

    val store: TimerTask.() -> Unit = {
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

    serial.addListener(SerialDataEventListener { read(it) })

    val receiveTimer = fixedRateTimer(initialDelay = 1000L, period = 60000L, action = initiate)
    val storeTimer = fixedRateTimer(initialDelay = 5000L, period = 300000L, action = store)

    try {
        Thread.currentThread().join()
    } catch (e: InterruptedException) {
        receiveTimer.cancel()
        storeTimer.cancel()
    }
}
