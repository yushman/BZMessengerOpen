package ooo.emessi.messenger.data.model.dto_model.muc_affiliation

import org.jivesoftware.smackx.muclight.MUCLightAffiliation
import org.jxmpp.jid.Jid

data class MucAffiliationDto(
    val affiliationJid: Jid,
    val affiliationType: MUCLightAffiliation)