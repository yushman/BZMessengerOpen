package ooo.emessi.messenger.xmpp.chat

import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto
import ooo.emessi.messenger.utils.toEntityBareJid
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muclight.MUCLightAffiliation
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.Jid

class MultyXChat(jid: EntityBareJid) : AbstractXChat(jid) {

    private val chat = managerFactory.getMucLightManager().getMultiUserChatLight(jid)

    override fun send(message: Message): Boolean {
        var result = false
        try {
            chat.sendMessage(message)
            result = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    override fun leave() {
        val currentOccupants = getAffiliations()
        if (currentOccupants[managerFactory.getMyJid()] == MUCLightAffiliation.owner)
            chat.destroy()
        else chat.leave()
    }

    fun getAffiliations() = chat.affiliations

    fun addAffiliations(contactDtos: List<ContactDto>) {
        val occupants = HashMap<Jid, MUCLightAffiliation>()
        val currentOccupants = getAffiliations()
        if (currentOccupants[managerFactory.getMyJid()] == MUCLightAffiliation.owner) {
            contactDtos.forEach {
                val jid = it.contactJid.toEntityBareJid()
                if (currentOccupants.containsKey(jid)) return
                occupants[jid] = MUCLightAffiliation.member
            }
            try {
                chat.changeAffiliations(occupants)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun removeAffiliation(mucAffiliationDto: MucAffiliationDto) {
        val occupants = HashMap<Jid, MUCLightAffiliation>()
        val currentOccupants = getAffiliations()
        if (currentOccupants[managerFactory.getMyJid()] == MUCLightAffiliation.owner) {
            currentOccupants[mucAffiliationDto.affiliationJid] = MUCLightAffiliation.none
            try {
                chat.changeAffiliations(occupants)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun create(roomName: String, occupants: List<Jid>) {
        chat.create(roomName, occupants)
    }

    fun isMeOwner(): Boolean {
        return getAffiliations()[managerFactory.getMyJid()] == MUCLightAffiliation.owner
    }
}