package net.showsan.komassistant

/**
 * Created by zoron on 17-11-21.
 */
class Utils {
    fun bytesToString(src: ByteArray): String {
        val strBuilder = StringBuilder()
        for (i in 0 until src.size) {
            strBuilder.append(src[i].toChar())
        }
        return strBuilder.toString()
    }

    fun hexStringToBytes(src: String): ByteArray {
        val len = src.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(src[i], 16) shl 4) + Character.digit(src[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun bytesToHexString(src: ByteArray): String {
        val strBuilder = StringBuilder()
        for (i in 0 until src.size) {
            strBuilder.append(byteToHex(src[i]))
            strBuilder.append(" ")
        }
        return strBuilder.toString()
    }

    fun byteToHex(src: Byte): String {
        return String.format("%02x", src).toUpperCase()
    }

    fun hexToAscii(value: String): String {
        val output = StringBuilder()
        var i = 0
        while (i < value.length) {
            val str = value.substring(i, i + 2)
            output.append(Integer.parseInt(str, 16).toChar())
            i += 2
        }
        return output.toString()
    }

    fun asciiToString(value: String): String {
        val sbu = StringBuilder()
        val chars = splitStringEvery(value, 2)
        for (aChar in chars) {
            sbu.append(Integer.parseInt(aChar).toChar())
        }
        return sbu.toString()
    }

    fun asciiToString(value: ByteArray): String {
        return asciiToString(value.toString())
    }

    fun splitStringEvery(s: String, interval: Int): Array<String?> {
        val arrayLength = Math.ceil(s.length / interval.toDouble()).toInt()
        val result = arrayOfNulls<String>(arrayLength)

        var j = 0
        val lastIndex = result.size - 1
        for (i in 0 until lastIndex) {
            result[i] = s.substring(j, j + interval)
            j += interval
        }
        result[lastIndex] = s.substring(j)

        return result
    }
}