package ooo.emessi.messenger.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.view_item_model.chat.ChatForwardViewItem
import ooo.emessi.messenger.data.repo.ChatRepo
import org.koin.core.KoinComponent
import org.koin.core.get

class ChatPickController : KoinComponent {
    private val chatRepo: ChatRepo = get()
    val chatsW: MutableLiveData<List<ChatForwardViewItem>> = MutableLiveData()
    val selectedChats = Transformations.map(chatsW) { list -> list.filter { true } } // Change

    fun loadContacts() = CoroutineScope(Dispatchers.IO).launch {

    }

    fun handlePickContact(jid: String) {
        Log.d("ContactPicker", jid)
//        chatsW.value = chatsW.value!!.map {
//            if (it.contact.contactJid == jid) it.copy(isSelected = !it.isSelected)
//            else it
//        }

    }

    fun handleUnpickContact(jid: String) {
//        chatsW.value = chatsW.value!!.map {
//            if (it.contact.contactJid == jid) it.copy(isSelected = false)
//            else it
//        }
    }
}