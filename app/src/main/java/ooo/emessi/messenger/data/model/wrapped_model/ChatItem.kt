package ooo.emessi.messenger.data.model.wrapped_model

import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import org.jivesoftware.smackx.muclight.MUCLightAffiliation

data class ChatItem(
    val chat: BZChat,
    var lastMessageItem: MessageItem? = null
)