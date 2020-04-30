package ooo.emessi.messenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.view_item_model.contact.ContactPickViewItem
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.utils.humanizeDiffForLastActivity
import ooo.emessi.messenger.utils.toDate
import java.util.*

class ContactsPickAdapter (private val listener: (ContactPickViewItem) -> Unit): RecyclerView.Adapter<ContactsPickAdapter.ContactsViewHolder>(){
    var contacts = listOf<ContactPickViewItem>()

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

    fun updateContacts(_contacts: List<ContactPickViewItem>){

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

    inner class ContactsViewHolder(view: View): RecyclerView.ViewHolder(view) {

//        val tvJid = view.findViewById<TextView>(R.id.tv_contact_item_contact_name)
        val tvName = view.findViewById<TextView>(R.id.tv_contact_item_contact_name)
        val tvLastActivity = view.findViewById<TextView>(R.id.tv_contact_item_last_online)
        val avatarView = view.findViewById<ImageView>(R.id.iv_contact_avatar)
        val tvLeftChar = view.findViewById<TextView>(R.id.tv_contact_item_char)
//        val indicator = view.findViewById<View>(R.id.contact_online_indicator)
        val ivPicked = view.findViewById<ImageView>(R.id.iv_picked_contact)
        val v = view

        fun bind(contactPickViewItem: ContactPickViewItem, listener: (ContactPickViewItem) -> Unit) {
//            tvJid.text = contactW.contact.contactJid
            tvName.text = contactPickViewItem.contactViewItem.contactDto.name
            tvLastActivity.text = when {
                contactPickViewItem.contactViewItem.contactDto.isOnline -> "Онлайн"
                contactPickViewItem.contactViewItem.contactDto.lastActivity != null -> Date().humanizeDiffForLastActivity(
                    contactPickViewItem.contactViewItem.contactDto.lastActivity!!.toDate()
                )
                else -> "Еще не заходил"
            }

            if (contactPickViewItem.isSelected) ivPicked.visibility = ImageView.VISIBLE
            else ivPicked.visibility = ImageView.GONE
            if (contactPickViewItem.contactViewItem.hasLeftChar){
                tvLeftChar.visibility = View.VISIBLE
                tvLeftChar.text = contactPickViewItem.contactViewItem.contactDto.name.first().toUpperCase().toString()
            } else {
                tvLeftChar.visibility = View.GONE
            }
//            if (contactW.contact.isOnline) indicator.visibility = View.VISIBLE
//            else indicator.visibility = View.INVISIBLE
            v.setOnClickListener { listener.invoke(contactPickViewItem) }
            AvatarHelper.placeRoundAvatar(
                avatarView,
                contactPickViewItem.contactViewItem.contactDto.avatar,
                contactPickViewItem.contactViewItem.contactDto.getShortName(),
                contactPickViewItem.contactViewItem.contactDto.contactJid
            )

        }
    }
}