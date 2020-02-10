package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.controllers.ContactSelectController
import org.koin.core.KoinComponent

class ContactSelectActivityViewModel : ViewModel(), KoinComponent {
    private val contactSelectManager =
        ContactSelectController()

    var contacts: LiveData<List<BZContact>> = contactSelectManager.contacts

    fun loadContacts() =
        contactSelectManager.loadContacts()


}