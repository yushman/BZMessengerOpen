package ooo.emessi.messenger.managers

import android.util.Log
import android.util.Log.d
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.model.wrapped_model.MessageItem
import ooo.emessi.messenger.data.model_converter.AttachmentConverter
import ooo.emessi.messenger.data.repo.AttachmentRepo
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.settings.Settings
import ooo.emessi.messenger.utils.helpers.LogHelper
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smackx.muclight.MultiUserChatLight
import org.koin.core.KoinComponent
import org.koin.core.inject

class MessagesManager(val chatId: String) : KoinComponent {

    private val TAG = this.javaClass.simpleName

    private val messageRepo: MessageRepo  by inject()
    private val chatRepo: ChatRepo  by inject()
    private val contactRepo: ContactRepo  by inject()
    private val attachmentRepo: AttachmentRepo  by inject()

    val messages: LiveData<List<BZMessage>> = messageRepo.loadMessagesByChatId(chatId)
    val messageSended: MutableLiveData<Boolean> = MutableLiveData()

    var correctedMessage: BZMessage? = null
    var replyedMessage: BZMessage? = null

    suspend fun createMessageItem(list: List<BZMessage>?): List<MessageItem> {
        val messageItemList = mutableListOf<MessageItem>()
        if (list.isNullOrEmpty()) return messageItemList
        list.forEach { bzMessage ->
            val contact = contactRepo.getContactById(bzMessage.from) ?: BZContact(bzMessage.from)
            val payload = AttachmentConverter().fromJson(bzMessage)
//            val list = attachmentRepo.getAttachments(bzMessage.id)
//            if (list.isNotEmpty()) payload = list.map { it.toTypedAttachment(bzMessage.payloadType) }
            val replyedMessage  = if (bzMessage.isReplyed)
                messageRepo.getMessageById(bzMessage.messageReplyedId!!)
            else null

            messageItemList.add(MessageItem(bzMessage,contact, payload, replyedMessage))
        }
        return messageItemList
    }

    fun receiveDeliveryReceipt(receiptId: String) = CoroutineScope(Dispatchers.IO).launch{
        val message = messageRepo.getMessageById(receiptId)
        d(TAG, receiptId)
        message?.let {
            it.isDelivered = true
            saveRecievedMessage(it)
        }
    }

    fun messageSended(packet: Stanza) = CoroutineScope(Dispatchers.IO).launch {
        val message = messageRepo.getMessageById(packet.stanzaId)
        message?.let{
            it.isSended = true
            messageRepo.updateMessage(it)
        }
    }

    fun saveMessage(message: BZMessage) = CoroutineScope(Dispatchers.IO).launch {
        messageRepo.addMessage(message)
    }

    fun saveRecievedMessage(message: BZMessage)= CoroutineScope(Dispatchers.IO).launch {
        var chat = chatRepo.getChatById(message.chatJid)
        if (chat != null) chat.lastMessage = message
        else chat = createChat(message)
        chat.unreadMessages++
        chatRepo.addChat(chat)
        messageRepo.addMessage(message)
    }

    fun createChat(message: BZMessage): BZChat {
        val jid = message.chatJid
        val isMulti = message.from.contains("@muclight")
        val name = XMPPConnectionApi.getRoster().getEntry(jid.toEntityBareJid()).name
        return BZChat(jid, name, isMulti = isMulti)
    }

//    fun getContact(from: String): BZContact? = contactRepo.getContactById(from)
//
//    fun getLastChatMessage(chatId: String): BZMessage? = messageRepo.getMessageLastInChat(chatId)

    fun deleteMessage(it: BZMessage) {
        messageRepo.delete(it)
    }

//    private suspend fun getExtension(message: Message, bzMessage: BZMessage): BZMessage {
//        val payload = mutableListOf<String>()
//        var payloadType = BZMessage.PayloadType.NONE
//        val mmc = MessageMediaConverter(message)
//        val attachment = mmc.retrievFilesFromMessage()
//        val attachmentManager = AttachmentManager()
////        attachment.forEach { attachmentManager.saveAttachment(it.toBZAttachment().copy(messageId = message.stanzaId, attachmentPath = S3Api.downloadFile(it.url!!))) }
//        payloadType = BZMessage.calcPayloadType(attachment.first())
//        if (payloadType == BZMessage.PayloadType.IMAGE) attachment.forEach {
//            payload.add(S3Api.downloadFile(it.url!!)!!)
//        }
//        return bzMessage.copy(payload = payload,
//            payloadType = payloadType)
//    }

    suspend fun sendMessage(message: BZMessage, chat: Chat){
        val messageSender = MessageSender(message) {updateMessageSendedResult(it)}
        messageSender.sendBy(chat)
    }

    suspend fun sendMessage(message: BZMessage, chat: MultiUserChatLight){
        val messageSender = MessageSender(message) {updateMessageSendedResult(it)}
        messageSender.sendBy(chat)
    }

    suspend fun sendAttachments(attachments: List<String>, chat: Chat) {
        val attachmentManager = AttachmentManager()
        val hm = prepareMessage(attachments, attachmentManager)
        val uploadResult = attachmentManager.tryUpload()
        hm.forEach {
            if (uploadResult[it.value]!!) sendMessage(it.key, chat)
        }
    }

