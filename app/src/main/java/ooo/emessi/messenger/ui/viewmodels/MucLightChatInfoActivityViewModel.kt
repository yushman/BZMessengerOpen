package ooo.emessi.messenger.ui.viewmodels

import ooo.emessi.messenger.controllers.MUCLightChatInfoController
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto

class MucLightChatInfoActivityViewModel (private val chatDto: ChatDto) : AbstractChatInfoViewModel(chatDto){
    override val chatInfoController = MUCLightChatInfoController(chatDto)
    val affiliations = chatInfoController.affiliations
    val isMeOwner = chatInfoController.isMeOwner

    fun loadAffiliations(){
        chatInfoController.loadAffiliations()
    }

    fun deleteAffiliation(affiliationDto: MucAffiliationDto) {
        chatInfoController.deleteAffiliation(affiliationDto)
    }
}