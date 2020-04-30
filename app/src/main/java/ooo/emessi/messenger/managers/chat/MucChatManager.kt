package ooo.emessi.messenger.managers.chat

import androidx.lifecycle.MutableLiveData
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto
import ooo.emessi.messenger.xmpp.chat.MultyXChat

class MucChatManager(
    private val chatDto: ChatDto,
    private val listener: MessageSendListener? = null
) : AbstractChatManager(chatDto, listener) {

    override val xChat = xmppApi.getXChat(chatDto) as MultyXChat

    val isMeOwner: MutableLiveData<Boolean> = MutableLiveData()

    fun loadAffiliations() =
        xmppApi.getAffiliations(xChat).map { MucAffiliationDto(it.key, it.value) }

    fun getIsMeOwner() = isMeOwner.postValue(xmppApi.isMeXChatOwner(xChat))

    fun deleteAffiliation(affiliationDto: MucAffiliationDto) {
        xmppApi.removeAffiliations(affiliationDto, xChat)
    }

    fun addOccupants(contactsDto: List<ContactDto>) {
        xmppApi.addAffiliations(contactsDto, xChat)
    }
}