package ooo.emessi.messenger.managers

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.model_converter.AttachmentConverter
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.MessageMediaConverter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.delay.DelayInformationManager
import org.jivesoftware.smackx.reference.element.ReferenceElement
import org.koin.core.KoinComponent
import org.koin.core.get

class MessageReciever(val message: Message): KoinComponent{

    private val messageRepo: MessageRepo = get()

    fun recieve() = CoroutineScope(Dispatchers.IO).launch{

        var timeStamp = System.currentTimeMillis()
        if (DelayInformationManager.isDelayedStanza(message)) {
            val delayInfo = DelayInformationManager.getDelayInformation(message)
            timeStamp = delayInfo.stamp.time
        }
        val from = getFrom()
        val bzMessage = BZMessage(
            message.from.asEntityBareJidIfPossible().toString(),
            from,
            message.to.asEntityBareJidIfPossible().toString(),
            message.body ?: "",
            timeStamp = timeStamp,
            id = message.stanzaId
        )
        if (message.hasExtension("bzm:media:1")) getExtension(bzMessage)
        if (ReferenceElement.containsReferences(message)) addReferenceToMessage(bzMessage)
        saveRecievedMessage(bzMessage)
        Log.i(this@MessageReciever.javaClass.simpleName, "message sent from " + bzMessage.from + " body " + bzMessage.body)
    }

    private fun getFrom():String {
        return when (message.type){
            Message.Type.headline,
            Message.Type.error,
            Message.Type.normal,
            Message.Type.chat -> message.from.toEntityBareJid().toString()
            Message.Type.groupchat -> message.from.resourceOrEmpty.toString()

        }
    }

    private suspend fun getExtension(bzMessage: BZMessage){
        val payload = mutableListOf<String>()
        val mmc = MessageMediaConverter(message)
        val attachments = mmc.retrievFilesFromMessage(bzMessage.id)
        bzMessage.payload = AttachmentConverter().toJson(attachments)
        val attachmentManager = AttachmentManager()
//        attachments.forEach { attachmentManager.saveAttachment(it.toBZAttachment())}

        //TODO DownloadManager
        val payloadType = BZMessage.calcPayloadType(attachments.first())
//        if (payloadType == BZMessage.PayloadType.IMAGE) attachments.forEach {
//            payload.add(S3Api.downloadFile(it.url!!)!!)
//        }
//        bzMessage.payload = payload
        bzMessage.payloadType = payloadType
        FileDownloadManager().downloadAttachments(attachments) {saveMessage(bzMessage, it)}
    }

    private fun saveMessage(
        message: BZMessage,
        list: List< ABZAttachment>
    ) = CoroutineScope(Dispatchers.IO).launch {
        message.payload = AttachmentConverter().toJson(list)
        saveRecievedMessage(message)
    }

    private fun addReferenceToMessage(bzMessage: BZMessage) {
        val referenceElements = ReferenceElement.getReferencesFromStanza(message).first()
        val refMessage = messageRepo.getMessageById(referenceElements.anchor)
        refMessage?.let {
            bzMessage.isReplyed = true
            bzMessage.messageReplyedId = refMessage.id
        }
    }

    private suspend fun saveRecievedMessage(message: BZMessage) {
        ChatManager(message.chatJid).saveMessage(message)
        messageRepo.addMessage(message)
    }
}