package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.controllers.AbstractContactListController

abstract class AbstractContactListViewModel : ViewModel(){
    abstract val contactListController: AbstractContactListController
    val contacts = contactListController.contactViewItems
    val newChat = contactListController.newChat


    fun loadContacts() {
        contactListController.loadContacts()
    }
}