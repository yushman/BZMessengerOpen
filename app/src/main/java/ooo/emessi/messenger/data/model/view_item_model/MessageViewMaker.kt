package ooo.emessi.messenger.data.model.view_item_model

import ooo.emessi.messenger.data.model.dto_model.AttachmentDtoMaker
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.model.view_item_model.message.MessageViewItem

class MessageViewMaker : AbstractMaker() {

    private val attachmentDtoMaker = AttachmentDtoMaker()

    fun make(messageDto: MessageDto): MessageViewItem {
        val body = if (messageDto.isCorrected) messageDto.messageCorrectedBody else messageDto.body
        val contact = contactRepo.getContactById(messageDto.from)
        val attachment =
            if (messageDto.payloadType != MessageDto.PayloadType.NONE) attachmentDtoMaker
                .fromJson(
                    messageDto
            ) else null
        val replyedDto =
            if (messageDto.isReplyed) messageRepo.getMessageById(messageDto.messageReplyedId!!) else null
        val replyed = replyedDto?.let { make(it) }
        return MessageViewItem(body!!, messageDto, contact, attachment, replyed)
    }

    fun makeList(list: List<MessageDto>): MutableList<MessageViewItem> {
        val resultList = mutableListOf<MessageViewItem>()
        list.forEach { resultList.add(make(it)) }
        return resultList
    }
}