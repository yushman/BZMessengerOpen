package ooo.emessi.messenger.xmpp

import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto
import ooo.emessi.messenger.xmpp.chat.AbstractXChat
import ooo.emessi.messenger.xmpp.chat.MultyXChat
import ooo.emessi.messenger.xmpp.connection.ConnectionState
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smackx.httpfileupload.element.Slot
import org.jivesoftware.smackx.muclight.MUCLightAffiliation
import org.jivesoftware.smackx.muclight.MultiUserChatLightManager
import org.jxmpp.jid.Jid
import java.util.*

interface IXMPPApi {

    //connection
    fun getConnection(): XMPPTCPConnection
    fun getMyJid(): Jid

    //Auth
    fun connect(listener: (state: ConnectionState) -> Unit)
    fun reconnect()
    fun login(user: String, password: String)

    //Messaging
    fun send(message: Message, XChat: AbstractXChat): Boolean
    fun setupStanzaListener(listener: (stanza: Stanza) -> Unit)
    fun setupDeliveryListener(listener: (fromJid: Jid, toJid: Jid, receiptId: String, receipt: Stanza) -> Unit)

    //PubSub
    fun registerFBToken(token: String)

    //Roster
    fun getRosterEntries(): MutableSet<RosterEntry>?
    fun saveRosterEntry(chatDto: ChatDto)
    fun removeRosterEntry(chatDto: ChatDto): Boolean

    //Chat
    fun getMucLightManager(): MultiUserChatLightManager
    fun loadFromMam(lastMessageDto: MessageDto?): List<Message>
    fun getAffiliations(chat: MultyXChat): HashMap<Jid, MUCLightAffiliation>?
    fun addAffiliations(contactDtos: List<ContactDto>, chat: MultyXChat)
    fun removeAffiliations(mucAffiliationDto: MucAffiliationDto, chat: MultyXChat)
    fun leaveChat(XChat: AbstractXChat)
    fun getXChat(chatDto: ChatDto): AbstractXChat
    fun createMucChat(xChat: MultyXChat, roomName: String, occupants: List<Jid>)
    fun isMeXChatOwner(xChat: MultyXChat): Boolean

    //HttpUpload
    fun requestSlot(attachment: AttachmentDto): Slot
    fun disconnect()

}