package ooo.emessi.messenger.managers.roster

import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.managers.AbstractManager
import ooo.emessi.messenger.utils.isMultiChat
import ooo.emessi.messenger.xmpp.XMPPApi
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.koin.core.get

class RosterManager : AbstractManager() {
    val xmppApi = get<XMPPApi>()

    fun updateChatsFromRoster() {
        val entries = xmppApi.getRosterEntries()
        entries?.forEach {
            updateChat(it)
        }
    }

    fun presenceChanged(presence: Presence) {
        val jid = presence.from.asEntityBareJidIfPossible().asEntityBareJidString()
        val contact = contactRepo.getContactById(jid)
        val isOnline = presence.isAvailable
        contact?.let{
            if (isOnline && !it.isOnline) {
                it.isOnline = isOnline
            } else return
            contactRepo.updateContact(it)
        }
    }

    private fun updateChat(entry: RosterEntry) {
        val jid = entry.jid.asEntityBareJidIfPossible().asEntityBareJidString()
        if (jid.isMultiChat()) makeMucChatFromEntry(entry)
        else makeSingleChatFromEntry(entry)
    }

    private fun makeMucChatFromEntry(entry: RosterEntry) {
        chatRepo.getChatById(entry.jid.asEntityBareJidIfPossible().asEntityBareJidString())?.let { return }
        val name = xmppApi.getMucChatInfo(entry)
        val chat = ChatDto(entry.jid.asEntityBareJidIfPossible().asEntityBareJidString(), true, name)
        chatRepo.addChat(chat)
    }

    private fun makeSingleChatFromEntry(entry: RosterEntry) {
        val contact = contactRepo.getContactById(entry.jid.asEntityBareJidIfPossible().asEntityBareJidString())
        val vCard = xmppApi.getVcard(entry.jid)
        if (contact != null) updateContactFromEntry(vCard, contact)
        else newContact(vCard, entry)
        chatRepo.getChatById(entry.jid.asEntityBareJidIfPossible().asEntityBareJidString())?.let { return }
        val name = vCard?.nickName ?: entry.name
        val chat = ChatDto(entry.jid.asEntityBareJidIfPossible().asEntityBareJidString(), false, name)
        chatRepo.addChat(chat)
    }

    private fun newContact(
        vCard: VCard?,
        entry: RosterEntry
    ) {
        val jid = entry.jid.asEntityBareJidIfPossible().asEntityBareJidString()
        val name = vCard?.nickName ?: entry.name
        val avatarHash = xmppApi.getAvatarHash(vCard)
        val avatar = xmppApi.getAvatar(vCard)
        val contact = ContactDto(jid, name, avatarHash, avatar)
        contactRepo.saveContact(contact)
    }

    private fun updateContactFromEntry(
        vCard: VCard?,
        contact: ContactDto
    ) {
        val avatarHash = xmppApi.getAvatarHash(vCard)
        if (contact.avatarHash != avatarHash){
            val avatar = xmppApi.getAvatar(vCard)
            contactRepo.updateContact(contact.copy(avatarHash = avatarHash, avatar = avatar))
        }

    }
}