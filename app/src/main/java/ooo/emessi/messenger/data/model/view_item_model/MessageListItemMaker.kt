package ooo.emessi.messenger.data.model.view_item_model

import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.model.view_item_model.message.MessageListViewItem
import ooo.emessi.messenger.data.model.view_item_model.message.MessageViewItem
import ooo.emessi.messenger.data.model.view_item_model.message.MessageViewItemContent
import ooo.emessi.messenger.utils.toDate

class MessageListItemMaker : AbstractMaker() {

    fun makeList(list: List<MessageViewItem>?): List<MessageListViewItem> {
        if (list.isNullOrEmpty()) return emptyList()
        return calculateDateHeaders(list)
    }

    private fun calculateDateHeaders(list: List<MessageViewItem>): List<MessageListViewItem> {
        val myLastMessageIndex = list.indexOfLast { !it.messageDto.isIncoming }
        val resultItemList = mutableListOf<MessageListViewItem>()

        // if messages empty return empty wrapper
        if (list.isNullOrEmpty()) {
            resultItemList.add(
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.DATE_HEADER,
                    MessageViewItemContent.DateHeader(
                        System.currentTimeMillis().toDate()
                    )
                )
            )
            return resultItemList
        }

        // if only one or more message return wrapped
        if (list.size == 1) {
            val message = list.first()
            resultItemList.add(createHeader(message))
            resultItemList.add(calculateMessageType(message, false, myLastMessageIndex == 0))
        } else {
            val messageF = list.first()
            resultItemList.add(createHeader(messageF))
            resultItemList.add(calculateMessageType(messageF, false, myLastMessageIndex == 0))
            for (i in 1 until list.size) {
                val current = list[i]
                val previous = list[i - 1]
                if (current.messageDto.timeStamp - previous.messageDto.timeStamp > 24 * 60 * 60 * 1000) {//!current.timeStamp.toDate().isSameDay(previous.timeStamp.toDate())
                    resultItemList.add(createHeader(current))
                    resultItemList.add(
                        calculateMessageType(
                            current,
                            isBottom(current, previous),
                            i == myLastMessageIndex
                        )
                    )
                } else resultItemList.add(
                    calculateMessageType(
                        current,
                        isBottom(current, previous),
                        i == myLastMessageIndex
                    )
                )
            }
        }
        return resultItemList
    }

    private fun createHeader(message: MessageViewItem) = MessageListViewItem(
        MessageListViewItem.MessageViewItemType.DATE_HEADER,
        MessageViewItemContent.DateHeader(
            message.messageDto.timeStamp.toDate()
        )
    )

    private fun isBottom(current: MessageViewItem, previous: MessageViewItem) =
        current.messageDto.from == previous.messageDto.from

    private fun calculateMessageType(
        message: MessageViewItem,
        isBottomType: Boolean,
        isEditable: Boolean
    ): MessageListViewItem {
        var messageWrapper: MessageListViewItem? = null
        val isIncoming = message.messageDto.isIncoming
        val isMuc = message.messageDto.chatJid.contains("@muclight")
        val isAttachment = message.messageDto.payloadType != MessageDto.PayloadType.NONE
        when {
            !isIncoming && !isBottomType && !isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.MY_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            !isIncoming && isBottomType && !isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.MY_BOTTOM_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            !isIncoming && !isBottomType && isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.MY_ATTACHMENT_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            !isIncoming && isBottomType && isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.MY_ATTACHMENT_BOTTOM_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            isIncoming && !isBottomType && !isMuc && !isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.THEIR_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            isIncoming && isBottomType && !isMuc && !isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.THEIR_BOTTOM_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            isIncoming && !isBottomType && !isMuc && isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.THEIR_ATTACHMENT_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            isIncoming && isBottomType && !isMuc && isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.THEIR_ATTACHMENT_BOTTOM_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            isIncoming && !isBottomType && isMuc && !isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.THEIR_MUC_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            isIncoming && isBottomType && isMuc && !isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.THEIR_MUC_BOTTOM_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            isIncoming && !isBottomType && isMuc && isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.THEIR_MUC_ATTACHMENT_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
            isIncoming && isBottomType && isMuc && isAttachment ->  messageWrapper =
                MessageListViewItem(
                    MessageListViewItem.MessageViewItemType.THEIR_MUC_ATTACHMENT_BOTTOM_MESSAGE,
                    MessageViewItemContent.MessageItem(message, isEditable)
                )
        }
        return messageWrapper!!
    }
}