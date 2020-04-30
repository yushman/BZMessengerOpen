package ooo.emessi.messenger.xmpp.managers

import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.utils.convertCompressedByteArrayToBitmap
import ooo.emessi.messenger.utils.saveTempBitmap
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.jxmpp.jid.BareJid
import org.jxmpp.jid.Jid

class XRosterManager(val managersFactory: XMPPManagersFactory) {

    private val MAIN_GROUP_NAME = "General"
    private val roster = managersFactory.getRoster()
    private val vCardManager = managersFactory.getVCardManager()

    fun getRoster() = roster

    fun getFullRosterEntries() = roster.entries

    fun getFullRosterEntriesJids() = getFullRosterEntries().map { it.jid.toEntityBareJid().toString() }

    fun getContactEntries() = roster.entries.filter { !it.jid.contains("@muclight") }

    fun getContactEntriesJids() = getContactEntries().map { it.jid.toEntityBareJid().toString() }

    fun isMulti(jid: BareJid) = roster.getEntry(jid).jid.contains("@muclihgt")
    fun isMulti(jid: String) =
        isMulti(jid.toEntityBareJid())
    fun isMulti(entry: RosterEntry) = entry.jid.contains("@muclight")

    fun getLastPresence(jid: Jid) = roster.getPresence(jid.asBareJid())
    fun getLastPresence(jid: String) = roster.getPresence(jid.toEntityBareJid())

    fun removeEntry(entry: RosterEntry): Boolean{
        roster.removeEntry(entry)
        entry.cancelSubscription()
        return true
    }
    fun removeEntry(jid: BareJid): Boolean {
        val entry = roster.getEntry(jid) ?: return false
        return removeEntry(entry)
    }

    fun removeEntry(chatDto: ChatDto) =
        removeEntry(chatDto.jid.toEntityBareJid())

    fun createEntry(jid: BareJid, name: String, groups: String) = roster.createItemAndRequestSubscription(jid, name, arrayOf(groups))
    fun createEntry(chatDto: ChatDto) =
        createEntry(chatDto.jid.toEntityBareJid(), chatDto.name, MAIN_GROUP_NAME)

    fun isInRoster(jid: BareJid) = roster.contains(jid)
    fun isInRoster(jid: String) = roster.contains(jid.toEntityBareJid())

    fun getVCard(jid: Jid): VCard? {
        var vCard: VCard? = null
//        val vc = vCardManager.loadVCard()
        try {
//            if (vCardManager.isSupported(jid.asEntityBareJidIfPossible()))
            vCard = vCardManager.loadVCard(jid.asEntityBareJidIfPossible())
        } catch (e: Exception){
            e.printStackTrace()
        }
        return vCard
    }
    fun getVCard(jid: String) =
        getVCard(jid.toEntityBareJid())

    fun getAvatarHash(vCard: VCard?) = vCard?.avatarHash

    fun getAvatar(vCard: VCard?): String? {
        var avatar: String? = null
        if (vCard != null){
            val avatarBytes = vCard.avatar
            val bitmap = convertCompressedByteArrayToBitmap(avatarBytes)
            avatar = saveTempBitmap(bitmap, vCard.from.asEntityBareJidIfPossible().toString())
        }
        return avatar
    }
    fun getAvatar(jid: String) =
        getAvatar(
            getVCard(jid.toEntityBareJid())
        )

    fun getContactName(entry: RosterEntry): String{
        return if (isMulti(entry)) getNameFromMUCConfig(entry.jid)
        else entry.name
    }

    private fun getNameFromMUCConfig(jid: BareJid): String {
        val chat = managersFactory.getMucLightManager()
            .getMultiUserChatLight(jid.asEntityBareJidIfPossible())
        return chat.configuration.roomName
    }
}