package ooo.emessi.messenger.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.wrapped_model.ContactPickItem
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*

class ContactPickController : KoinComponent{
    private val contactRepo: ContactRepo = get()
    val contactsW: MutableLiveData<List<ContactPickItem>> = MutableLiveData()
    val selectedContact = Transformations.map(contactsW) { list -> list.filter { it.isSelected }}

    fun loadContacts() = CoroutineScope(Dispatchers.IO).launch{
        contactsW.postValue(contactRepo.getContacts().map { it.toWrappedContact() })
    }

    fun handlePickContact(jid: String){
        Log.d("ContactPicker", jid)
        contactsW.value = contactsW.value!!.map {
            if (it.contact.contactJid == jid) it.copy(isSelected = !it.isSelected)
            else it
        }

    }

    fun handleUnpickContact(jid: String) {
        contactsW.value = contactsW.value!!.map {
            if (it.contact.contactJid == jid) it.copy(isSelected = false)
            else it
        }
    }

    fun addContactToChat(
        chatId: String,
        selectedList: List<ContactPickItem>
    ) {
        val mucm =
            MUCLightChatsController(chatId)
        val occupants = selectedList.map { it.contact.contactJid.toEntityBareJid() }
        occupants.forEach {
            Log.d("PickManager", it.toString())
        }
        try {
            mucm.addOccupants(occupants)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun createNewChat(name: String) {

        val jid = UUID.randomUUID().toString() + "@muclight." + XMPPConnectionApi.getConnection().xmppServiceDomain.toString()
        val mucm = MUCLightChatsController(jid)
        Log.d("ContactPicker",contactsW.value?.size.toString())
        val occupants = contactsW.value!!.filter { it.isSelected }.map { it.contact.contactJid.toEntityBareJid() }
        mucm.createMucLightChat(name, occupants)
    }
}