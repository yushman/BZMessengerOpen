package ooo.emessi.messenger.ui.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.model.view_item_model.message.MessageListViewItem
import ooo.emessi.messenger.data.model.view_item_model.message.MessageListViewItem.MessageViewItemType
import ooo.emessi.messenger.data.model.view_item_model.message.MessageViewItemContent.DateHeader
import ooo.emessi.messenger.data.model.view_item_model.message.MessageViewItemContent.MessageItem
import ooo.emessi.messenger.utils.*
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.utils.helpers.ColorHelper
import timber.log.Timber

class MessagesAdapter (private val clickListener: (v: View, messageItem: MessageItem) -> Unit):
    RecyclerView.Adapter<MessagesAdapter.AbstractMessageListItemsViewHolder>(){

    companion object{
        private const val DATE_HEADER = 0
        private const val MY_MESSAGE = 10
        private const val MY_ATTACHMENT_MESSAGE = 11
        private const val THEIR_MESSAGE = 20
        private const val THEIR_ATTACHMENT_MESSAGE = 21
        private const val THEIR_MUC_MESSAGE = 30
        private const val THEIR_MUC_ATTACHMENT_MESSAGE = 31

    }

    var messages = listOf<MessageListViewItem>()

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].type){
            MessageViewItemType.DATE_HEADER -> DATE_HEADER
            MessageViewItemType.MY_MESSAGE,
            MessageViewItemType.MY_BOTTOM_MESSAGE -> MY_MESSAGE
            MessageViewItemType.MY_ATTACHMENT_MESSAGE,
            MessageViewItemType.MY_ATTACHMENT_BOTTOM_MESSAGE -> MY_ATTACHMENT_MESSAGE
            MessageViewItemType.THEIR_MESSAGE,
            MessageViewItemType.THEIR_BOTTOM_MESSAGE -> THEIR_MESSAGE
            MessageViewItemType.THEIR_ATTACHMENT_MESSAGE,
            MessageViewItemType.THEIR_ATTACHMENT_BOTTOM_MESSAGE -> THEIR_ATTACHMENT_MESSAGE
            MessageViewItemType.THEIR_MUC_MESSAGE,
            MessageViewItemType.THEIR_MUC_BOTTOM_MESSAGE -> THEIR_MUC_MESSAGE
            MessageViewItemType.THEIR_MUC_ATTACHMENT_MESSAGE,
            MessageViewItemType.THEIR_MUC_ATTACHMENT_BOTTOM_MESSAGE -> THEIR_MUC_ATTACHMENT_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractMessageListItemsViewHolder {
        val li = LayoutInflater.from(parent.context)
        return when (viewType) {
            DATE_HEADER -> DateHeaderViewHolder(li.inflate(R.layout.item_date_header, parent, false))
            MY_MESSAGE -> MyMessageViewHolder(li.inflate(R.layout.my_message_item, parent, false))
            MY_ATTACHMENT_MESSAGE -> MyAttachmentMessageViewHolder(li.inflate(R.layout.my_image_message_item, parent, false))
            THEIR_MESSAGE -> TheirMessageViewHolder(li.inflate(R.layout.their_message_item, parent, false))
            THEIR_ATTACHMENT_MESSAGE -> TheirAttachmentMessageViewHolder(li.inflate(R.layout.their_image_message_item, parent, false))
            THEIR_MUC_MESSAGE -> TheirMucMessageViewHolder(li.inflate(R.layout.their_muc_message_item, parent, false))
            THEIR_MUC_ATTACHMENT_MESSAGE -> TheirMucAttachmentMessageViewHolder(li.inflate(R.layout.their_muc_image_message_item, parent, false))
            else -> DateHeaderViewHolder(li.inflate(R.layout.item_date_header, parent, false))
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: AbstractMessageListItemsViewHolder, position: Int) {
        holder.bindView(messages[position])
        holder.bindListenerToItem(messages[position], clickListener)
    }

    fun updateMessages(_messages: List<MessageListViewItem>){

        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return messages[oldItemPosition] == _messages[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return messages.size
            }

            override fun getNewListSize(): Int {
                return _messages.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return messages[oldItemPosition].hashCode() == _messages[newItemPosition].hashCode()
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int) = Any()

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messages = _messages
        diffResult.dispatchUpdatesTo(this)
    }

    interface IAttachment{
        fun bindAttachment(imageView: ImageView, item: MessageListViewItem)

        fun bindFileAttachment(imageView: ImageView, item: MessageItem){
            imageView.setImageResource(R.drawable.ic_cloud_computing)
            if (item.message.payload == null)
                imageView.isEnabled = false
        }

        fun  bindImageAttachment(imageView: ImageView, item: MessageItem){
            val payload = item.message.payload!!
            val path = payload.attachmentPath
            Timber.d(path ?: "none")
                if (path != null)
            Glide.with(imageView.context).load(path)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
                else Glide.with(imageView.context).load(payload.thumb!!.toByteArray())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView)
        }
    }

    abstract inner class AbstractMessageListItemsViewHolder(view: View): RecyclerView.ViewHolder(view){
        val v = view
        val myMessageDrawble = ResourcesCompat.getDrawable(v.resources, R.drawable.my_message, null)
        val myMessageBottomDrawble = ResourcesCompat.getDrawable(v.resources, R.drawable.my_message_bottom, null)
        val theirMessageDrawble = ResourcesCompat.getDrawable(v.resources, R.drawable.their_message, null)
        val theirMessageBottomDrawble = ResourcesCompat.getDrawable(v.resources, R.drawable.their_message_bottom, null)

        abstract fun bindView(item: MessageListViewItem)
        abstract fun bindListenerToItem(item: MessageListViewItem, clickListener: (v: View, messageItem: MessageItem) -> Unit)

        private fun dpToPx(dp: Int): Int{
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                v.context.resources.displayMetrics
            ).toInt()
        }

        fun setMargins(isBottom: Boolean, ltMessageContainer: ConstraintLayout){
            val layoutParams = ltMessageContainer.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = dpToPx(2)
            if (!isBottom) layoutParams.topMargin = dpToPx(4)
            else layoutParams.topMargin = 0
            ltMessageContainer.layoutParams = layoutParams
        }
    }

    open inner class MyMessageViewHolder(view: View) : AbstractMessageListItemsViewHolder(view),
        ItemSwipeble {
        private val tvBody = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_my_time)
        private val ivSended = view.findViewById<ImageView>(R.id.iv_message_sended)
        private val ivDelivered = view.findViewById<ImageView>(R.id.iv_message_delivered)
        private val ivEdited = view.findViewById<ImageView>(R.id.iv_message_edited)
        private val ivWaiting = view.findViewById<ImageView>(R.id.iv_message_waiting)
        private val layoutReply = view.findViewById<ConstraintLayout>(R.id.layout_message_reply)
        private val tvReplyText = view.findViewById<TextView>(R.id.tv_message_action_text)
        private val tvReplyFrom = view.findViewById<TextView>(R.id.tv_message_action_description)
        private val ltMessageContainer = view.findViewById<ConstraintLayout>(R.id.lt_message_container)

        override fun bindView(item: MessageListViewItem) {
            bindBaseParams(item)
        }

        override fun bindListenerToItem(
            item: MessageListViewItem,
            clickListener: (v: View, messageItem: MessageItem) -> Unit
        ) {
            val message = item.content as MessageItem
            v.setOnClickListener { clickListener.invoke(it, message) }
            layoutReply.setOnClickListener { clickListener.invoke(it, message) }
        }

        private fun bindBaseParams(item: MessageListViewItem) {
            setItemBackGround(item.type)
            val message = item.content as MessageItem
            if (message.message.messageDto.isCorrected) {
                ivEdited.visibility = View.VISIBLE
                tvBody.text = message.message.body
            }
            else {
                ivEdited.visibility = View.GONE
                if (message.message.body.isEmpty()) tvBody.visibility = View.GONE //???? INVISIBLE
                else {
                    tvBody.visibility = View.VISIBLE
                    tvBody.text = message.message.body
                }
            }
            tvTime.text = message.message.messageDto.timeStamp.toDate().format(Template.TIME)
            if (!message.message.messageDto.isIncoming){

                if (message.message.messageDto.isReplyed) {
                    layoutReply.visibility = View.VISIBLE
                    tvReplyText.text = message.message.replyedMessage!!.body
                    tvReplyFrom.text = message.message.replyedMessage.getContactName()
                } else layoutReply.visibility = View.GONE
                if (message.message.messageDto.isSended){
                    ivSended.visibility = View.VISIBLE
                    ivWaiting.visibility = View.GONE
                    if (message.message.messageDto.isDelivered) ivDelivered.visibility = View.VISIBLE
                    else ivDelivered.visibility = View.GONE

                } else {
                    ivSended.visibility = View.INVISIBLE
                    ivWaiting.visibility = View.VISIBLE
                    ivDelivered.visibility = View.GONE
                }
            }
        }

        private fun setItemBackGround(type: MessageViewItemType){
            val isBottom = when (type){
                MessageViewItemType.MY_BOTTOM_MESSAGE,
                MessageViewItemType.MY_ATTACHMENT_BOTTOM_MESSAGE -> true
                else -> false
            }
            ltMessageContainer.background = if (!isBottom) myMessageDrawble
            else myMessageBottomDrawble
            setMargins(isBottom, ltMessageContainer)

        }
    }

    inner class MyAttachmentMessageViewHolder(view: View): MyMessageViewHolder(view), IAttachment{
        private val ivPayload = view.findViewById<ImageView>(R.id.iv_payload_message_item)
        override fun bindView(item: MessageListViewItem) {
            super.bindView(item)
            bindAttachment(ivPayload, item)
        }

        override fun bindListenerToItem(
            item: MessageListViewItem,
            clickListener: (v: View, messageItem: MessageItem) -> Unit
        ) {
            super.bindListenerToItem(item, clickListener)
            val message = item.content as MessageItem
            ivPayload.setOnClickListener { clickListener.invoke(it, message) }
        }
        override fun bindAttachment(imageView: ImageView, item: MessageListViewItem) {
            val message = item.content as MessageItem
            when (message.message.messageDto.payloadType){
                MessageDto.PayloadType.FILE -> bindFileAttachment(imageView, message)
                MessageDto.PayloadType.IMAGE -> bindImageAttachment(imageView, message)
//                MessageDto.PayloadType.THUMB, // TODO
//                MessageDto.PayloadType.DOCUMENT, // TODO
                MessageDto.PayloadType.URL, // TODO
                MessageDto.PayloadType.NONE -> imageView.visibility = View.GONE
            }
        }
    }

    open inner class TheirMessageViewHolder(view: View) : AbstractMessageListItemsViewHolder(view),
        ItemSwipeble {
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
        private val ivEdited = view.findViewById<ImageView>(R.id.iv_message_edited)
        private val layoutReply = view.findViewById<ConstraintLayout>(R.id.layout_message_reply)
        private val tvReplyText = view.findViewById<TextView>(R.id.tv_message_action_text)
        private val tvReplyFrom = view.findViewById<TextView>(R.id.tv_message_action_description)
        private val ltMessageContainer = view.findViewById<ConstraintLayout>(R.id.lt_message_container)

        override fun bindView(item: MessageListViewItem) {
            bindBaseParams(item)
        }

        override fun bindListenerToItem(
            item: MessageListViewItem,
            clickListener: (v: View, messageItem: MessageItem) -> Unit
        ) {
            val message = item.content as MessageItem
            v.setOnClickListener { clickListener.invoke(it, message) }
            tvReplyFrom.setOnClickListener { clickListener.invoke(it, message) }
        }

        private fun bindBaseParams(item: MessageListViewItem){
            setItemBackGround(item.type)
            val message = item.content as MessageItem
            if (message.message.messageDto.isCorrected) {
                ivEdited.visibility = View.VISIBLE
                tvData.text = message.message.body
            }
            else {
                ivEdited.visibility = View.GONE
                if (message.message.body.isEmpty()) tvData.visibility = View.GONE //???? INVISIBLE
                else {
                    tvData.visibility = View.VISIBLE
                    tvData.text = message.message.body
                }
            }
            tvTime.text = message.message.messageDto.timeStamp.toDate().format(Template.TIME)
            if (message.message.messageDto.isReplyed) {
                layoutReply.visibility = View.VISIBLE
                tvReplyText.text = message.message.replyedMessage!!.body
                tvReplyFrom.text = message.message.replyedMessage.getContactName()
            } else layoutReply.visibility = View.GONE
        }

        private fun setItemBackGround(type: MessageViewItemType){
            val isBottom = when (type){
                MessageViewItemType.THEIR_BOTTOM_MESSAGE,
                MessageViewItemType.THEIR_ATTACHMENT_BOTTOM_MESSAGE -> true
                else -> false
            }
            ltMessageContainer.background = if (!isBottom) theirMessageDrawble
            else theirMessageBottomDrawble
            setMargins(isBottom, ltMessageContainer)
        }

    }

    inner class TheirAttachmentMessageViewHolder(view: View): TheirMessageViewHolder(view), IAttachment{
        private val ivPayload = view.findViewById<ImageView>(R.id.iv_payload_message_item)

        override fun bindView(item: MessageListViewItem) {
            super.bindView(item)
            bindAttachment(ivPayload, item)
        }

        override fun bindListenerToItem(
            item: MessageListViewItem,
            clickListener: (v: View, messageItem: MessageItem) -> Unit
        ) {
            super.bindListenerToItem(item, clickListener)
            val message = item.content as MessageItem
            ivPayload.setOnClickListener { clickListener.invoke(it, message) }
        }

        override fun bindAttachment(imageView: ImageView, item: MessageListViewItem) {
            val message = item.content as MessageItem
            when (message.message.messageDto.payloadType){
                MessageDto.PayloadType.FILE -> bindFileAttachment(imageView, message)
                MessageDto.PayloadType.IMAGE -> bindImageAttachment(imageView, message)
//                MessageDto.PayloadType.THUMB, // TODO
//                MessageDto.PayloadType.DOCUMENT, // TODO
                MessageDto.PayloadType.URL, // TODO
                MessageDto.PayloadType.NONE -> imageView.visibility = View.GONE
            }
        }

    }

    open inner class TheirMucMessageViewHolder(view: View) :
        AbstractMessageListItemsViewHolder(view), ItemSwipeble {
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
        private val tvFrom = view. findViewById<TextView>(R.id.tv_muc_chat_name)
        private val ivEdited = view.findViewById<ImageView>(R.id.iv_message_edited)
        private val avatarView = view.findViewById<ImageView>(R.id.messages_avatar)
        private val layoutReply = view.findViewById<ConstraintLayout>(R.id.layout_message_reply)
        private val tvReplyText = view.findViewById<TextView>(R.id.tv_message_action_text)
        private val tvReplyFrom = view.findViewById<TextView>(R.id.tv_message_action_description)
        private val ltMessageContainer = view.findViewById<ConstraintLayout>(R.id.lt_message_container)

        override fun bindView(item: MessageListViewItem) {
            bindBaseParams(item)
        }

        override fun bindListenerToItem(
            item: MessageListViewItem,
            clickListener: (v: View, messageItem: MessageItem) -> Unit
        ) {
            val message = item.content as MessageItem
            v.setOnClickListener { clickListener.invoke(it, message) }
            tvReplyFrom.setOnClickListener { clickListener.invoke(it, message) }
            avatarView.setOnClickListener { clickListener.invoke(it, message) }
        }

        private fun bindBaseParams(item: MessageListViewItem){
            setItemBackGroundAndAvatar(item)
            val message = item.content as MessageItem
            if (message.message.messageDto.isCorrected) {
                ivEdited.visibility = View.VISIBLE
                tvData.text = message.message.body
            }
            else {
                ivEdited.visibility = View.GONE
                if (message.message.body.isEmpty()) tvData.visibility = View.GONE //???? INVISIBLE
                else {
                    tvData.visibility = View.VISIBLE
                    tvData.text = message.message.body
                }
            }
            tvTime.text = message.message.messageDto.timeStamp.toDate().format(Template.TIME)
            tvFrom.text = message.message.getContactName()
            val colorGen = ColorHelper.MATERIAL
            val color = colorGen.getColor(message.message.getContactName())
            tvFrom.setTextColor(color)

            if (message.message.messageDto.isReplyed) {
                layoutReply.visibility = View.VISIBLE
                tvReplyText.text = message.message.replyedMessage!!.body
                tvReplyFrom.text = message.message.replyedMessage.getContactName()
            } else layoutReply.visibility = View.GONE
        }

        private fun setItemBackGroundAndAvatar(item: MessageListViewItem){
            val type = item.type
            val isBottom = when (type){
                MessageViewItemType.THEIR_MUC_BOTTOM_MESSAGE,
                MessageViewItemType.THEIR_MUC_ATTACHMENT_BOTTOM_MESSAGE -> true
                else -> false
            }
            val message = item.content as MessageItem
            if (!isBottom) {
                tvFrom.visibility = View.VISIBLE
                avatarView.visibility = View.VISIBLE
                avatarView.isEnabled = true
                ltMessageContainer.background = theirMessageDrawble
                AvatarHelper.placeRoundAvatar(
                    avatarView, message.message.fromContactDto?.avatar,
                    message.message.fromContactDto?.getShortName(), message.message.getContactName()
                )
            }
            else {
                tvFrom.visibility = View.GONE
                avatarView.visibility = View.INVISIBLE
                avatarView.isEnabled = false
                ltMessageContainer.background = theirMessageBottomDrawble
            }
            setMargins(isBottom, ltMessageContainer)
        }
    }

    inner class TheirMucAttachmentMessageViewHolder(view: View): TheirMucMessageViewHolder(view), IAttachment{
        private val ivPayload = view.findViewById<ImageView>(R.id.iv_payload_message_item)

        override fun bindView(item: MessageListViewItem) {
            super.bindView(item)
            bindAttachment(ivPayload, item)
        }

        override fun bindListenerToItem(
            item: MessageListViewItem,
            clickListener: (v: View, messageItem: MessageItem) -> Unit
        ) {
            super.bindListenerToItem(item, clickListener)
            val message = item.content as MessageItem
            ivPayload.setOnClickListener { clickListener.invoke(it, message) }
        }

        override fun bindAttachment(imageView: ImageView, item: MessageListViewItem) {
            val message = item.content as MessageItem
            when (message.message.messageDto.payloadType){
                MessageDto.PayloadType.FILE -> bindFileAttachment(imageView, message)
                MessageDto.PayloadType.IMAGE -> bindImageAttachment(imageView, message)
//                MessageDto.PayloadType.THUMB, // TODO
//                MessageDto.PayloadType.DOCUMENT, // TODO
                MessageDto.PayloadType.URL, // TODO
                MessageDto.PayloadType.NONE -> imageView.visibility = View.GONE
            }
        }

    }

    inner class DateHeaderViewHolder(view: View): AbstractMessageListItemsViewHolder(view) {
        private val tvHeader = view.findViewById<TextView>(R.id.tv_messages_header)
        override fun bindView(item: MessageListViewItem) {
            val header = item.content as DateHeader
            val date = header.date
            when {
                date.isToday() -> tvHeader.text = "Сегодня"
                date.isYesterday() -> tvHeader.text = "Вчера"
                else -> tvHeader.text = date.format(Template.STRING_DAY_MONTH)
            }
        }

        override fun bindListenerToItem(
            item: MessageListViewItem,
            clickListener: (v: View, messageItem: MessageItem) -> Unit
        ) {
            //DO NOTHING
        }
    }

}