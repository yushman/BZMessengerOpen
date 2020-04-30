package ooo.emessi.messenger.managers.chat

import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPApi
import ooo.emessi.messenger.xmpp.chat.MultyXChat
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*

class ChatFactory(private val xmppApi: XMPPApi): KoinComponent{
    val chatRepo = get<ChatRepo>()

    fun getChatFromContact(contact: ContactDto): ChatDto {
        var chat = chatRepo.getChatById(contact.contactJid)
        if (chat == null) chat = createSingleChat(contact)
        return chat
    }

    fun createSingleChat(contact: ContactDto) : ChatDto{
        val chat = ChatDto(contact.contactJid, false, contact.name)
        chatRepo.addChat(chat)
        xmppApi.saveRosterEntry(chat)
        return chat
    }

    fun createMucChat(name: String, occupants: List<ContactDto>): ChatDto{
        val mucLightService = "@" + xmppApi.getMucLightManager().localServices.first().toString()
        val jid = UUID.randomUUID().toString() + mucLightService
        val chat = ChatDto(jid, true, name)
        val xChat = xmppApi.getXChat(chat) as MultyXChat
        val occupantsJid = occupants.map { it.contactJid.toEntityBareJid() }
        xmppApi.createMucChat(xChat, name, occupantsJid)
        xmppApi.saveRosterEntry(chat)
        chatRepo.addChat(chat)
        return chat
    }
}