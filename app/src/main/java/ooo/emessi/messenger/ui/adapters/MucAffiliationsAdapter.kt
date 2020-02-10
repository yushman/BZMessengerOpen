package ooo.emessi.messenger.ui.adapters

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.muc_affiliation.BZMucAffiliation
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import org.jivesoftware.smackx.muclight.MUCLightAffiliation

class MucAffiliationsAdapter(private val listener: (BZMucAffiliation, View) -> Unit): RecyclerView.Adapter<MucAffiliationsAdapter.AffiliationsViewHolder>(){
    var affiliations = listOf<BZMucAffiliation>()
    var isMeOwner = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AffiliationsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.affiliations_item, parent, false)
        return AffiliationsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return affiliations.size
    }

    override fun onBindViewHolder(holder: AffiliationsViewHolder, position: Int) {
        holder.bind(affiliations[position], listener)
    }

    fun updateOwner(_isMeOwner: Boolean){
        isMeOwner = _isMeOwner
        notifyDataSetChanged()
    }

    fun updateAffiliations(items: List<BZMucAffiliation>){
        val _affiliations = items.filter { it.affiliationType != MUCLightAffiliation.none }.sortedBy { it.affiliationJid.toString().capitalize() }
        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return affiliations[oldItemPosition] == _affiliations[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return affiliations.size
            }

            override fun getNewListSize(): Int {
                return _affiliations.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return affiliations[oldItemPosition].hashCode() == _affiliations[newItemPosition].hashCode()
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        affiliations = _affiliations
        diffResult.dispatchUpdatesTo(this)
    }

    inner class AffiliationsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val tvJid = view.findViewById<TextView>(R.id.tv_contact_item_jid)
        val tvName = view.findViewById<TextView>(R.id.tv_contact_item_contact_name)
        val tvLastActivity = view.findViewById<TextView>(R.id.tv_contact_item_last_online)
        val ivOwner = view.findViewById<ImageView>(R.id.iv_is_chat_owner)
        val avatarView = view.findViewById<ImageView>(R.id.iv_contact_avatar)
        val btnDelete = view.findViewById<ImageView>(R.id.btn_contact_item_delete)
        val v = view

        fun bind(affiliation: BZMucAffiliation, listener: (BZMucAffiliation, View) -> Unit) {
//            tvJid.text = affiliation.affiliationJid.toString()

            tvName.text = affiliation.affiliationContact.nickName
            d("AFFILIATIONS", isMeOwner.toString())
            tvLastActivity.text = affiliation.affiliationContact.getLastActivity()

//            if (affiliation.affiliationContact!!.isOnline) indicator.visibility = View.VISIBLE
//            else indicator.visibility = View.GONE
            if (isMeOwner){
                if (affiliation.affiliationType == MUCLightAffiliation.owner) {
                    btnDelete.visibility = View.GONE
                } else {
                    btnDelete.visibility = View.VISIBLE
                    btnDelete.setOnClickListener { listener.invoke(affiliation, btnDelete) }
                }
            } else {btnDelete.visibility = View.GONE}

            if (affiliation.affiliationType == MUCLightAffiliation.owner) {
                ivOwner.visibility = View.VISIBLE
            } else {
                ivOwner.visibility = View.GONE
            }

            v.setOnClickListener { listener.invoke(affiliation, v) }
            AvatarHelper.placeRoundAvatar(avatarView, affiliation.affiliationContact.avatar, affiliation.affiliationContact.getShortName(), affiliation.affiliationContact.contactJid)
        }
    }
}