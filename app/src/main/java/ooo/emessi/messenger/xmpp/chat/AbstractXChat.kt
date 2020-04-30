package ooo.emessi.messenger.xmpp.chat

import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class AbstractXChat(jid: EntityBareJid) : KoinComponent {
    protected val managerFactory by inject<XMPPManagersFactory>()
    abstract fun send(message: Message): Boolean
    abstract fun leave()
}