package net.showsan.komassistant

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zoron on 17-11-25.
 */
class ComData(val port: String, buffer: ByteArray, size: Int) {
    val data = Arrays.copyOfRange(buffer, 0, size)
    //val data = ByteArray(size)

    val dateFormat = SimpleDateFormat("hh:mm:ss")
    val time = dateFormat.format(java.util.Date())

    /*init {
        for (i in 0 until size) {
            data[i] = buffer[i]
        }
    }*/
}