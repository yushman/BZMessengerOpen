package ooo.emessi.messenger.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.controllers.ChatPickController
import org.koin.core.KoinComponent

class ForwardMessageViewModel : ViewModel(), KoinComponent {
    private val chatPickController =
        ChatPickController()

    var contacts = chatPickController.chatsW
    var selectedContacts = chatPickController.selectedChats

    fun handlePickChat(jid: String) {
        chatPickController.handlePickContact(jid)
    }

    fun handleUnpickChat(jid: String) {
        chatPickController.handleUnpickContact(jid)
    }

    fun loadChats() {
        chatPickController.loadContacts()
    }

    fun createForwardMessage(messageId: String) {
        TODO("Not yet implemented")
    }

    fun createTextMessage(text: String) {

    }

    fun createImageMessage(uri: Uri) {
        TODO("Not yet implemented")
    }
}