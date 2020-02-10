package ooo.emessi.messenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.wrapped_model.ContactPickItem
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.utils.humanizeDiffForLastActivity
import ooo.emessi.messenger.utils.toDate
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import java.util.*

class ContactsPickAdapter (private val listener: (ContactPickItem) -> Unit): RecyclerView.Adapter<ContactsPickAdapter.ContactsViewHolder>(){
    var contacts = listOf<ContactPickItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(contacts[position], listener)
    }

    fun updateContacts(items: List<ContactPickItem>){
        val _contacts = calculateLeftChar(items.filter { it.contact.contactJid != XMPPConnectionApi.getMyJid().asEntityBareJidIfPossible().toString() }.sortedBy { it.contact.nickName.capitalize() })
        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return contacts[oldItemPosition] == _contacts[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return contacts.size
            }

            override fun getNewListSize(): Int {
                return _contacts.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return contacts[oldItemPosition].hashCode() == _contacts[newItemPosition].hashCode()
            }

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        contacts = _contacts
        diffResult.dispatchUpdatesTo(this)
    }

    private fun calculateLeftChar(contactPicks: List<ContactPickItem>): List<ContactPickItem> {
        val contactsW = mutableListOf<ContactPickItem>()
        if (contactPicks.isNullOrEmpty()) return emptyList()
        if (contactPicks.size == 1) return listOf(contactPicks.first().copy(hasLeftChar = true))
        else contactsW.add(contactPicks.first().copy(hasLeftChar = true))
        for (i in 1 until contactPicks.size) {
            val current = contactPicks[i]
            val previous = contactPicks[i-1]
            if (current.contact.nickName.first().toUpperCase() != previous.contact.nickName.first().toUpperCase()) {
                contactsW.add(contactPicks[i].copy(hasLeftChar = true))
            } else {
                contactsW.add(contactPicks[i])
            }
        }
        return contactsW
    }

    inner class ContactsViewHolder(view: View): RecyclerView.ViewHolder(view) {

//        val tvJid = view.findViewById<TextView>(R.id.tv_contact_item_contact_name)
        val tvName = view.findViewById<TextView>(R.id.tv_contact_item_contact_name)
        val tvLastActivity = view.findViewById<TextView>(R.id.tv_contact_item_last_online)
        val avatarView = view.findViewById<ImageView>(R.id.iv_contact_avatar)
        val tvLeftChar = view.findViewById<TextView>(R.id.tv_contact_item_char)
//        val indicator = view.findViewById<View>(R.id.contact_online_indicator)
        val ivPicked = view.findViewById<ImageView>(R.id.iv_picked_contact)
        val v = view

        fun bind(contactPickW: ContactPickItem, listener: (ContactPickItem) -> Unit) {
//            tvJid.text = contactW.contact.contactJid
            tvName.text = contactPickW.contact.nickName
            tvLastActivity.text = when {
                contactPickW.contact.isOnline -> "Онлайн"
                contactPickW.contact.lastVisit != null -> Date().humanizeDiffForLastActivity(contactPickW.contact.lastVisit!!.toDate())
                else -> "Еще не заходил"
            }

            if (contactPickW.isSelected) ivPicked.visibility = ImageView.VISIBLE
            else ivPicked.visibility = ImageView.GONE
            if (contactPickW.hasLeftChar){
                tvLeftChar.visibility = View.VISIBLE
                tvLeftChar.text = contactPickW.contact.nickName.first().toUpperCase().toString()
            } else {
                tvLeftChar.visibility = View.GONE
            }
//            if (contactW.contact.isOnline) indicator.visibility = View.VISIBLE
//            else indicator.visibility = View.INVISIBLE
            v.setOnClickListener { listener.invoke(contactPickW) }
            AvatarHelper.placeRoundAvatar(avatarView, contactPickW.contact.avatar, contactPickW.contact.getShortName(), contactPickW.contact.contactJid)

        }
    }
}