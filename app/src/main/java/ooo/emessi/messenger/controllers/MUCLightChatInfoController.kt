package ooo.emessi.messenger.controllers

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.muc_affiliation.BZMucAffiliation
import ooo.emessi.messenger.managers.MucChatManager
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class MUCLightChatInfoController (val chatId: String): KoinComponent{

    val chatManager = MucChatManager(chatId)

    val affiliations: LiveData<List<BZMucAffiliation>> = chatManager.affiliations
    var isMeOwner: LiveData<Boolean> = chatManager.isMeOwner

    fun deleteChat() = CoroutineScope(Dispatchers.IO).launch {
        chatManager.deleteChat()
    }

    fun loadAffiliations() = CoroutineScope(Dispatchers.IO).launch {
        chatManager.loadAffiliations()
    }

    fun deleteAffiliation(affiliation: BZMucAffiliation) = CoroutineScope(Dispatchers.IO).launch {
        chatManager.deleteAffiliation(affiliation)
    }

}