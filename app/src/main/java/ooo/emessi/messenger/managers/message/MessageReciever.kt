package ooo.emessi.messenger.managers.message

import android.util.Log
import ooo.emessi.messenger.data.model.dto_model.MessageDtoMaker
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.message_correct.element.MessageCorrectExtension
import org.koin.core.KoinComponent
import org.koin.core.get

class MessageReciever(val message: Message, val myJid: String): KoinComponent{

    private val messageRepo: MessageRepo = get()
    private val chatRepo: ChatRepo = get()

    suspend fun recieve() {
        if (message.hasExtension(MessageCorrectExtension.NAMESPACE)) {
            getCorrectedExtension()
            return
        }
        val messageDto = MessageDtoMaker(myJid).makeDto(message)
//        if (message.body.isNullOrEmpty()) return
//
//        if (messageRepo.getMessageById(message.stanzaId) != null) return
//
//        var timeStamp = System.currentTimeMillis()
//        var fromChat = message.from.asEntityBareJidIfPossible().toString()
//        if (fromChat == Settings.myAcc.userJid) fromChat =
//            message.to.asEntityBareJidIfPossible().toString()
//        val from = getFrom(message)
//        if (DelayInformationManager.isDelayedStanza(message)) {
//            Log.i(this.javaClass.simpleName, "delayed")
//            val delayInfo = DelayInformationManager.getDelayInformation(message)
//            timeStamp = delayInfo.stamp.time
////            from = delayInfo.from
//        }
//        val isIncoming = from != Settings.myAcc.userJid
//        val bzMessage = MessageDto(
//            fromChat,
//            from,
//            message.to.asEntityBareJidIfPossible().toString(),
//            message.body ?: "",
//            timeStamp = timeStamp,
//            id = message.stanzaId,
//            isIncoming = isIncoming
//        )
//        if (message.hasExtension("bzm:media:1")) getMediaExtension(bzMessage)
//        if (ReferenceElement.containsReferences(message)) addReferenceToMessage(bzMessage)

        saveRecievedMessage(messageDto)
        Log.i(this@MessageReciever.javaClass.simpleName, "message sent from " + messageDto.from + " body " + messageDto.body)
    }

//    private fun getForwardedMessage() {
//        val extension = message.getExtension(Forwarded.NAMESPACE) as Forwarded
//        val bzMessage = extension.forwardedStanza
//    }
//
//    private fun getFrom(_message: Message):String {
//        return when (_message.type){
//            Message.Type.headline,
//            Message.Type.error,
//            Message.Type.normal,
//            Message.Type.chat -> message.from.toEntityBareJid().toString()
//            Message.Type.groupchat -> message.from.resourceOrEmpty.toString()
//        }
//    }

    private suspend fun getCorrectedExtension(){
        val extension = message.getExtension(MessageCorrectExtension.NAMESPACE) as MessageCorrectExtension
        var bzMessage = messageRepo.getMessageById(extension.idInitialMessage)
        if (bzMessage != null) {
            bzMessage = bzMessage.copy(isCorrected = true, messageCorrectedBody = message.body)
            saveRecievedMessage(bzMessage)
        }
    }

//    private suspend fun getMediaExtension(messageDto: MessageDto) {
//        Timber.i("getting image from message ${messageDto.id}")
//        val payload = mutableListOf<String>()
//        val mmc =
//            MediaExtensionConverter(message)
//        val attachments = mmc.retrievFilesFromMessage(messageDto.id)
//        messageDto.payload = AttachmentToJSONConverter()
//            .toJson(attachments)
//        val attachmentManager =
//            AttachmentManager()
////        attachments.forEach { attachmentManager.saveAttachment(it.toBZAttachment())}
//
//        //TODO DownloadManager
//        val payloadType = MessageDto.calcPayloadType(attachments.first())
////        if (payloadType == BZMessage.PayloadType.IMAGE) attachments.forEach {
////            payload.add(S3Api.downloadFile(it.url!!)!!)
////        }
////        bzMessage.payload = payload
//        messageDto.payloadType = payloadType
//        FileDownloadManager()
//            .downloadAttachments(attachments) { saveMessage(messageDto, it) }
//    }

//    private fun saveMessage(
//        messageDto: MessageDto,
//        list: List<AttachmentDto>
//    ) = CoroutineScope(Dispatchers.IO).launch {
//        messageDto.payload = AttachmentToJSONConverter()
//            .toJson(list)
//        saveRecievedMessage(messageDto)
//    }

//    private fun addReferenceToMessage(messageDto: MessageDto) {
//        val referenceElements = ReferenceElement.getReferencesFromStanza(message).first()
//        val refMessage = messageRepo.getMessageById(referenceElements.anchor)
//        refMessage?.let {
//            messageDto.isReplyed = true
//            messageDto.messageReplyedId = refMessage.id
//        }
//    }

    private suspend fun saveRecievedMessage(messageDto: MessageDto) {
        messageRepo.addMessage(messageDto)
        val chat = chatRepo.getChatById(messageDto.chatJid) ?: ChatDto(messageDto.chatJid)
        chat.unreadMessages++
        chatRepo.addChat(chat)
    }
}