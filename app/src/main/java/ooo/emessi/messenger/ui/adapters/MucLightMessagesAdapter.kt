package ooo.emessi.messenger.ui.adapters

import android.util.Log
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
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.utils.*
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.utils.helpers.ColorHelper
import java.io.File
import java.util.*

//class MucLightMessagesAdapter(val listener:(BZMessage, Boolean) -> Unit) : RecyclerView.Adapter<MucLightMessagesAdapter.AbstarctMessageViewHolder>(){

//    companion object{
//        private const val HEADER = 0
//        private const val MY_MESSAGE = 1
//        private const val THEIR_MESSAGE = 2
//        private const val MY_FILE_MESSAGE = 3
//        private const val THEIR_FILE_MESSAGE = 4
//        private const val MY_IMAGE_MESSAGE = 5
//        private const val THEIR_IMAGE_MESSAGE = 6
//    }
//    var messagesW = mutableListOf<MessageWrapper>()
//    var messages = mutableListOf<BZMessage>()
//
//    override fun getItemViewType(position: Int): Int {
//        return when (messagesW[position].type) {
//            MessageType.THEIR_MESSAGE -> THEIR_MESSAGE
//            MessageType.MY_MESSAGE -> MY_MESSAGE
//            MessageType.HEADER -> HEADER
//            MessageType.MY_FILE_MESSAGE -> MY_FILE_MESSAGE
//            MessageType.THEIR_FILE_MESSAGE -> THEIR_FILE_MESSAGE
//            MessageType.MY_IMAGE_MESSAGE -> MY_IMAGE_MESSAGE
//            MessageType.THEIR_IMAGE_MESSAGE -> THEIR_IMAGE_MESSAGE
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstarctMessageViewHolder {
//        val li = LayoutInflater.from(parent.context)
//        return when (viewType){
//            MY_MESSAGE -> MyMessagesViewHolder(li.inflate(R.layout.my_message_item, parent, false))
//            THEIR_MESSAGE -> TheirMessagesViewHolder(li.inflate(R.layout.their_muc_message_item, parent, false))
//            MY_FILE_MESSAGE -> MyMessagesFileViewHolder(li.inflate(R.layout.my_file_message_item, parent, false))
//            THEIR_FILE_MESSAGE -> TheirMessagesFileViewHolder(li.inflate(R.layout.their_muc_file_message_item, parent, false))
//            MY_IMAGE_MESSAGE -> MyMessagesImageViewHolder(li.inflate(R.layout.my_image_message_item, parent, false))
//            THEIR_IMAGE_MESSAGE -> TheirMessagesImageViewHolder(li.inflate(R.layout.their_muc_image_message_item, parent, false))
//            else -> HeaderViewHolder((li.inflate(R.layout.item_date_header, parent, false)))
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return messagesW.size
//    }
//
//    override fun onBindViewHolder(holder: AbstarctMessageViewHolder, position: Int) {
//        val message = messagesW[position]
//        holder.bindView(message)
//        if (message.type != MessageType.HEADER) {
//            val messageU = message.content as BZMessage
//            var messageEditable = false
//            if (!messageU.isIncoming) {
//                val incomingMessages = messages.filter { !it.isIncoming }
//                if (!incomingMessages.isNullOrEmpty())
//                    messageEditable = messageU.id == incomingMessages.last().id
//            }
//
//            holder.bindListener(message, messageEditable, listener)
//        }
//    }
//
//    fun updateMessages(_messages: List<BZMessage>){
//
//        val _messagesW = calculateHeaders(_messages)
//
//
//        val diffCallback = object : DiffUtil.Callback(){
//            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return messagesW[oldItemPosition] == _messagesW[newItemPosition]
//            }
//
//            override fun getOldListSize(): Int {
//                return messagesW.size
//            }
//
//            override fun getNewListSize(): Int {
//                return _messagesW.size
//            }
//
//            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return messagesW[oldItemPosition].hashCode() == _messagesW[newItemPosition].hashCode()
//            }
//
//        }
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        messagesW = _messagesW
//        messages = _messages.toMutableList()
//        diffResult.dispatchUpdatesTo(this)
//
//    }
//
//    fun newMessage(it: BZMessage) {
////        val copy = messages.toMutableList()
////        copy.add(it)
////        updateMessages(copy)
//        if (messages.isNotEmpty()){
//            if (it.timeStamp.toDate().isSameDay(messages.last().timeStamp.toDate())) {
//                messagesW.add(calculateMessageType(it))
//            }
//        }
//    }
//
//    fun calculateHeaders(messages: List<BZMessage>): MutableList<MessageWrapper> {
//        Log.d("list", messages.size.toString())
//
//        val messagesWrapped = mutableListOf<MessageWrapper>()
//        // if messages empty return empty wrapper
//        if (messages.isNullOrEmpty()) {
//            messagesWrapped.add(
//                MessageWrapper(
//                    MessageType.HEADER,
//                    Header(System.currentTimeMillis().toDate())
//                )
//            )
//            return messagesWrapped
//        }
////        {
////            messagesWrapped.add(
////                MessageWrapper(
////                    MessageType.HEADER,
////                    Header(System.currentTimeMillis().toDate())
////                )
////            )
////
////        } else
//        // if only one or more message return wrapped
//            if (messages.size == 1) {
//            val message = messages.first()
//            messagesWrapped.add(
//                MessageWrapper(
//                    MessageType.HEADER,
//                    Header(message.timeStamp.toDate())
//                )
//            )
//            messagesWrapped.add(calculateMessageType(message))
//        } else {
//            val messageF = messages.first()
//            messagesWrapped.add(
//                MessageWrapper(
//                    MessageType.HEADER,
//                    Header(messageF.timeStamp.toDate())
//                )
//            )
//            messagesWrapped.add(calculateMessageType(messageF))
//            for (i in 1 until messages.size){
//                val current = messages[i]
//                val previous = messages[i-1]
//                if (!current.timeStamp.toDate().isSameDay(previous.timeStamp.toDate())){
//                    messagesWrapped.add(MessageWrapper(
//                        MessageType.HEADER,
//                        Header(current.timeStamp.toDate())
//                    ))
//                    messagesWrapped.add(calculateMessageType(current))
//                } else messagesWrapped.add(calculateMessageType(current))
//            }
//        }
//        return messagesWrapped
//    }
//
//    private fun calculateMessageType(message: BZMessage): MessageWrapper {
//        var messageWrapper: MessageWrapper? = null
//        if (message.isIncoming) {
//            when(message.payloadType){
//                BZMessage.PayloadType.NONE -> messageWrapper = MessageWrapper(MessageType.THEIR_MESSAGE, message)
//                BZMessage.PayloadType.FILE -> messageWrapper = MessageWrapper(MessageType.THEIR_FILE_MESSAGE, message)
//                BZMessage.PayloadType.IMAGE, BZMessage.PayloadType.THUMB -> messageWrapper = MessageWrapper(MessageType.THEIR_IMAGE_MESSAGE, message)
//            }
//        } else {
//            when(message.payloadType){
//                BZMessage.PayloadType.NONE -> messageWrapper = MessageWrapper(MessageType.MY_MESSAGE, message)
//                BZMessage.PayloadType.FILE -> messageWrapper = MessageWrapper(MessageType.MY_FILE_MESSAGE, message)
//                BZMessage.PayloadType.IMAGE, BZMessage.PayloadType.THUMB -> messageWrapper = MessageWrapper(MessageType.MY_IMAGE_MESSAGE, message)
//            }
//        }
//
//        return messageWrapper!!
//    }
//
//    inner class MessageWrapper (val type: MessageType, val content: Any?)
//
//    enum class MessageType{
//        MY_MESSAGE,
//        THEIR_MESSAGE,
//        HEADER,
//        MY_FILE_MESSAGE,
//        THEIR_FILE_MESSAGE,
//        MY_IMAGE_MESSAGE,
//        THEIR_IMAGE_MESSAGE,
////        MY_IMAGE2_MESSAGE,
////        THEIR_IMAGE2_MESSAGE,
////        MY_IMAGE3_MESSAGE,
////        THEIR_IMAGE3_MESSAGE,
////        MY_IMAGE4_MESSAGE,
////        THEIR_IMAGE4_MESSAGE,
////        MY_IMAGE5_MESSAGE,
////        THEIR_IMAGE5_MESSAGE,
//
//    }
//
//    inner class Header(val date: Date)
//
//    abstract inner class AbstarctMessageViewHolder(view: View): RecyclerView.ViewHolder(view){
//        val v = view
//        abstract fun bindView(wrapper: MessageWrapper)
//        fun bindListener(messageWrapper: MessageWrapper, isEditable: Boolean, listener: (BZMessage, Boolean) -> Unit) {
//            val message = messageWrapper.content as BZMessage
//            v.setOnClickListener { listener.invoke(message, isEditable) }
//        }
//    }
//
//    inner class HeaderViewHolder(view: View): AbstarctMessageViewHolder(view) {
//        private val tv_header = view.findViewById<TextView>(R.id.tv_messages_header)
//        override fun bindView(wrapper: MessageWrapper) {
//            val header = wrapper.content as Header
//            val date = header.date
//            when {
//                System.currentTimeMillis() - date.time < 24*60*60*1000 -> tv_header.text = "Сегодня"
//                System.currentTimeMillis() - date.time < 48*60*60*1000 -> tv_header.text = "Вчера"
//                else -> tv_header.text = date.format(Template.STRING_DAY_MONTH)
//            }
//        }
//    }
//
//    inner class TheirMessagesViewHolder(view: View) : AbstarctMessageViewHolder(view) {
//        private val tvData = view.findViewById<TextView>(R.id.message_body)
//        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
//        private val tvFrom = view. findViewById<TextView>(R.id.tv_muc_chat_name)
//        private val avatarView = view.findViewById<ImageView>(R.id.messages_avatar)
//        private val layoutReply = view.findViewById<ConstraintLayout>(R.id.layout_message_reply)
//        private val tvReplyText = view.findViewById<TextView>(R.id.tv_message_action_text)
//        private val tvReplyFrom = view.findViewById<TextView>(R.id.tv_message_action_description)
//        override fun bindView(wrapper: MessageWrapper) {
//            val message = wrapper.content as BZMessage? ?: return
//            tvData.text = message.body
//            tvTime.text = message.timeStamp.toDate().format(Template.TIME)
//            tvFrom.text = message.getContactName()
//            val colorGen = ColorHelper.MATERIAL
//            val color = colorGen.getColor(message.from)
//            tvFrom.setTextColor(color)
//            AvatarHelper.placeRoundAvatar(avatarView, message.fromContact?.avatar, message.getShortName(), message.from)
//            if (message.isReplyed) {
//                layoutReply.visibility = View.VISIBLE
//                tvReplyText.text = message.messageReplyedId!!.body
//                tvReplyFrom.text = message.messageReplyedId!!.from
//            } else layoutReply.visibility = View.GONE
//        }
//    }
//
//    inner class MyMessagesViewHolder(view: View) : AbstarctMessageViewHolder(view){
//        private val tvData = view.findViewById<TextView>(R.id.message_body)
//        private val tvTime = view.findViewById<TextView>(R.id.tv_my_time)
//        private val ivSended = view.findViewById<ImageView>(R.id.iv_message_sended)
//        private val ivDelivered = view.findViewById<ImageView>(R.id.iv_message_delivered)
//        private val ivEdited = view.findViewById<ImageView>(R.id.iv_message_edited)
//        private val layoutReply = view.findViewById<ConstraintLayout>(R.id.layout_message_reply)
//        private val tvReplyText = view.findViewById<TextView>(R.id.tv_message_action_text)
//        private val tvReplyFrom = view.findViewById<TextView>(R.id.tv_message_action_description)
//        override fun bindView(wrapper: MessageWrapper){
//            val message = wrapper.content as BZMessage
//            tvData.text = message.body
//            tvTime.text = message.timeStamp.toDate().format(Template.TIME)
//            if (!message.isIncoming){
//                if (message.isCorrected) ivEdited.visibility = View.VISIBLE
//                else ivEdited.visibility = View.GONE
//                if (message.isReplyed) {
//                    layoutReply.visibility = View.VISIBLE
//                    tvReplyText.text = message.messageReplyedId!!.body
//                    tvReplyFrom.text = message.messageReplyedId!!.from
//                } else layoutReply.visibility = View.GONE
//                if (message.isSended){
//                    ivSended.visibility = View.VISIBLE
//                    if (message.isDelivered) ivDelivered.visibility = View.VISIBLE
//                    else ivDelivered.visibility = View.GONE
//
//                } else {
//                    ivSended.visibility = View.INVISIBLE
//                    ivDelivered.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//    inner class MyMessagesFileViewHolder(view: View): AbstarctMessageViewHolder(view){
//        private val tvData = view.findViewById<TextView>(R.id.message_body)
//        private val tvTime = view.findViewById<TextView>(R.id.tv_my_time)
//        private val ivSended = view.findViewById<ImageView>(R.id.iv_message_sended)
//        private val ivDelivered = view.findViewById<ImageView>(R.id.iv_message_delivered)
//        override fun bindView(wrapper: MessageWrapper) {
//            val message = wrapper.content as BZMessage
//            if (message.payload.isNotEmpty()){
//                val payload = File(message.payload.first())
//                tvData.text = payload.name
//            }
//            tvTime.text = message.timeStamp.toDate().format(Template.TIME)
//            if (!message.isIncoming){
//                if (message.isSended){
//                    ivSended.visibility = View.VISIBLE
//                    if (message.isDelivered) ivDelivered.visibility = View.VISIBLE
//                    else ivDelivered.visibility = View.GONE
//
//                } else {
//                    ivSended.visibility = View.INVISIBLE
//                    ivDelivered.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//    inner class TheirMessagesFileViewHolder(view: View): AbstarctMessageViewHolder(view){
//        private val tvData = view.findViewById<TextView>(R.id.message_body)
//        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
//        private val tvFrom = view. findViewById<TextView>(R.id.tv_muc_chat_name)
//        private val avatarView = view.findViewById<ImageView>(R.id.messages_avatar)
//        override fun bindView(wrapper: MessageWrapper) {
//            val message = wrapper.content as BZMessage
//            if (message.payload.isNotEmpty()){
//                val payload = File(message.payload.first())
//                tvData.text = payload.name
//            }
//            tvTime.text = message.timeStamp.toDate().format(Template.TIME)
//            tvFrom.text = message.getContactName()
//            val colorGen = ColorHelper.MATERIAL
//            val color = colorGen.getColor(message.from)
//            tvFrom.setTextColor(color)
//            AvatarHelper.placeRoundAvatar(avatarView, message.fromContact?.avatar, message.getShortName(), message.from)
//        }
//    }
//
//    inner class MyMessagesImageViewHolder(view: View): AbstarctMessageViewHolder(view){
//        private val tvData = view.findViewById<TextView>(R.id.message_body)
//        private val tvTime = view.findViewById<TextView>(R.id.tv_my_time)
//        private val ivSended = view.findViewById<ImageView>(R.id.iv_message_sended)
//        private val ivDelivered = view.findViewById<ImageView>(R.id.iv_message_delivered)
//        private val ivPayload = view.findViewById<ImageView>(R.id.iv_payload_message_item)
//
//        override fun bindView(wrapper: MessageWrapper) {
//            val message = wrapper.content as BZMessage
//
//            if (message.payload.isNotEmpty()){
//                tvData.text = File(message.payload.first()).name
//                Glide.with(v.context).load(message.payload.first())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(ivPayload)
//            }
//            tvTime.text = message.timeStamp.toDate().format(Template.TIME)
//            if (!message.isIncoming){
//                if (message.isSended){
//                    ivSended.visibility = View.VISIBLE
//                    if (message.isDelivered) ivDelivered.visibility = View.VISIBLE
//                    else ivDelivered.visibility = View.GONE
//
//                } else {
//                    ivSended.visibility = View.INVISIBLE
//                    ivDelivered.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//    inner class TheirMessagesImageViewHolder(view: View): AbstarctMessageViewHolder(view){
//        private val tvData = view.findViewById<TextView>(R.id.message_body)
//        private val tvTime = view.findViewById<TextView>(R.id.tv_their_time)
//        private val ivPayload = view.findViewById<ImageView>(R.id.iv_payload_message_item)
//        private val tvFrom = view. findViewById<TextView>(R.id.tv_muc_chat_name)
//        private val avatarView = view.findViewById<ImageView>(R.id.messages_avatar)
//
//        override fun bindView(wrapper: MessageWrapper) {
//            val message = wrapper.content as BZMessage
//            if (message.payload.isNotEmpty()){
//                tvData.text = File(message.payload.first()).name
//                Glide.with(v.context).load(message.payload.first())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(ivPayload)
//            }
//            tvTime.text = message.timeStamp.toDate().format(Template.TIME)
//            tvFrom.text = message.getContactName()
//            val colorGen = ColorHelper.MATERIAL
//            val color = colorGen.getColor(message.from)
//            tvFrom.setTextColor(color)
//            AvatarHelper.placeRoundAvatar(avatarView, message.fromContact?.avatar, message.getShortName(), message.from)
//        }
//    }
//}