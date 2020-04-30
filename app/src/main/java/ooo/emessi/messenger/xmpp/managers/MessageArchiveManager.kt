package ooo.emessi.messenger.xmpp.managers

import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.utils.toDate
import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.mam.MamManager
import timber.log.Timber

class MessageArchiveManager(private val managersFactory: XMPPManagersFactory) {

    private val monthAgo = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L

    fun loadMessageArchive(lastKnownMessageDto: MessageDto?): MutableList<Message> {
        val messageList = mutableListOf<Message>()
        val querySinceTime = lastKnownMessageDto?.timeStamp ?: monthAgo
        try {
            val mamManager = managersFactory.getMamManager()
            Timber.i(mamManager.isSupported.toString())
            mamManager.enableMamForAllMessages()
            val mamQueryArgs = MamManager.MamQueryArgs.builder()
                .limitResultsSince(querySinceTime.toDate())
                .setResultPageSizeTo(50)
                .build()
            val mamQuery = mamManager.queryArchive(mamQueryArgs) ?: return messageList
            do {
                val forwardedMessages = mamQuery.messages
                messageList.addAll(forwardedMessages)
                mamQuery.pageNext(50)
            } while (!mamQuery.isComplete)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return messageList
    }
}