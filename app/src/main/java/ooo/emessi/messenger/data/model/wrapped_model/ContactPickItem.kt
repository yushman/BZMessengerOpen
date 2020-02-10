package ooo.emessi.messenger.data.model.wrapped_model

import ooo.emessi.messenger.data.model.bz_model.contact.BZContact

data class ContactPickItem(
    val isSelected: Boolean,
    val hasLeftChar: Boolean,
    val contact: BZContact
)
