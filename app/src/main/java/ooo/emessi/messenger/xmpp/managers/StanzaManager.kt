package ooo.emessi.messenger.xmpp.managers

import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Stanza
import org.jxmpp.jid.Jid
import org.koin.core.KoinComponent
import timber.log.Timber

class StanzaManager(private val managerFactory: XMPPManagersFactory) : KoinComponent {

    fun setupStanzaListener(listener: (stanza: Stanza) -> Unit) {
        managerFactory.getConnection().addAsyncStanzaListener(
            { packet -> listener.invoke(packet) },
            { stanza -> stanzaFilter(stanza) })
    }

    fun setupDeliveryListener(listener: (fromJid: Jid, toJid: Jid, receiptId: String, receipt: Stanza) -> Unit) {
        managerFactory.getDeliveryReceiptManager()
            .addReceiptReceivedListener { fromJid, toJid, receiptId, receipt ->
                listener.invoke(fromJid, toJid, receiptId, receipt)
            }
    }

    private fun stanzaFilter(stanza: Stanza): Boolean {
        Timber.d("bzmmedia%s", stanza.hasExtension("bzm:media:1").toString())
        return when (stanza) {
            is Message -> !stanza.body.isNullOrEmpty() || stanza.hasExtension(
                "media",
                "bzm:media:1"
            )
            is Presence -> true
            is IQ -> false
            else -> false
        }
    }
}