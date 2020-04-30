package ooo.emessi.messenger.xmpp.chat

import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid
import org.koin.core.KoinComponent

class SingleXChat(jid: EntityBareJid) : AbstractXChat(jid), KoinComponent {

    private val chat = managerFactory.getChatManager().chatWith(jid)

    override fun send(message: Message): Boolean {
        var result = false
        try {
            chat.send(message)
            result = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    override fun leave() {
        //Todo
    }
}