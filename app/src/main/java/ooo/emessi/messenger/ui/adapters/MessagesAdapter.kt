package ooo.emessi.messenger.ui.adapters

import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.attachment.ImageAttachment
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.model.wrapped_model.MessageItem
import ooo.emessi.messenger.utils.*
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.utils.helpers.ColorHelper
import java.util.Date
import android.util.Base64

class MessagesAdapter(val listener:(MessageItem, Boolean) -> Unit) : RecyclerView.Adapter<MessagesAdapter.AbstarctMessageViewHolder>(){

    companion object{
        private const val HEADER = 0
        private const val MY_MESSAGE = 1
        private const val THEIR_MESSAGE = 2
        private const val MY_FILE_MESSAGE = 3
        private const val THEIR_FILE_MESSAGE = 4
        private const val MY_IMAGE_MESSAGE = 5
        private const val THEIR_IMAGE_MESSAGE = 6
        private const val THEIR_MUC_MESSAGE = 7
        private const val THEIR_MUC_FILE_MESSAGE = 8
        private const val THEIR_MUC_IMAGE_MESSAGE = 9

    }
    var messagesW = mutableListOf<MessageWrapper>()
    var messages = mutableListOf<MessageItem>()

    override fun getItemViewType(position: Int): Int {
        return when (messagesW[position].type) {
            MessageType.THEIR_MESSAGE -> THEIR_MESSAGE
            MessageType.MY_MESSAGE -> MY_MESSAGE
            MessageType.HEADER -> HEADER
            MessageType.MY_FILE_MESSAGE -> MY_FILE_MESSAGE
            MessageType.THEIR_FILE_MESSAGE -> THEIR_FILE_MESSAGE
            MessageType.MY_IMAGE_MESSAGE -> MY_IMAGE_MESSAGE
            MessageType.THEIR_IMAGE_MESSAGE -> THEIR_IMAGE_MESSAGE
            MessageType.THEIR_MUC_MESSAGE -> THEIR_MUC_MESSAGE
            MessageType.THEIR_MUC_FILE_MESSAGE -> THEIR_MUC_FILE_MESSAGE
            MessageType.THEIR_MUC_IMAGE_MESSAGE -> THEIR_MUC_IMAGE_MESSAGE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstarctMessageViewHolder {
        val li = LayoutInflater.from(parent.context)
        return when (viewType){
            MY_MESSAGE -> MyMessagesViewHolder(li.inflate(R.layout.my_message_item, parent, false))
            THEIR_MESSAGE -> TheirMessagesViewHolder(li.inflate(R.layout.their_message_item, parent, false))
            MY_FILE_MESSAGE -> MyMessagesFileViewHolder(li.inflate(R.layout.my_file_message_item, parent, false))
            THEIR_FILE_MESSAGE -> TheirMessagesFileViewHolder(li.inflate(R.layout.their_file_message_item, parent, false))
            MY_IMAGE_MESSAGE -> MyMessagesImageViewHolder(li.inflate(R.layout.my_image_message_item, parent, false))
            THEIR_IMAGE_MESSAGE -> TheirMessagesImageViewHolder(li.inflate(R.layout.their_image_message_item, parent, false))
            THEIR_MUC_MESSAGE -> TheirMucMessagesViewHolder(li.inflate(R.layout.their_muc_message_item, parent, false))
            THEIR_MUC_FILE_MESSAGE -> TheirMucMessagesFileViewHolder(li.inflate(R.layout.their_muc_file_message_item, parent, false))
            THEIR_MUC_IMAGE_MESSAGE -> TheirMucMessagesImageViewHolder(li.inflate(R.layout.their_muc_image_message_item, parent, false))
            else -> HeaderViewHolder((li.inflate(R.layout.item_date_header, parent, false)))
        }
    }

    override fun getItemCount(): Int {
        return messagesW.size
    }

    override fun onBindViewHolder(holder: AbstarctMessageViewHolder, position: Int) {
        val message = messagesW[position]
        holder.bindView(message)
        if (message.type != MessageType.HEADER) {
            val messageU = message.content as MessageItem
            Log.i(this.javaClass.simpleName, messageU.message.toString())
            Log.i(this.javaClass.simpleName, messageU.payload.toString())
            var messageEditable = false
            if (!messageU.message.isIncoming) {
                val incomingMessages = messages.filter { !it.message.isIncoming }
                if (!incomingMessages.isNullOrEmpty())
                messageEditable = messageU.message.id == incomingMessages.last().message.id
            }

            holder.bindListener(message, messageEditable, listener)
        }

    }

    fun updateMessages(_messages: List<MessageItem>){

        val _messagesW = calculateHeaders(_messages)


        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return messagesW[oldItemPosition] == _messagesW[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return messagesW.size
            }

            override fun getNewListSize(): Int {
                return _messagesW.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return messagesW[oldItemPosition].hashCode() == _messagesW[newItemPosition].hashCode()
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int) = Any()

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messagesW = _messagesW
        messages = _messages.toMutableList()
        diffResult.dispatchUpdatesTo(this)

    }

//    fun newMessage(it: MessageItem) {
////        val copy = messages.toMutableList()
////        copy.add(it)
////        updateMessages(copy)
//        if (messages.isNotEmpty()){
//            if (it.message.timeStamp.toDate().isSameDay(messages.last().message.timeStamp.toDate())) {
//                messagesW.add(calculateMessageType(it))
//            }
//        }
//    }

    fun calculateHeaders(messages: List<MessageItem>): MutableList<MessageWrapper> {

        val messagesWrapped = mutableListOf<MessageWrapper>()
        // if messages empty return empty wrapper
        if (messages.isNullOrEmpty()) {
            messagesWrapped.add(
                MessageWrapper(
                    MessageType.HEADER,
                    Header(System.currentTimeMillis().toDate())
                )
            )
            return messagesWrapped
        }
        // if only one or more message return wrapped

        if (messages.size == 1) {
            val message = messages.first()
            messagesWrapped.add(
                MessageWrapper(
                    MessageType.HEADER,
                    Header(message.message.timeStamp.toDate())
                )
            )
            messagesWrapped.add(calculateMessageType(message))
        } else {
            val messageF = messages.first()
            messagesWrapped.add(
                MessageWrapper(
                    MessageType.HEADER,
                    Header(messageF.message.timeStamp.toDate())
                )
            )
            messagesWrapped.add(calculateMessageType(messageF))
            for (i in 1 until messages.size){
                val current = messages[i]
                val previous = messages[i-1]
                if (current.message.timeStamp - previous.message.timeStamp > 24*60*60*1000){//!current.timeStamp.toDate().isSameDay(previous.timeStamp.toDate())
                    messagesWrapped.add(MessageWrapper(
                        MessageType.HEADER,
                        Header(current.message.timeStamp.toDate())
                    ))
                    messagesWrapped.add(calculateMessageType(current))
                } else messagesWrapped.add(calculateMessageType(current))
            }
        }
        return messagesWrapped
    }

    private fun calculateMessageType(message: MessageItem): MessageWrapper {
        var messageWrapper: MessageWrapper? = null
        if (message.message.isIncoming) {
            if (message.message.chatJid.contains("@muclight")){
                when(message.message.payloadType){
                    BZMessage.PayloadType.NONE -> messageWrapper = MessageWrapper(MessageType.THEIR_MUC_MESSAGE, message)
                    BZMessage.PayloadType.FILE -> messageWrapper = MessageWrapper(MessageType.THEIR_MUC_FILE_MESSAGE, message)
                    BZMessage.PayloadType.IMAGE, BZMessage.PayloadType.THUMB -> messageWrapper = MessageWrapper(MessageType.THEIR_MUC_IMAGE_MESSAGE, message)
                }
            } else {
                when(message.message.payloadType){
                    BZMessage.PayloadType.NONE -> messageWrapper = MessageWrapper(MessageType.THEIR_MESSAGE, message)
                    BZMessage.PayloadType.FILE -> messageWrapper = MessageWrapper(MessageType.THEIR_FILE_MESSAGE, message)
                    BZMessage.PayloadType.IMAGE, BZMessage.PayloadType.THUMB -> messageWrapper = MessageWrapper(MessageType.THEIR_IMAGE_MESSAGE, message)
                }
            }

        } else {
            when(message.message.payloadType){
                BZMessage.PayloadType.NONE -> messageWrapper = MessageWrapper(MessageType.MY_MESSAGE, message)
                BZMessage.PayloadType.FILE -> messageWrapper = MessageWrapper(MessageType.MY_FILE_MESSAGE, message)
                BZMessage.PayloadType.IMAGE, BZMessage.PayloadType.THUMB -> messageWrapper = MessageWrapper(MessageType.MY_IMAGE_MESSAGE, message)
            }
        }

        return messageWrapper!!
    }

    inner class MessageWrapper (val type: MessageType, val content: Any)

    enum class MessageType{
        MY_MESSAGE,
        THEIR_MESSAGE,
        HEADER,
        MY_FILE_MESSAGE,
        THEIR_FILE_MESSAGE,
        MY_IMAGE_MESSAGE,
        THEIR_IMAGE_MESSAGE,
        THEIR_MUC_MESSAGE,
        THEIR_MUC_FILE_MESSAGE,
        THEIR_MUC_IMAGE_MESSAGE
//        MY_IMAGE2_MESSAGE,
//        THEIR_IMAGE2_MESSAGE,
//        MY_IMAGE3_MESSAGE,
//        THEIR_IMAGE3_MESSAGE,
//        MY_IMAGE4_MESSAGE,
//        THEIR_IMAGE4_MESSAGE,
//        MY_IMAGE5_MESSAGE,
//        THEIR_IMAGE5_MESSAGE,

    }

    inner class Header(val date: Date)

    abstract inner class AbstarctMessageViewHolder(view: View): RecyclerView.ViewHolder(view){
        val v = view
        abstract fun bindView(wrapper: MessageWrapper)
        fun bindListener(messageWrapper: MessageWrapper, isEditable: Boolean, listener: (MessageItem, Boolean) -> Unit) {

            val message = messageWrapper.content as MessageItem
            v.setOnClickListener { listener.invoke(message, isEditable) }

        }
    }

    inner class HeaderViewHolder(view: View): AbstarctMessageViewHolder(view) {
        private val tv_header = view.findViewById<TextView>(R.id.tv_messages_header)
        override fun bindView(wrapper: MessageWrapper) {
            val header = wrapper.content as Header
            val date = header.date
            when {
                date.isToday() -> tv_header.text = "Сегодня"
                date.isYesterday() -> tv_header.text = "Вчера"
                else -> tv_header.text = date.format(Template.STRING_DAY_MONTH)
            }
        }
    }

    inner class TheirMessagesViewHolder(view: View) : AbstarctMessageViewHolder(view) {
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
        private val layoutReply = view.findViewById<ConstraintLayout>(R.id.layout_message_reply)
        private val tvReplyText = view.findViewById<TextView>(R.id.tv_message_action_text)
        private val tvReplyFrom = view.findViewById<TextView>(R.id.tv_message_action_description)
        override fun bindView(wrapper: MessageWrapper) {
            val message = wrapper.content as MessageItem
            tvData.text = message.message.body
            tvTime.text = message.message.timeStamp.toDate().format(Template.TIME)
            if (message.message.isReplyed) {
                layoutReply.visibility = View.VISIBLE
                tvReplyText.text = message.replyedMessage!!.body
                tvReplyFrom.text = message.replyedMessage.from
            } else layoutReply.visibility = View.GONE
        }
    }


    inner class MyMessagesViewHolder(view: View) : AbstarctMessageViewHolder(view){
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_my_time)
        private val ivSended = view.findViewById<ImageView>(R.id.iv_message_sended)
        private val ivDelivered = view.findViewById<ImageView>(R.id.iv_message_delivered)
        private val ivEdited = view.findViewById<ImageView>(R.id.iv_message_edited)
        private val ivWaiting = view.findViewById<ImageView>(R.id.iv_message_waiting)
        private val layoutReply = view.findViewById<ConstraintLayout>(R.id.layout_message_reply)
        private val tvReplyText = view.findViewById<TextView>(R.id.tv_message_action_text)
        private val tvReplyFrom = view.findViewById<TextView>(R.id.tv_message_action_description)
        override fun bindView(wrapper: MessageWrapper){
            val message = wrapper.content as MessageItem
            tvData.text = message.message.body
            tvTime.text = message.message.timeStamp.toDate().format(Template.TIME)
            if (!message.message.isIncoming){
                if (message.message.isCorrected) ivEdited.visibility = View.VISIBLE
                else ivEdited.visibility = View.GONE
                if (message.message.isReplyed) {
                    layoutReply.visibility = View.VISIBLE
                    tvReplyText.text = message.replyedMessage!!.body
                    tvReplyFrom.text = message.replyedMessage.from
                } else layoutReply.visibility = View.GONE
                if (message.message.isSended){
                    ivSended.visibility = View.VISIBLE
                    ivWaiting.visibility = View.GONE
                    if (message.message.isDelivered) ivDelivered.visibility = View.VISIBLE
                    else ivDelivered.visibility = View.GONE

                } else {
                    ivSended.visibility = View.INVISIBLE
                    ivWaiting.visibility = View.VISIBLE
                    ivDelivered.visibility = View.GONE
                }
            }
        }
    }

    inner class MyMessagesFileViewHolder(view: View): AbstarctMessageViewHolder(view){
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_my_time)
        private val ivSended = view.findViewById<ImageView>(R.id.iv_message_sended)
        private val ivDelivered = view.findViewById<ImageView>(R.id.iv_message_delivered)
        private val ivWaiting = view.findViewById<ImageView>(R.id.iv_message_waiting)
        override fun bindView(wrapper: MessageWrapper) {
            val message = wrapper.content as MessageItem
            if (message.payload.isNotEmpty()){
                tvData.text = message.payload.first().attachmentName
            }
            tvTime.text = message.message.timeStamp.toDate().format(Template.TIME)
            if (!message.message.isIncoming){
                if (message.message.isSended){
                    ivSended.visibility = View.VISIBLE
                    ivWaiting.visibility = View.GONE
                    if (message.message.isDelivered) ivDelivered.visibility = View.VISIBLE
                    else ivDelivered.visibility = View.GONE

                } else {
                    ivSended.visibility = View.INVISIBLE
                    ivDelivered.visibility = View.GONE
                    ivWaiting.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class TheirMessagesFileViewHolder(view: View): AbstarctMessageViewHolder(view){
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
        override fun bindView(wrapper: MessageWrapper) {
            val message = wrapper.content as MessageItem
            if (message.payload.isNotEmpty()){
                tvData.text = message.payload.first().attachmentName
            }
            tvTime.text = message.message.timeStamp.toDate().format(Template.TIME)
        }
    }

    inner class MyMessagesImageViewHolder(view: View): AbstarctMessageViewHolder(view){
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_my_time)
        private val ivSended = view.findViewById<ImageView>(R.id.iv_message_sended)
        private val ivDelivered = view.findViewById<ImageView>(R.id.iv_message_delivered)
        private val ivWaiting = view.findViewById<ImageView>(R.id.iv_message_waiting)
        private val ivPayload = view.findViewById<ImageView>(R.id.iv_payload_message_item)

        override fun bindView(wrapper: MessageWrapper) {
            val message = wrapper.content as MessageItem

            if (message.payload.isNotEmpty()){
                val payload = message.payload.first() as ImageAttachment
                tvData.text = payload.attachmentName
                val path = payload.attachmentPath
                d(this@MessagesAdapter.javaClass.simpleName, path)
//                if (path != null)
                Glide.with(v.context).load(path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPayload)
//                else Glide.with(v.context).load(payload.thumb!!.toByteArray())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(ivPayload)
            }
            tvTime.text = message.message.timeStamp.toDate().format(Template.TIME)
            if (!message.message.isIncoming){
                if (message.message.isSended){
                    ivSended.visibility = View.VISIBLE
                    ivWaiting.visibility = View.GONE
                    if (message.message.isDelivered) ivDelivered.visibility = View.VISIBLE
                    else ivDelivered.visibility = View.GONE

                } else {
                    ivSended.visibility = View.INVISIBLE
                    ivDelivered.visibility = View.GONE
                    ivWaiting.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class TheirMessagesImageViewHolder(view: View): AbstarctMessageViewHolder(view){
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
        private val ivPayload = view.findViewById<ImageView>(R.id.iv_payload_message_item)

        override fun bindView(wrapper: MessageWrapper) {
            val message = wrapper.content as MessageItem
            if (message.payload.isNotEmpty()){
                val payload = message.payload.first() as ImageAttachment
                tvData.text = payload.attachmentName
                val path = payload.attachmentPath
                d(this@MessagesAdapter.javaClass.simpleName, path?:"")
                if (path != null)
                    Glide.with(v.context).load(path)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivPayload)
                else Glide.with(v.context).load(Base64.decode(payload.thumb, Base64.DEFAULT))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPayload)
            }
            tvTime.text = message.message.timeStamp.toDate().format(Template.TIME)
        }
    }

    inner class TheirMucMessagesViewHolder(view: View) : AbstarctMessageViewHolder(view) {
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
        private val tvFrom = view. findViewById<TextView>(R.id.tv_muc_chat_name)
        private val avatarView = view.findViewById<ImageView>(R.id.messages_avatar)
        private val layoutReply = view.findViewById<ConstraintLayout>(R.id.layout_message_reply)
        private val tvReplyText = view.findViewById<TextView>(R.id.tv_message_action_text)
        private val tvReplyFrom = view.findViewById<TextView>(R.id.tv_message_action_description)
        override fun bindView(wrapper: MessageWrapper) {
            val message = wrapper.content as MessageItem
            tvData.text = message.message.body
            tvTime.text = message.message.timeStamp.toDate().format(Template.TIME)
            tvFrom.text = message.getContactName()
            val colorGen = ColorHelper.MATERIAL
            val color = colorGen.getColor(message.from)
            tvFrom.setTextColor(color)
            AvatarHelper.placeRoundAvatar(avatarView, message.from?.avatar, message.getShortName(), message.message.from)
            if (message.message.isReplyed) {
                layoutReply.visibility = View.VISIBLE
                tvReplyText.text = message.replyedMessage!!.body
                tvReplyFrom.text = message.replyedMessage.from
            } else layoutReply.visibility = View.GONE
        }
    }

    inner class TheirMucMessagesFileViewHolder(view: View): AbstarctMessageViewHolder(view){
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
        private val tvFrom = view. findViewById<TextView>(R.id.tv_muc_chat_name)
        private val avatarView = view.findViewById<ImageView>(R.id.messages_avatar)
        override fun bindView(wrapper: MessageWrapper) {
            val message = wrapper.content as MessageItem
            if (message.payload.isNotEmpty()){
                tvData.text = message.payload.first().attachmentName
            }
            tvTime.text = message.message.timeStamp.toDate().format(Template.TIME)
            tvFrom.text = message.getContactName()
            val colorGen = ColorHelper.MATERIAL
            val color = colorGen.getColor(message.from)
            tvFrom.setTextColor(color)
            AvatarHelper.placeRoundAvatar(avatarView, message.from?.avatar, message.getShortName(), message.message.from)
        }
    }

    inner class TheirMucMessagesImageViewHolder(view: View): AbstarctMessageViewHolder(view){
        private val tvData = view.findViewById<TextView>(R.id.message_body)
        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
        private val ivPayload = view.findViewById<ImageView>(R.id.iv_payload_message_item)
        private val tvFrom = view. findViewById<TextView>(R.id.tv_muc_chat_name)
        private val avatarView = view.findViewById<ImageView>(R.id.messages_avatar)

        override fun bindView(wrapper: MessageWrapper) {
            val message = wrapper.content as MessageItem
            if (message.payload.isNotEmpty()){
                val payload = message.payload.first() as ImageAttachment
                tvData.text = payload.attachmentName
                val path = payload.attachmentPath
                d(this@MessagesAdapter.javaClass.simpleName, path?: "")
                if (path != null)
                    Glide.with(v.context).load(path)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivPayload)
                else Glide.with(v.context).load(Base64.decode(payload.thumb, Base64.DEFAULT))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPayload)
            }
            tvTime.text = message.message.timeStamp.toDate().format(Template.TIME)
            tvFrom.text = message.getContactName()
            val colorGen = ColorHelper.MATERIAL
            val color = colorGen.getColor(message.from)
            tvFrom.setTextColor(color)
            AvatarHelper.placeRoundAvatar(avatarView, message.from?.avatar, message.getShortName(), message.message.from)
        }
    }
}