package ooo.emessi.messenger.controllers

import androidx.lifecycle.MutableLiveData
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto
import ooo.emessi.messenger.data.model.view_item_model.MucAffiliationViewMaker
import ooo.emessi.messenger.data.model.view_item_model.muc_affiliation.MucAffiliationViewItem
import ooo.emessi.messenger.managers.chat.MucChatManager
import org.jivesoftware.smackx.muclight.MUCLightAffiliation

class MUCLightChatInfoController (private val chatDto: ChatDto): AbstractChatInfoController(chatDto){

    override val chatManager = MucChatManager(chatDto)
    val affiliations: MutableLiveData<List<MucAffiliationViewItem>> = MutableLiveData()
    val isMeOwner = chatManager.isMeOwner

    fun loadAffiliations() {
        chatManager.getIsMeOwner()
        affiliations.postValue(cookMucAffiliationViewItems(chatManager.loadAffiliations()))

    }

    fun deleteAffiliation(affiliationDto: MucAffiliationDto){
        chatManager.deleteAffiliation(affiliationDto)
    }

    private fun cookMucAffiliationViewItems(list: List<MucAffiliationDto>?): List<MucAffiliationViewItem> {
        var result = listOf<MucAffiliationViewItem>()
        if (list.isNullOrEmpty()) return result
        result = MucAffiliationViewMaker().makeList(list)
        return result.filter { it.affiliation.affiliationType != MUCLightAffiliation.none }
            .sortedBy { it.affiliation.affiliationType }
    }
}