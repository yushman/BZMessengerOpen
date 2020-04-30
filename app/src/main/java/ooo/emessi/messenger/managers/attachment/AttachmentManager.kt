package ooo.emessi.messenger.managers.attachment

import ooo.emessi.messenger.data.model.dto_model.AttachmentDtoMaker
import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.data.model.wrapped_model.AttachmentMessage
import ooo.emessi.messenger.xmpp.XMPPApi
import org.koin.core.KoinComponent
import org.koin.core.inject


class AttachmentManager : KoinComponent {
    private val xmppApi by inject<XMPPApi>()
    private val attachmentDtoMaker by inject<AttachmentDtoMaker>()

    fun createAttachments(docPaths: List<String>): List<AttachmentDto> {
        val attachments = mutableListOf<AttachmentDto>()
        docPaths.forEach { attachments.add(attachmentDtoMaker.makeAttachment(it)) }
        return attachments
    }

    fun uploadAttachments(
        attachments: List<AttachmentMessage>,
        uploadListener: (uploadResult: Pair<AttachmentMessage, Boolean>) -> Unit
    ) {
        val fileUpload = FileUploadManager()

        attachments.forEach {
            var isUploadSucceed = false
            try {
                val slot = xmppApi.requestSlot(it.attachment)
                it.attachment.url = slot.getUrl.toString()
                isUploadSucceed = fileUpload.upload(slot, it.attachment)
            } catch (e:Exception){
                e.printStackTrace()
            }
            uploadListener.invoke(it to isUploadSucceed)
        }
    }

    fun downloadAttachments(attachments: List<AttachmentDto>) {

    }
}