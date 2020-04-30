package ooo.emessi.messenger.xmpp

import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.chat.AbstractXChat
import ooo.emessi.messenger.xmpp.chat.MultyXChat
import ooo.emessi.messenger.xmpp.chat.SingleXChat
import ooo.emessi.messenger.xmpp.connection.ConnectionState
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smackx.httpfileupload.element.Slot
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.jxmpp.jid.Jid
import org.koin.core.KoinComponent
import org.koin.core.inject

class XMPPApi(
    private val connection: XMPPTCPConnection
) : IXMPPApi, KoinComponent {

    private val managersFactory by inject<XMPPManagersFactory>()

    override fun getMucLightManager() = managersFactory.getMucLightManager()

    override fun getConnection() = connection

    override fun getMyJid() = connection.user

    override fun connect(listener: (state: ConnectionState) -> Unit) {
        managersFactory.getConnectionManager().makeConnection(listener)
    }

    override fun reconnect() {
        managersFactory.getConnectionManager().doLogout()
    }

    override fun login(user: String, password: String) {
        managersFactory.getConnectionManager().doLogin(user, password)
    }

    override fun send(message: Message, XChat: AbstractXChat) = XChat.send(message)

    override fun setupStanzaListener(listener: (stanza: Stanza) -> Unit) {
        managersFactory.getStanzaManager().setupStanzaListener(listener)
    }

    override fun setupDeliveryListener(listener: (fromJid: Jid, toJid: Jid, receiptId: String, receipt: Stanza) -> Unit) {
        managersFactory.getStanzaManager().setupDeliveryListener(listener)
    }

    override fun registerFBToken(token: String) {
        managersFactory.getPushManager().sendPushDiscoverIq(token)
    }

    override fun getRosterEntries(): MutableSet<RosterEntry>? {
        return managersFactory.getRosterManager().getFullRosterEntries()
    }

    override fun saveRosterEntry(chatDto: ChatDto) {
        managersFactory.getRosterManager().createEntry(chatDto)
    }

    override fun removeRosterEntry(chatDto: ChatDto) =
        managersFactory.getRosterManager().removeEntry(chatDto)

    fun getAvatar(vCard: VCard?) = managersFactory.getRosterManager().getAvatar(vCard)

    fun getAvatarHash(vCard: VCard?) = managersFactory.getRosterManager().getAvatarHash(vCard)

    override fun loadFromMam(lastMessageDto: MessageDto?): List<Message> {
        return managersFactory.getMessageArchiveManager().loadMessageArchive(lastMessageDto)
    }

    override fun getAffiliations(chat: MultyXChat) = chat.getAffiliations()

    override fun addAffiliations(contactDtos: List<ContactDto>, chat: MultyXChat) {
        chat.addAffiliations(contactDtos)
    }

    override fun removeAffiliations(mucAffiliationDto: MucAffiliationDto, chat: MultyXChat) {
        chat.removeAffiliation(mucAffiliationDto)
    }

    override fun leaveChat(XChat: AbstractXChat) {
        XChat.leave()
    }

    override fun getXChat(chatDto: ChatDto): AbstractXChat {
        return if (chatDto.isMulti) MultyXChat(chatDto.jid.toEntityBareJid())
        else SingleXChat(chatDto.jid.toEntityBareJid())
    }

    override fun createMucChat(xChat: MultyXChat, roomName: String, occupants: List<Jid>) {
        xChat.create(roomName, occupants)
    }

    fun getMucChatInfo(entry: RosterEntry) = managersFactory.getRosterManager().getContactName(entry)

    override fun requestSlot(attachment: AttachmentDto): Slot {
        return managersFactory.getFileUpload()
            .requestSlot(attachment.attachmentPath, attachment.size.toLong())
    }

    override fun disconnect() {
        managersFactory.getConnection().disconnect()
    }

    override fun isMeXChatOwner(xChat: MultyXChat) = xChat.isMeOwner()

    fun getVcard(jid: Jid) = managersFactory.getRosterManager().getVCard(jid)

}