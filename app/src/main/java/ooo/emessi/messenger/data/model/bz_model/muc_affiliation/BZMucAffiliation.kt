package ooo.emessi.messenger.data.model.bz_model.muc_affiliation

import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import org.jivesoftware.smackx.muclight.MUCLightAffiliation
import org.jxmpp.jid.Jid

data class BZMucAffiliation(
    val affiliationJid: Jid,
    val affiliationType: MUCLightAffiliation,
    val affiliationContact: BZContact
)