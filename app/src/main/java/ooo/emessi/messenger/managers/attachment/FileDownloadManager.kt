package ooo.emessi.messenger.managers.attachment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.minio.S3Api

class FileDownloadManager {

    fun downloadAttachments(
        list: List<AttachmentDto>,
        downloadListener: (list: List<AttachmentDto>) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
//        val attachmentManager = AttachmentManager()
        list.forEach {
            val path = S3Api.downloadFile(it.url!!)
            it.attachmentPath = path
//            attachmentManager.saveAttachment(it.toBZAttachment())
        }
        downloadListener.invoke(list)
    }

}