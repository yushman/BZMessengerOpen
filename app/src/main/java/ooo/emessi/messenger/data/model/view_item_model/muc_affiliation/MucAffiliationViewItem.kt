package ooo.emessi.messenger.data.model.view_item_model.muc_affiliation

import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto

data class MucAffiliationViewItem(
    val affiliation: MucAffiliationDto,
    val contact: ContactDto?
)