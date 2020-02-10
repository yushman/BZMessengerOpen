package ooo.emessi.messenger.utils.helpers

import android.util.Log
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object LogHelper {
    fun appendLog(text: String){
        val logFile = File(File(StorageHelper.getDownloadDirPath()),"log.txt")
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) { // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        try { //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    fun logMessage(tag: String, message: BZMessage): String {
        val text = """Message from: ${message.from}
            to: ${message.to}
            body: ${message.body}
            correctedBody: ${message.messageCorrectedBody}
            refferenceId: ${message.messageReplyedId}
            payloadSize: ${message.payload}
            payloadType: ${message.payloadType.name}
            sended: ${message.isSended}
            delivered: ${message.isDelivered}
        """.trimIndent()
        Log.i(tag, text)
        return text
    }
}