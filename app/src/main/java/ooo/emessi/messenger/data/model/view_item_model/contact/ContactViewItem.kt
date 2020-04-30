package ooo.emessi.messenger.data.model.view_item_model.contact

import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto

data class ContactViewItem(
    val contactDto: ContactDto,
    val hasLeftChar: Boolean
)