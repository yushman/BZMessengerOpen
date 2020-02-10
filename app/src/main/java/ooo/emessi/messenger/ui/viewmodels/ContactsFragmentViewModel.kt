package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.managers.ContactsManager
import org.koin.core.KoinComponent

class ContactsFragmentViewModel: ViewModel(), KoinComponent {
    private val TAG = this.javaClass.simpleName

    private val contactsManager = ContactsManager()

    var contacts: LiveData<List<BZContact>> = contactsManager.contacts

//    fun loadContactsFromRoster() {
//        contactsManager.loadContactsFromRoaster()
//        Log.d(TAG, "load contacts from roster")
//    }

}
