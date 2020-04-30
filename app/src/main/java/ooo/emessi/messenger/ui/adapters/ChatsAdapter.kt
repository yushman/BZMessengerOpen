package ooo.emessi.messenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.muc_chat_item.view.*
import kotlinx.android.synthetic.main.single_chat_item.view.*
import kotlinx.android.synthetic.main.single_chat_item.view.iv_chat_avatar
import kotlinx.android.synthetic.main.single_chat_item.view.iv_message_delivered2
import kotlinx.android.synthetic.main.single_chat_item.view.iv_message_sended2
import kotlinx.android.synthetic.main.single_chat_item.view.tv_chat_item_last_date
import kotlinx.android.synthetic.main.single_chat_item.view.tv_chat_item_last_message
import kotlinx.android.synthetic.main.single_chat_item.view.tv_chat_item_unread_count
import kotlinx.android.synthetic.main.single_chat_item.view.tv_chat_item_user_name
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.model.view_item_model.chat.ChatViewItem
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.utils.humanizeDiff
import java.util.*

class ChatsAdapter(val listener: (ChatDto) -> Unit) :
    RecyclerView.Adapter<ChatsAdapter.AbstractChatsViewHolder>() {
    var chats = listOf<ChatViewItem>()

    private val SINGLE_CHAT = 0
    private val GROUP_CHAT = 1

    override fun getItemViewType(position: Int): Int {
        return when (chats[position].chatDto.isMulti) {
            true -> GROUP_CHAT
            false -> SINGLE_CHAT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractChatsViewHolder {
        val li = LayoutInflater.from(parent.context)
        return when(viewType){
            SINGLE_CHAT -> SingleChatsViewHolder(li.inflate(R.layout.single_chat_item, parent, false))
            else -> GroupChatsViewHolder(li.inflate(R.layout.muc_chat_item, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: AbstractChatsViewHolder, position: Int) {
        holder.bind(chats[position], listener)
    }

    fun updateChats(_chats: List<ChatViewItem>){
//        val _chats = _items.sortedBy { it.lastMessage?.timeStamp }
        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return chats[oldItemPosition] == _chats[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return chats.size
            }

            override fun getNewListSize(): Int {
                return _chats.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return chats[oldItemPosition].hashCode() == _chats[newItemPosition].hashCode()
            }

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        chats = _chats
        diffResult.dispatchUpdatesTo(this)
    }

    abstract class AbstractChatsViewHolder(view: View): RecyclerView.ViewHolder(view){
        abstract fun bind(chat: ChatViewItem, listener: (ChatDto) -> Unit)
    }

    inner class SingleChatsViewHolder(view: View): AbstractChatsViewHolder(view){

        val tvLastMessage = view.tv_chat_item_last_message
        val tvChatId = view.tv_chat_item_user_name
        val tvDate = view.tv_chat_item_last_date
        val tvUnreadCount = view.tv_chat_item_unread_count
        val avatarView = view.iv_chat_avatar
        val ivOnline = view.iv_chat_item_online_indicator
        val ivSended = view.iv_message_sended2
        val ivDelivered = view.iv_message_delivered2
        val v = itemView


        override fun bind(chat: ChatViewItem, listener: (ChatDto) -> Unit) {
            tvChatId.text = chat.chatDto.name
            ivSended.visibility = View.GONE
            ivDelivered.visibility = View.GONE
            if (chat.contactDto != null){
                if (chat.contactDto!!.isOnline) {
                    ivOnline.visibility = View.VISIBLE
                } else {
                    ivOnline.visibility = View.GONE
                }
            } else {
                ivOnline.visibility = View.GONE
            }

            val lastMessage = chat.lastMessageViewItem?.messageDto
            if (lastMessage != null){
                if (!lastMessage.isIncoming){
                    if (lastMessage.isSended) ivSended.visibility = View.VISIBLE
                    else ivSended.visibility = View.GONE
                    if (lastMessage.isDelivered) ivDelivered.visibility = View.VISIBLE
                    else ivDelivered.visibility = View.GONE
                } else {
                    ivSended.visibility = View.GONE
                    ivDelivered.visibility = View.GONE
                }
                var lastMessageBody = lastMessage.messageCorrectedBody ?: lastMessage.body
                if (lastMessageBody.isEmpty()) {
                    lastMessageBody = when (lastMessage.payloadType){
                        MessageDto.PayloadType.IMAGE -> "IMAGE"
                        MessageDto.PayloadType.FILE -> "FILE"
                        else -> ""
                    }
                }
                tvLastMessage.text = lastMessageBody
                tvDate.text = Date().humanizeDiff(Date(lastMessage.timeStamp))
            } else {
                tvLastMessage.text = "No messages"
                tvDate.text = ""
            }
            val unread = chat.chatDto.unreadMessages
            if (unread == 0) tvUnreadCount.visibility = View.GONE
            else {
                tvUnreadCount.visibility = View.VISIBLE
                tvUnreadCount.text = unread.toString()
            }
            v.setOnClickListener { listener.invoke(chat.chatDto) }
            AvatarHelper.placeRoundAvatar(
                avatarView,
                chat.contactDto?.avatar,
                chat.contactDto?.getShortName(),
                chat.chatDto.name
            )
        }
    }

    inner class GroupChatsViewHolder(view: View): AbstractChatsViewHolder(view){

        val tvLastMessage = view.tv_chat_item_last_message
        val tvChatId = view.tv_chat_item_user_name
        val tvDate = view.tv_chat_item_last_date
        val tvUnreadCount = view.tv_chat_item_unread_count
        val tvFrom = view.tv_chat_item_from
        val avatarView = view.iv_chat_avatar
        val ivMultiChat = view.iv_chat_item_multichat
        val ivSended = view.iv_message_sended2
        val ivDelivered = view.iv_message_delivered2
        val v = view


        override fun bind(chat: ChatViewItem, listener: (ChatDto) -> Unit) {
            tvChatId.text = chat.chatDto.name
            ivSended.visibility = View.GONE
            ivDelivered.visibility = View.GONE
            ivMultiChat.visibility = View.VISIBLE
            val lastMessage = chat.lastMessageViewItem
            if (lastMessage != null){
                tvFrom.visibility = View.VISIBLE

                    //Change on CgatItem Impl
                    if (lastMessage.messageDto.isIncoming) {
                        tvFrom.text = lastMessage.getContactName()
                        tvFrom.setTextColor(ColorGenerator.MATERIAL.getColor(lastMessage.getContactName()))
                    }
                    else {
                        tvFrom.text = "Вы"
                        tvFrom.setTextColor(v.resources.getColor(R.color.color_text_dk_gray))
                    }
                if (!lastMessage.messageDto.isIncoming){
                    if (lastMessage.messageDto.isSended) ivSended.visibility = View.VISIBLE
                    else ivSended.visibility = View.GONE
                    if (lastMessage.messageDto.isDelivered) ivDelivered.visibility = View.VISIBLE
                    else ivDelivered.visibility = View.GONE
                } else {
                    ivSended.visibility = View.GONE
                    ivDelivered.visibility = View.GONE
                }
                var lastMessageBody = lastMessage.body
                if (lastMessageBody.isEmpty()) {
                    lastMessageBody = when (lastMessage.messageDto.payloadType){
                        MessageDto.PayloadType.IMAGE -> "IMAGE"
                        MessageDto.PayloadType.FILE -> "FILE"
                        else -> ""
                    }
                }
                tvLastMessage.text = lastMessageBody
                tvDate.text = Date().humanizeDiff(Date(lastMessage.messageDto.timeStamp))
            } else {
                tvLastMessage.text = "No messages"
                tvDate.text = ""
            }
            val unread = chat.chatDto.unreadMessages
            if (unread == 0) tvUnreadCount.visibility = View.GONE
            else {
                tvUnreadCount.visibility = View.VISIBLE
                tvUnreadCount.text = unread.toString()
            }
            v.setOnClickListener { listener.invoke(chat.chatDto) }
            AvatarHelper.placeRoundAvatar(
                avatarView,
                chat.contactDto?.avatar,
                chat.contactDto?.getShortName(),
                chat.chatDto.jid
            )
        }
    }
}