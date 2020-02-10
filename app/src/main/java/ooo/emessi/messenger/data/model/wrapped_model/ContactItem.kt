package ooo.emessi.messenger.data.model.wrapped_model

import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import org.jivesoftware.smackx.muclight.MUCLightAffiliation

data class ContactItem(
    val contact: BZContact,
    val affiliation: MUCLightAffiliation? = null
)