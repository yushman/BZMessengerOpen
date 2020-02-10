package ooo.emessi.messenger.managers

import android.webkit.DownloadListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.minio.S3Api

class FileDownloadManager (){

    fun downloadAttachments(list: List<ABZAttachment>, downloadListener:(list: List<ABZAttachment>) -> Unit ) = CoroutineScope(Dispatchers.IO).launch {
//        val attachmentManager = AttachmentManager()
        list.forEach {
            val path = S3Api.downloadFile(it.url!!)
            it.attachmentPath = path
//            attachmentManager.saveAttachment(it.toBZAttachment())
        }
        downloadListener.invoke(list)
    }

}