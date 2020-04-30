package ooo.emessi.messenger.data.model.view_item_model

import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto
import ooo.emessi.messenger.data.model.view_item_model.muc_affiliation.MucAffiliationViewItem
import ooo.emessi.messenger.data.repo.ContactRepo
import org.koin.core.KoinComponent
import org.koin.core.get

class MucAffiliationViewMaker : KoinComponent{
    val contactRepo = get<ContactRepo>()

    fun makeList(list: List<MucAffiliationDto>): List<MucAffiliationViewItem>{
        val result = listOf<MucAffiliationViewItem>()
        if (list.isEmpty()) return result
        return list.map { make(it) }
    }

    fun make(dto: MucAffiliationDto) =
        MucAffiliationViewItem(dto, contactRepo.getContactById(dto.affiliationJid.asEntityBareJidIfPossible().asEntityBareJidString()))
}