package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.controllers.MUCLightChatInfoController
import ooo.emessi.messenger.data.model.bz_model.muc_affiliation.BZMucAffiliation
import ooo.emessi.messenger.data.repo.ChatRepo
import org.koin.core.KoinComponent
import org.koin.core.get

class MucLightChatInfoActivityViewModel (private val chatId: String) : ViewModel(), KoinComponent{
    private val chatRepo: ChatRepo = get()

    private val chatInfoController =
        MUCLightChatInfoController(chatId)
    val chat = chatRepo.loadChatById(chatId)
    val affiliations: LiveData<List<BZMucAffiliation>> = chatInfoController.affiliations
    var isMeOwner: LiveData<Boolean> = chatInfoController.isMeOwner

    fun leaveChat() {
        chatInfoController.deleteChat()
    }

    fun loadAffiliations() {
        chatInfoController.loadAffiliations()
    }

    fun deleteAffiliation(affiliation: BZMucAffiliation) {
        chatInfoController.deleteAffiliation(affiliation)
    }
}