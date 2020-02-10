package ooo.emessi.messenger.controllers

import android.util.Log
import android.util.Log.d
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.model_converter.AttachmentConverter
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.managers.ContactsManager
import ooo.emessi.messenger.managers.RosterManager
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smackx.mam.MamManager
import org.jivesoftware.smackx.search.UserSearchManager
import org.jivesoftware.smackx.xdata.Form
import org.jivesoftware.smackx.xdata.FormField
import org.jivesoftware.smackx.xdata.packet.DataForm
import org.jxmpp.jid.BareJid
import org.jxmpp.jid.impl.JidCreate
import org.koin.core.KoinComponent
import org.koin.core.get

class ChatListController : KoinComponent {
    private val chatRepo: ChatRepo = get()
    private val contactRepo: ContactRepo = get()
    private val messageRepo: MessageRepo = get()
    private val mucManager = XMPPConnectionApi.getMucLightManager()
    val chats: LiveData<List<BZChat>> = chatRepo.loadChats()
    val forwardedMessage: MutableLiveData<BZMessage> = MutableLiveData()

    private fun loadLastMessage(chats: List<BZChat>): List<BZChat> {
        val _chats = mutableListOf<BZChat>()
        chats.forEach {
            val message = messageRepo.getMessageLastInChat(it.jid)
            it.lastMessage = message
            if (message != null) it.unreadMessages++
            _chats.add(it)
        }
        return _chats
    }



    fun loadChatsFromRoster() = CoroutineScope(Dispatchers.IO).launch {
//        ContactsManager.clearRoster()
        try {

            val rChats =
                RosterManager.getFullRosterEntries()
            rChats.forEach {
                var chat = chatRepo.getChatById(it.jid.asEntityBareJidIfPossible().toString())
                if (chat == null) chat = chatFromRosterEntry(it)
                val contact = ContactsManager().updateContactFromRoster(it)
                chat.contact = contact
                chatRepo.addChat(chat)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun loadJUS() = CoroutineScope(Dispatchers.IO).launch {
        val us = UserSearchManager(XMPPConnectionApi.getConnection())
        val serv = us.searchServices
        Log.i(this@ChatListController.javaClass.simpleName, serv.toString())
        val formreq = us.getSearchForm(serv.first())
        val form = formreq.createAnswerForm()
        form.setAnswer("email", "i.yushenkov@mossales.ru")

        Log.i(this@ChatListController.javaClass.simpleName, form.toString())
        val res = us.getSearchResults(form, serv.first())
        Log.i(this@ChatListController.javaClass.simpleName, serv.toString())
        Log.i(this@ChatListController.javaClass.simpleName, form.toString())
        Log.i(this@ChatListController.javaClass.simpleName, res.toString())
    }

    fun updateChatsFromRoster() = CoroutineScope(Dispatchers.IO).launch{
        try {
            val rChats =
                RosterManager.getFullRosterEntries()
            rChats.forEach { entry ->
                val jid = entry.jid.asEntityBareJidIfPossible().toString()
                var chat = chatRepo.getChatById(jid)
                if (chat == null) chat = chatFromRosterEntry(entry)
                val contact = contactRepo.getContactById(jid)
                contact?.let {
                    chat.contact = contact
                }
                chatRepo.addChat(chat)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadMessageFromMam(it: RosterEntry) {
        val mamManager = XMPPConnectionApi.getMamManager()
        val myJid = JidCreate.entityBareFrom(mamManager.archiveAddress)
        val mamQueryArgs = MamManager.MamQueryArgs.builder()
            .limitResultsToJid(it.jid.toEntityBareJid())
            .setResultPageSize(1)
            .queryLastPage()
            .build()
        var mamQuery: MamManager.MamQuery? = null
        try {
            mamQuery = mamManager.queryArchive(mamQueryArgs)
        } catch (e: Exception){ e.printStackTrace() }
        if (mamQuery == null) return
        if (mamQuery.isComplete && mamQuery.messageCount != 0) {
            val message = mamQuery.messages.last()
            if (message.body == null) return
            if (message.type == Message.Type.chat || message.type == Message.Type.groupchat) {
                Log.d("Service", "MAM " + message.stanzaId)
                Log.d("Service", "MAM " + message.from)
                Log.d("Service", "MAM " + message.body)
            }
        }

    }

    private fun chatFromRosterEntry(it: RosterEntry): BZChat {
        val isMulti = RosterManager.isMulti(it)
        var name = ""
        if (isMulti) name = getNameFromMUCConfig(it.jid)
        else name = it.name
        return BZChat(it.jid.toString(), name, null, isMulti)
    }

    private fun getNameFromMUCConfig(jid: BareJid): String {
        val chat = mucManager.getMultiUserChatLight(jid.asEntityBareJidIfPossible())
        val config = chat.configuration
        return config.roomName
    }

    private fun updateChatLastMessage(
        bzChat: BZChat,
        message: BZMessage?
    ) = CoroutineScope(Dispatchers.IO).launch {
        d("UNREAD", bzChat.name + bzChat.unreadMessages)
        bzChat.lastMessage = message
        if (message != null && message.isIncoming) bzChat.unreadMessages++
        chatRepo.updateChat(bzChat)
    }

    fun loadForwardedMessage(forwardedMessageId: String) = CoroutineScope(Dispatchers.IO).launch {
        forwardedMessage.postValue(messageRepo.getMessageById(forwardedMessageId))
    }

    fun forwardMessage(it: BZChat) {
        if (it.isMulti) forwardMulti(it)
        else forwardSingle(it)
    }

    private fun forwardSingle(it: BZChat) {
        val chat = SingleChatsController(it.jid)
        val message = forwardedMessage.value
        message?.let {
            if (it.payloadType != BZMessage.PayloadType.NONE) {
                val payload = AttachmentConverter().fromJson(it)
                chat.sendAttachments(payload.map { abzAttachment -> abzAttachment.attachmentPath!! })
            } else chat.sendMessage(it)
        }
    }

    private fun forwardMulti(it: BZChat) {
        val chat =
            MUCLightChatsController(it.jid)
        val message = forwardedMessage.value
        message?.let {
            if (it.payloadType != BZMessage.PayloadType.NONE) {
                val payload = AttachmentConverter().fromJson(it)
                chat.sendAttachments(payload.map { abzAttachment -> abzAttachment.attachmentPath!! })
            } else chat.sendMessage(it)
        }
    }

}