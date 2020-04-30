package ooo.emessi.messenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto
import ooo.emessi.messenger.data.model.view_item_model.muc_affiliation.MucAffiliationViewItem
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import org.jivesoftware.smackx.muclight.MUCLightAffiliation

class MucAffiliationsAdapter(private val listener: (MucAffiliationDto, View) -> Unit) :
    RecyclerView.Adapter<MucAffiliationsAdapter.AffiliationsViewHolder>() {
    var affiliations = listOf<MucAffiliationViewItem>()
    var isMeOwner = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AffiliationsViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.affiliations_item, parent, false)
        return AffiliationsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return affiliations.size
    }

    override fun onBindViewHolder(holder: AffiliationsViewHolder, position: Int) {
        holder.bind(affiliations[position], listener)
    }

    fun updateOwner(_isMeOwner: Boolean) {
        isMeOwner = _isMeOwner
//        notifyDataSetChanged()
    }

    fun updateAffiliations(list: List<MucAffiliationViewItem>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return affiliations[oldItemPosition] == list[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return affiliations.size
            }

            override fun getNewListSize(): Int {
                return list.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return affiliations[oldItemPosition].hashCode() == list[newItemPosition].hashCode()
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        affiliations = list
        diffResult.dispatchUpdatesTo(this)
    }

    inner class AffiliationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvJid = view.findViewById<TextView>(R.id.tv_contact_item_jid)
        val tvName = view.findViewById<TextView>(R.id.tv_contact_item_contact_name)
        val tvLastActivity = view.findViewById<TextView>(R.id.tv_contact_item_last_online)
        val ivOwner = view.findViewById<ImageView>(R.id.iv_is_chat_owner)
        val avatarView = view.findViewById<ImageView>(R.id.iv_contact_avatar)
        val btnDelete = view.findViewById<ImageView>(R.id.btn_contact_item_delete)
        val v = view

        fun bind(
            affiliationViewItem: MucAffiliationViewItem,
            listener: (MucAffiliationDto, View) -> Unit
        ) {
//            tvJid.text = affiliation.affiliationJid.toString()

            tvName.text = affiliationViewItem.contact?.name
                ?: affiliationViewItem.affiliation.affiliationJid.asEntityBareJidIfPossible()
                    .asEntityBareJidString()
            tvLastActivity.text = affiliationViewItem.contact?.getLastActivityInfo()

//            if (affiliation.affiliationContact!!.isOnline) indicator.visibility = View.VISIBLE
//            else indicator.visibility = View.GONE
            if (isMeOwner) {
                if (affiliationViewItem.affiliation.affiliationType == MUCLightAffiliation.owner) {
                    btnDelete.visibility = View.GONE
                } else {
                    btnDelete.visibility = View.VISIBLE
                    btnDelete.setOnClickListener {
                        listener.invoke(
                            affiliationViewItem.affiliation,
                            btnDelete
                        )
                    }
                }
            } else {
                btnDelete.visibility = View.GONE
            }

            if (affiliationViewItem.affiliation.affiliationType == MUCLightAffiliation.owner) {
                ivOwner.visibility = View.VISIBLE
            } else {
                ivOwner.visibility = View.GONE
            }

            v.setOnClickListener { listener.invoke(affiliationViewItem.affiliation, v) }
            AvatarHelper.placeRoundAvatar(
                avatarView,
                affiliationViewItem.contact?.avatar,
                affiliationViewItem.contact?.getShortName(),
                affiliationViewItem.affiliation.affiliationJid.asEntityBareJidIfPossible()
                    .asEntityBareJidString()
            )
        }
    }
}