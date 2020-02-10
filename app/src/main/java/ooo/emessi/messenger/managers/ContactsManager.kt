package ooo.emessi.messenger.managers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.controllers.ChatListController
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.utils.toContact
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.RosterEntry
import org.jxmpp.jid.impl.JidCreate
import org.koin.core.KoinComponent
import org.koin.core.get

class ContactsManager : KoinComponent{

    private val DEFAULT_GROUP = "General"

    private val contactRepo: ContactRepo = get()
    private val chatRepo: ChatRepo = get()
    val contact: MutableLiveData<BZContact> = MutableLiveData()
    val contacts: LiveData<List<BZContact>> = contactRepo.loadContacts()


    fun addContact(jid: String, nickName: String, avatar: String? = null):BZContact{
        val bzContact = BZContact(jid, nickName, avatar)
        addContact(bzContact)
        return bzContact
    }

    private fun addContact(contact: BZContact){
        RosterManager.createEntry(contact.contactJid, contact.nickName, DEFAULT_GROUP)
        contactRepo.saveContact(contact)
        val chatListManager =
            ChatListController()
        chatListManager.loadChatsFromRoster()
    }

    fun getContact(jid: String) = CoroutineScope(Dispatchers.IO).launch {
        contact.postValue(contactRepo.getContactById(jid))
    }

    fun clearRoster() = CoroutineScope(Dispatchers.IO).launch {
        val e = RosterManager.getFullRosterEntries()
        e.forEach{
            RosterManager.removeEntry(it.jid)
        }
    }

//    fun loadContactsFromRoaster() = CoroutineScope(Dispatchers.IO).launch {
//        val rosterContacts = RosterManager.getContactEntries()
//        rosterContacts.forEach {
//            var contact = updateContactFromRoster(it)
//            attachToChat(contact)
//            contactRepo.saveContact(contact)
//        }
//    }

    fun updateContactFromRoster(it: RosterEntry): BZContact? {
        if (RosterManager.isMulti(it)) return null
        var contact = contactRepo.getContactById(it.jid.asEntityBareJidIfPossible().toString())
        if (contact == null) contact = it.toContact()
        contact.apply {
            val vCard = RosterManager.getVCard(it.jid)
            val avatarHash = RosterManager.getAvatarHash(vCard)
            Log.i(this@ContactsManager.javaClass.simpleName, "${it.jid} vcard ${vCard.toString()}")
            Log.i(this@ContactsManager.javaClass.simpleName, "${it.jid} vcard ${avatarHash.toString()}")
            if (this.avatarHash != avatarHash) {//
                this.avatarHash = avatarHash
                this.avatar = RosterManager.getAvatar(vCard)
            }
            val isOnline = RosterManager.getLastPresence(it.jid).isAvailable
            try {
                this.isOnline = isOnline
                this.lastVisit = System.currentTimeMillis() - XMPPConnectionApi.getLastActivityManager().getLastActivity(it.jid).lastActivity * 1000
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
        Log.d("CONTACTS", contact.avatar.toString())
        contactRepo.saveContact(contact)
        return contact
    }

    fun removeContact(jid: String) = CoroutineScope(Dispatchers.IO).launch{
        RosterManager.removeEntry(jid)
        contactRepo.deleteContactById(jid)
    }

    fun presenceChanged(presence: Presence) = CoroutineScope(Dispatchers.IO).launch {
        val jid = JidCreate.entityBareFrom(presence.from).toString()
        val chat = chatRepo.getChatById(jid)
        val contact = contactRepo.getContactById(jid)
        val isOnline = presence.isAvailable
        if (contact != null) {
            if (isOnline && !contact.isOnline) {
                contact.isOnline = isOnline
            } else return@launch
            contactRepo.saveContact(contact)
            chatRepo.updateChat(chat!!.copy(contact = contact))
        }
    }

    fun attachToChat(contact: BZContact) = CoroutineScope(Dispatchers.IO).launch{
        val chat = chatRepo.getChatById(contact.contactJid)
        if (chat != null) {
            if (chat.contact != null){
                chat.contact = contact
                chatRepo.updateChat(chat)
            }
        }
    }
}