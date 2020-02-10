package ooo.emessi.messenger.managers

import android.util.Log
import kotlinx.coroutines.*
import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.minio.S3Api
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smackx.httpfileupload.element.Slot
import java.io.File

class FileUploadManager (val attachmentManager: AttachmentManager) {
    val slots = mutableMapOf<ABZAttachment, Slot>()
    val successResults = mutableMapOf<ABZAttachment, Boolean>()
    val fumr = XMPPConnectionApi.getFileUpload()

//    fun requestSlots(paths: List<String>): MutableMap<File, Slot> {
//
//        paths.forEach {
//            val file = File(it)
//            try {
//                slots[file] = (fumr.requestSlot(file.name, file.length()))
//            } catch (e: Exception){
//                e.printStackTrace()
//            }
//        }
//        return slots
//    }

    suspend fun requestSlot(attachment: ABZAttachment): String {
        var url = ""
        try {
            val file = File(attachment.attachmentPath!!)
            val slot = fumr.requestSlot(file.name, file.length())
            url = slot.getUrl.toString()
            slots[attachment] = slot
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.i(this.javaClass.simpleName, url)
        return url
    }

    suspend fun uploadFiles() = CoroutineScope(Dispatchers.IO).async{
        try {
            if (slots.isNotEmpty()) slots.forEach {
                val successResult = S3Api.uploadFile(File(it.key.attachmentPath!!), it.value.putUrl)
                if (successResult) attachmentManager.saveAttachment(it.key.toBZAttachment())
                successResults[it.key] = successResult
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        slots.clear()
        return@async successResults
    }.await()
}