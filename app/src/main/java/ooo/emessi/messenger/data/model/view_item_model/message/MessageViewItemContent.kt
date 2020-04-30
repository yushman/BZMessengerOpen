package ooo.emessi.messenger.data.model.view_item_model.message

import java.util.*

sealed class MessageViewItemContent {
    data class DateHeader(val date: Date):
        MessageViewItemContent()
    data class MessageItem(
        val message: MessageViewItem,
        var isEditable: Boolean = false
    ): MessageViewItemContent()
}



