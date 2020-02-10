package ooo.emessi.messenger.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jxmpp.jid.impl.JidCreate
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

class ChatManager (val jid: String): KoinComponent{

    private val messageRepo: MessageRepo by inject()
    private val chatRepo: ChatRepo by inject()
    private val contactRepo: ContactRepo by inject()

    val chat = XMPPConnectionApi.getChatManager().chatWith(JidCreate.entityBareFrom(jid))

    val chatLD = chatRepo.loadChatById(jid)

    suspend fun saveMessage(message: BZMessage) {
        val chat = getChat()
        chat.lastMessage = message
        if (message.isIncoming) chat.unreadMessages++
        chatRepo.addChat(chat)
    }

    suspend fun createChat(){
        val name = RosterManager.getRoster().getEntry(jid.toEntityBareJid()).name
        val chat = BZChat(jid, name, contact = contactRepo.getContactById(jid))
        chatRepo.addChat(chat)
    }

    suspend fun deleteChat(){
        RosterManager.removeEntry(jid)
        chatRepo.deleteChatById(jid)
    }

    suspend fun flushChatUnread(){
        val chat = getChat()
        chat.unreadMessages = 0
        chatRepo.updateChat(chat)
    }

    suspend fun updateChatLastActivity(){
        val entry = RosterManager.getRoster().getEntry(jid.toEntityBareJid())
        val contact = ContactsManager().updateContactFromRoster(entry)
        val chat = getChat()
        chatRepo.updateChat(chat.copy(contact = contact))
    }

    suspend fun updateChatLastMessage() {
        val chat = getChat()
        chat.lastMessage = messageRepo.getMessageLastInChat(jid)
        chatRepo.updateChat(chat)
    }

    private suspend fun getChat() = chatRepo.getChatById(jid) ?: BZChat(jid)


}