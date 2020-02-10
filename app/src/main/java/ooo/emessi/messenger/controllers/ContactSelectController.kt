package ooo.emessi.messenger.controllers

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.repo.ContactRepo
import org.koin.core.KoinComponent
import org.koin.core.get

class ContactSelectController : KoinComponent{
    private val contactRepo: ContactRepo = get()
    val contacts: MutableLiveData<List<BZContact>> = MutableLiveData()

    fun loadContacts() = CoroutineScope(Dispatchers.IO).launch{
        contacts.postValue(contactRepo.getContacts())
    }
}