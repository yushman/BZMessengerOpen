package ooo.emessi.messenger.managers.chat

import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.xmpp.chat.SingleXChat

class SingleChatManager (
    private val chatDto: ChatDto,
    private val listener: MessageSendListener? = null
): AbstractChatManager(chatDto, listener) {
    override val xChat = xmppApi.getXChat(chatDto) as SingleXChat
}