package net.showsan.komassistant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ToggleButton

import org.jetbrains.anko.*

import java.io.IOException
import java.security.InvalidParameterException
import java.util.*

class MainActivity : AppCompatActivity() {
    private val utils = Utils()

    private val ui = MainActivityUI()
    private val com = Com()
    private val dispQueue = DispQueueThread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        dispQueue.start()
    }

    fun initView() {
        ui.setContentView(this)
        ui.bindView(this)
    }

    fun clear() {
        ui.recDataTxt?.text = ""
    }

    fun send(text: String) {
        sendPortData(com, text)
    }

    fun toggleCom(port: String, baudrate: String, toggleButton: ToggleButton?) {
        if (!com.isOpen()) {
            com.setPort(port)
            com.setBaudrate(baudrate.toInt())
            openCom(com)
            toggleButton?.isChecked = true
        } else {
            closeCom(com)
            toggleButton?.isChecked = false
        }
    }

    fun setBaudrate(baudrate: String) {
        com.setBaudrate(baudrate.toInt())
    }

    fun openCom(com: Serialer) {
        try {
            com.open()
            if (com.isOpen())
                toast("Port ${com.getPort()} Opened. Baudrate ${com.getBaudrate()}")
        } catch (e: SecurityException) {
            toast("Open failed. No Permission")
        } catch (e: IOException) {
            toast("Open failed. Unkown Errors")
        } catch (e: InvalidParameterException) {
            toast("Open failed. Invalid Parameters")
        }

    }

    fun sendPortData(com: Serialer, sOut: String) {
        if (com.isOpen()) {
            if (ui.radioButtonTxt!!.isChecked) {
                com.sendTxt(sOut)
            } else if (ui.radioButtonHex!!.isChecked) {
                com.sendHex(sOut)
            }
        }
    }

    fun closeCom(com: Serialer) {
        com.close()
        if (!com.isOpen()) toast("Port ${com.getPort()} Closed")
    }

    fun dispRecData(data: ComData) {
        val sMsg = StringBuilder()
        sMsg.append(data.time);
        sMsg.append("[");
        sMsg.append(data.port);
        sMsg.append("]");
        if (ui.radioButtonTxt!!.isChecked) {
            sMsg.append("[Txt] ")
            sMsg.append(utils.bytesToString(data.data))
        } else if (ui.radioButtonHex!!.isChecked) {
            sMsg.append("[Hex] ")
            sMsg.append(utils.bytesToHexString(data.data))
        }
        sMsg.append("\r\n")
        ui.recDataTxt?.append(sMsg)
    }

    private inner class Com : Serialer() {
        override fun onDataReceived(data: ComData) {
            dispQueue.AddQueue(data)
        }
    }

    private inner class DispQueueThread : Thread() {
        private val queueList = LinkedList<ComData>()
        override fun run() {
            super.run()
            while (!isInterrupted) {
                val data = queueList.poll()
                while (data != null) {
                    runOnUiThread { dispRecData(data) }
                    Thread.sleep(100)
                    break
                }
            }
        }

        @Synchronized
        fun AddQueue(data: ComData) {
            queueList.add(data)
        }
    }
}