    suspend fun sendAttachments(attachments: List<String>, chat: MultiUserChatLight) {
        val attachmentManager = AttachmentManager()
        val hm = prepareMessage(attachments, attachmentManager)
        val uploadResult = attachmentManager.tryUpload()
        hm.forEach {
            if (uploadResult[it.value]!!) sendMessage(it.key, chat)
        }
//        if (attachments.isNotEmpty()) {
//
//            val attachmentManager = AttachmentManager()
//            attachmentManager.createAttachments(attachments)
//            attachmentManager.attachments.forEach {
//                it.url = attachmentManager.requestSlot(it)
//                val message = createAttachmentMessage(it)
//                hm[message] = it
////                it.messageId = message.id
////                attachmentManager.saveAttachment(it.toBZAttachment())
//            }
//            val uploadResult = attachmentManager.tryUpload()
//
//
//        }
    }

    private suspend fun prepareMessage(
        attachments: List<String>,
        attachmentManager: AttachmentManager
    ): MutableMap<BZMessage, ABZAttachment> {
        val hm = mutableMapOf<BZMessage, ABZAttachment>()
        if (attachments.isNotEmpty()) {
            attachmentManager.createAttachments(attachments)
            attachmentManager.attachments.forEach {
                it.url = attachmentManager.requestSlot(it)
                Log.i(TAG, it.url)
                val message = createAttachmentMessage(it)
                hm[message] = it
//                it.messageId = message.id
//                attachmentManager.saveAttachment(it.toBZAttachment())
            }
        }
        return hm
    }

    fun createMessage(messageBody: String, attachments: List<String> = listOf()): BZMessage {
        var message: BZMessage? = null
        correctedMessage?.let {
            message = createCorrectedMessage(messageBody, it)
        }
        replyedMessage?.let {
            message = createReplyMessage(messageBody, it)
        }
        if (message == null) message = createSimpleMessage(messageBody)
        return message!!
    }

    private fun createCorrectedMessage(messageBody: String, correctedMessage: BZMessage): BZMessage {
        val message = correctedMessage.copy(isCorrected = true, messageCorrectedBody = messageBody)
        saveMessage(message)
        return message
    }

    private fun createReplyMessage(messageBody: String, it: BZMessage): BZMessage {
        val message = BZMessage(
            chatId,
            Settings.myAcc!!.userJid,
            chatId,
            messageBody,
            isIncoming = false,
            isReplyed = true,
            messageReplyedId = it.id
        )
        saveMessage(message)
        return message
    }

    private fun createSimpleMessage(messageBody: String): BZMessage {
        val message = BZMessage(
            chatId,
            Settings.myAcc!!.userJid,
            chatId,
            messageBody,
            isIncoming = false
        )
        saveMessage(message)
        return message
    }

    fun createAttachmentMessage(attachment: ABZAttachment): BZMessage {
        val message = BZMessage(
            chatId,
            Settings.myAcc!!.userJid,
            chatId,
            attachment.attachmentName,
            isIncoming = false,
            payloadType = BZMessage.calcPayloadType(attachment),
            payload = AttachmentConverter().toJson(listOf(attachment))
        )
        Log.i(TAG, message.payload)
//        attachment.messageId = message.id
        saveMessage(message)
        return message
    }

    private fun updateMessageSendedResult(bzMessage: BZMessage) {
        updateMessage(bzMessage)
        if (!bzMessage.isSended) scheduleSentMessage(bzMessage)
        else messageSended.postValue(bzMessage.isSended)
    }

    private fun updateMessage(bzMessage: BZMessage) = CoroutineScope(Dispatchers.IO).launch {
        // Delete after implementing chatItem
        ChatManager(bzMessage.chatJid).saveMessage(bzMessage)
        messageRepo.updateMessage(bzMessage)

    }

    private fun scheduleSentMessage(bzMessage: BZMessage) {
        LogHelper.logMessage(this.javaClass.simpleName, bzMessage)
    }

    fun clearMessageHistory() {
        messageRepo.deleteMessagesByChatId(chatId)
    }


//    fun loadMessagesFromMam(chatId: String) = CoroutineScope(Dispatchers.IO).launch {
//        val mamManager = XMPPConnectionApi.getMamManager()
//        val lastMessage = messageRepo.getMessageLast()
//        var lastMessageDate = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 10L
//        if (lastMessage != null) {
//            lastMessageDate = lastMessage.timeStamp
//        }
//        val myJid = JidCreate.entityBareFrom(mamManager.archiveAddress)
//        val mamQueryArgs = MamManager.MamQueryArgs.builder()
//            .limitResultsSince(lastMessageDate.toDate())
//            .build()
//        val mamQuery = mamManager.queryArchive(mamQueryArgs)
//        val loadedMessages = mutableListOf<Message>()
//        while (!mamQuery.isComplete) {
//            loadedMessages.addAll(mamQuery.messages)
//            mamQuery.pageNext(50)
//        }
//        Log.d("Service", "MAM size" + loadedMessages.size.toString())
////        val newMessages = mutableListOf<BZMessage>()
//        loadedMessages.forEach {
//            if (it.body == null) return@forEach
//            if (it.type == Message.Type.chat || it.type == Message.Type.groupchat) {
//                Log.d("Service", "MAM " + it.stanzaId)
//                Log.d("Service", "MAM " + it.from)
//                Log.d("Service", "MAM " + it.body)
//                val chatId = if (it.from.asEntityBareJidIfPossible() == myJid) {
//                    JidCreate.entityBareFrom(it.to).toString()
//                } else {
//                    JidCreate.entityBareFrom(it.from).toString()
//                }
//                val isIncoming = JidCreate.entityBareFrom(it.from) != myJid
//
//                val newMessage = BZMessage(
//                    chatId,
//                    JidCreate.entityBareFrom(it.from).toString(),
//                    JidCreate.entityBareFrom(it.to).toString(),
//                    it.body,
//                    isIncoming = isIncoming,
//                    id = it.stanzaId
//                )
//                messageRepo.addMessage(newMessage)
//            }
//
//        }
//
//    }

    companion object{

    }
}