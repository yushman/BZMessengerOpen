package ooo.emessi.messenger.managers.attachment

import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.minio.S3Api
import org.jivesoftware.smackx.httpfileupload.element.Slot
import timber.log.Timber
import java.io.File

class FileUploadManager {
    fun upload(slot: Slot, attachment: AttachmentDto): Boolean {
        val isUploadSucceed = S3Api.uploadFile(File(attachment.attachmentPath!!), slot.putUrl)
        if (!isUploadSucceed) addToQueue(slot, attachment)
        return isUploadSucceed
    }

    private fun addToQueue(slot: Slot, attachment: AttachmentDto) {
        Timber.i("Added to queue $slot, ${attachment}")
    }
}