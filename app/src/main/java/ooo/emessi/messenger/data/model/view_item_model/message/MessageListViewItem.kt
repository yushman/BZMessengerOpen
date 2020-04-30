package ooo.emessi.messenger.data.model.view_item_model.message

data class MessageListViewItem (
    val type: MessageViewItemType,
    val content: MessageViewItemContent
) {
    enum class MessageViewItemType{
        DATE_HEADER,
        MY_MESSAGE,
        MY_BOTTOM_MESSAGE,
        MY_ATTACHMENT_MESSAGE,
        MY_ATTACHMENT_BOTTOM_MESSAGE,
        THEIR_MESSAGE,
        THEIR_BOTTOM_MESSAGE,
        THEIR_ATTACHMENT_MESSAGE,
        THEIR_ATTACHMENT_BOTTOM_MESSAGE,
        THEIR_MUC_MESSAGE,
        THEIR_MUC_BOTTOM_MESSAGE,
        THEIR_MUC_ATTACHMENT_MESSAGE,
        THEIR_MUC_ATTACHMENT_BOTTOM_MESSAGE
    }
}