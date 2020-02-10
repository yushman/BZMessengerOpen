package ooo.emessi.messenger.managers

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.model.bz_model.muc_affiliation.BZMucAffiliation
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smackx.muclight.MUCLightAffiliation
import org.jivesoftware.smackx.muclight.MultiUserChatLight
import org.jxmpp.jid.Jid
import org.koin.core.KoinComponent
import org.koin.core.inject

class MucChatManager (val jid: String): KoinComponent{

    val chatManager = ChatManager(jid)
    private val messageRepo: MessageRepo by inject()
    private val chatRepo: ChatRepo by inject()
    private val contactRepo: ContactRepo by inject()

    val chat: MultiUserChatLight by lazy { getMUChat() }

    val affiliations: MutableLiveData<List<BZMucAffiliation>> = MutableLiveData()
    val isMeOwner: MutableLiveData<Boolean> = MutableLiveData()



    suspend fun loadAffiliations()= CoroutineScope(Dispatchers.IO).launch  {
        val af = chat.affiliations
        val _isMeOwner = af[XMPPConnectionApi.getMyJid().asEntityBareJid()] == MUCLightAffiliation.owner
        val afList = af.map { createAffiliation(it.key, it.value) }
//        af.forEach { jid, mucLightAffiliation -> afList.add(createAffiliation(jid, mucLightAffiliation)) }

        isMeOwner.postValue(_isMeOwner)
        affiliations.postValue(afList)
    }

    suspend fun deleteAffiliation(affiliation: BZMucAffiliation) = CoroutineScope(Dispatchers.IO).launch {
        val occupants = HashMap<Jid, MUCLightAffiliation>()
        occupants[affiliation.affiliationJid] = MUCLightAffiliation.none
        try {
            chat.changeAffiliations(occupants)
        } catch (e: Exception){
            e.printStackTrace()
        }
        loadAffiliations()
    }

    suspend fun createChat(roomName: String, occupants: List<Jid>) {
        chat.create(roomName, occupants)
        val chat = BZChat(jid, roomName)
        chatRepo.addChat(chat)
    }

    suspend fun flushChatUnread() {
        chatManager.flushChatUnread()
    }

    suspend fun deleteChat() {
        try {
            val isMeOwner = chat.affiliations[XMPPConnectionApi.getMyJid()] == MUCLightAffiliation.owner
            if (isMeOwner) chat.destroy()
            else chat.leave()
        } catch (e: Exception){ e.printStackTrace() }
        chatManager.deleteChat()
    }

    suspend fun updateChatLastActivity() {
        chatManager.updateChatLastActivity()
    }

    suspend fun updateChatLastMessage() {
        chatManager.updateChatLastMessage()
    }

    suspend fun addOccupants(occupants: List<Jid>) {
        val _occupants = HashMap<Jid, MUCLightAffiliation>()
        val currentOccupants = chat.affiliations
        occupants.forEach {
            if (currentOccupants.containsKey(it)) return@forEach
            _occupants[it] = MUCLightAffiliation.member
        }
        try {
            chat.changeAffiliations(_occupants)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }


    private fun createAffiliation(jid: Jid, mucLightAffiliation: MUCLightAffiliation): BZMucAffiliation {
        var contact = contactRepo.getContactById(jid.asEntityBareJidIfPossible().toString())
        if (contact == null) {
            contact = BZContact(jid.asEntityBareJidIfPossible().toString(), jid.localpartOrNull.asUnescapedString())
            contactRepo.saveContact(contact)
        }
        return BZMucAffiliation(
            jid,
            mucLightAffiliation,
            contact
        )
    }

    private fun getMUChat() = XMPPConnectionApi.getMucLightManager().getMultiUserChatLight(jid.toEntityBareJid())
}