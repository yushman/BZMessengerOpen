package ooo.emessi.messenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.utils.humanizeDiffForLastActivity
import ooo.emessi.messenger.utils.toDate
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import java.util.*

class ContactsAdapter (private val listener: (BZContact) -> Unit): RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>(){
    var contacts = listOf<BZContact>()
    var contactsW = listOf<ContactWrapper>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactsW.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(contactsW[position], listener)
    }

    fun updateContacts(items: List<BZContact>){
        val _contacts = calculateLeftChars(items.filter { it.contactJid != XMPPConnectionApi.getMyJid().asEntityBareJidIfPossible().toString() }.sortedBy { it.nickName.capitalize() })

        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return contactsW[oldItemPosition] == _contacts[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return contactsW.size
            }

            override fun getNewListSize(): Int {
                return _contacts.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return contactsW[oldItemPosition].hashCode() == _contacts[newItemPosition].hashCode()
            }

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        contactsW = _contacts
        diffResult.dispatchUpdatesTo(this)
    }

    private fun calculateLeftChars(contacts: List<BZContact>): List<ContactWrapper> {
        val contactsWrapped = mutableListOf<ContactWrapper>()

        if (contacts.isNullOrEmpty()) return listOf<ContactWrapper>()
        if (contacts.size == 1) return listOf<ContactWrapper>(ContactWrapper(true, contacts.first()))
        else contactsWrapped.add(ContactWrapper(true, contacts.first()))
        for (i in 1 until contacts.size){
            val current = contacts[i]
            val previous = contacts[i-1]
            if (current.nickName.first().toUpperCase() != previous.nickName.first().toUpperCase()) {
                contactsWrapped.add(ContactWrapper(true, current))
            } else {
                contactsWrapped.add(ContactWrapper(false, current))
            }
        }
        return contactsWrapped
    }

    inner class ContactWrapper(val hasLeftChar: Boolean, val contact: BZContact)

    inner class ContactsViewHolder(view: View): RecyclerView.ViewHolder(view) {

//        val tvJid = view.findViewById<TextView>(R.id.tv_contact_item_jid)
        val tvName = view.findViewById<TextView>(R.id.tv_contact_item_contact_name)
        val tvLastActivity = view.findViewById<TextView>(R.id.tv_contact_item_last_online)
        val tvLeftChar = view.findViewById<TextView>(R.id.tv_contact_item_char)
        val avatarView = view.findViewById<ImageView>(R.id.iv_contact_avatar)
//        val indicator = view.findViewById<View>(R.id.contact_online_indicator)
        val v = view

        fun bind(contact: ContactWrapper, listener: (BZContact) -> Unit) {
//            tvJid.text = contact.contactJid

            tvName.text = contact.contact.nickName
            tvLastActivity.text = when {
                contact.contact.isOnline -> "Онлайн"
                contact.contact.lastVisit != null -> Date().humanizeDiffForLastActivity(contact.contact.lastVisit!!.toDate())
                else -> "Еще не заходил"
            }
            if (contact.hasLeftChar){
                tvLeftChar.visibility = View.VISIBLE
                tvLeftChar.text = contact.contact.nickName.first().toUpperCase().toString()
            } else {
                tvLeftChar.visibility = View.GONE
            }

//            if (contact.isOnline) indicator.visibility = View.VISIBLE
//            else indicator.visibility = View.INVISIBLE
            v.setOnClickListener { listener.invoke(contact.contact) }
            AvatarHelper.placeRoundAvatar(avatarView, contact.contact.avatar, contact.contact.getShortName(), contact.contact.contactJid)





        }
    }
}