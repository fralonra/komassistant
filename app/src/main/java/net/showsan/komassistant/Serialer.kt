package net.showsan.komassistant

import android.serialport.SerialPort
import android.util.Log
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*

/**
 * Created by zoron on 17-11-21.
 */
abstract class Serialer {
    private var serialPort: SerialPort? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var readThread: ReadThread? = null
    private var port = "/dev/ttyS0"
    private var baudrate = 15200
    private var isOpen = false
    private var delay = 50

    fun Serialer(port: String, baudrate: Int) {
        this.port = port
        this.baudrate = baudrate
    }

    fun Serialer() {
        this.Serialer(port, baudrate)
    }

    fun Serialer(port: String) {
        this.Serialer(port, baudrate)
    }

    fun open() {
        serialPort = SerialPort(File(port), baudrate, 0)
        outputStream = serialPort?.getOutputStream()
        inputStream = serialPort?.getInputStream()
        readThread = ReadThread()
        readThread?.start()
        isOpen = true
    }

    fun close() {
        readThread?.interrupt()
        serialPort?.close()
        serialPort = null
        isOpen = false
    }

    fun send(outArray: ByteArray) {
        outputStream?.write(outArray)
        outputStream?.flush()
    }

    fun sendHex(hex: String) {
        val outArray = Utils().hexStringToBytes(hex)
        send(outArray)
    }

    fun sendTxt(txt: String) {
        val outArray = txt.toByteArray()
        send(outArray)
    }

    private inner class ReadThread : Thread() {
        override fun run() {
            super.run()
            while (!isInterrupted) {
                if (inputStream == null) return
                val buffer = ByteArray(512)
                val size = inputStream?.read(buffer) ?: 0
                if (size > 0) {
                    Log.d("sscc", "-----------$size")
                    val b = Arrays.copyOfRange(buffer, 0, size)
                    var sta = ""
                    for (i in 0 until size) {
                        sta += b[i].toChar()
                    }
                    Log.d("sscc", sta)
                    val data = ComData(port, buffer, size)
                    onDataReceived(data)
                }
            }
        }
    }

    fun getBaudrate(): Int {
        return baudrate
    }

    fun setBaudrate(baudrate: Int): Boolean {
        if (isOpen) {
            return false
        } else {
            this.baudrate = baudrate
            return true
        }
    }

    fun getPort(): String {
        return port
    }

    fun setPort(port: String): Boolean {
        if (isOpen) {
            return false
        } else {
            this.port = port
            return true
        }
    }

    fun isOpen(): Boolean {
        return isOpen
    }

    fun getdelay(): Int {
        return delay
    }

    fun setdelay(delay: Int) {
        this.delay = delay
    }

    abstract protected fun onDataReceived(data: ComData)
}
