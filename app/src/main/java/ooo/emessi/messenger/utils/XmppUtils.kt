package ooo.emessi.messenger.utils

import android.text.TextUtils
import android.util.Patterns
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import org.jivesoftware.smack.roster.RosterEntry
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate

fun RosterEntry.toContact(): ContactDto {
    return ContactDto(
        this.jid.asEntityBareJidIfPossible().toString(),
        this.name
    )
}

fun String.toEntityBareJid(): EntityBareJid {
    return if (!this.jidIsValid()) JidCreate.entityBareFrom("1$this@mossales.ru")
    else JidCreate.entityBareFrom(this)
}
fun Jid.toEntityBareJid() = JidCreate.entityBareFrom(this)
fun String.jidIsValid() = !TextUtils.isEmpty(this) &&
        Patterns.EMAIL_ADDRESS.matcher(this).matches() &&
        this.length > 5
