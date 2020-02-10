package ooo.emessi.messenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.user.BZUser

class UsersAdapter (private val listener: (BZUser) -> Unit): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(){
    var users = listOf<BZUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return UsersViewHolder(v)
    }

    override fun getItemCount(): Int {
        return users.size + 1
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(users[position], listener)
    }

    fun updateUsers(_users: List<BZUser>){
        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return users[oldItemPosition] == _users[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return users.size
            }

            override fun getNewListSize(): Int {
                return _users.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return users[oldItemPosition].hashCode() == _users[newItemPosition].hashCode()
            }

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        users = _users
        diffResult.dispatchUpdatesTo(this)
    }

    inner class UsersViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val tvJid = view.findViewById<TextView>(R.id.tv_user_item_jid)
        val tvName = view.findViewById<TextView>(R.id.tv_user_item_user_name)
        val v = view

        fun bind(user: BZUser, listener: (BZUser) -> Unit) {
            tvJid.text = user.userJid
            tvName.text = user.userJid
            v.setOnClickListener { listener.invoke(user) }


        }
    }
}