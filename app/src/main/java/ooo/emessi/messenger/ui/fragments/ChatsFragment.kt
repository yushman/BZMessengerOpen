package ooo.emessi.messenger.ui.fragments


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.ui.activities.ContactPickActivity
import ooo.emessi.messenger.ui.activities.ContactSelectActivity
import ooo.emessi.messenger.ui.activities.MucLightChatActivity
import ooo.emessi.messenger.ui.activities.SingleChatActivity
import ooo.emessi.messenger.ui.adapters.ChatsAdapter
import ooo.emessi.messenger.ui.viewmodels.ChatsFragmentViewModel

class ChatsFragment : Fragment() {

    companion object{
        const val SELECT_MODE = "SELECT_MODE"
    }

    private val TAG = this.javaClass.simpleName


    private lateinit var fabAddChat: SpeedDialView
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var chatsViewModel: ChatsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_chats, container, false)

        initViews(v)
        initViewModels()



        return v
    }

    override fun onResume() {
//        chatsViewModel.loadChatsFromRoster()
        super.onResume()
    }

    private fun initViewModels() {
        chatsViewModel = ViewModelProviders.of(this).get(ChatsFragmentViewModel::class.java)
        chatsViewModel.chats.observe(this, Observer {
            chatsAdapter.updateChats(it)
            if (chatsAdapter.itemCount !=0) recyclerView.scrollToPosition(0)
//            chatsViewModel.addMediatorChatSources(it)
        })


//        chatsViewModel.loadMam()
    }

    private fun initViews(v: View) {

        val lm = LinearLayoutManager(v.context).apply { reverseLayout }
        val decorator = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        val newItemAnimator = DefaultItemAnimator()
        newItemAnimator.supportsChangeAnimations = false
        newItemAnimator.changeDuration = 0
        chatsAdapter = ChatsAdapter{openChat(it)}
        recyclerView = v.findViewById(R.id.rv_chats)

        recyclerView.apply {
            layoutManager = lm
            adapter = chatsAdapter
            itemAnimator = null
            addItemDecoration(decorator)
        }

        fabAddChat = v.findViewById(R.id.fab_add_chat)

        fabAddChat.addActionItem(SpeedDialActionItem.Builder(R.id.fab_muc_chat, R.drawable.ic_group_white_24dp)
            .setLabel("Group chat")
            .setFabBackgroundColor(resources.getColor(R.color.color_fab_new_muc_chat))
            .setFabImageTintColor(Color.WHITE)
            .setLabelClickable(true)
            .create())
        fabAddChat.addActionItem(SpeedDialActionItem.Builder(R.id.fab_single_chat, R.drawable.ic_person_white_24dp)
            .setLabel("Single chat")
            .setFabBackgroundColor(resources.getColor(R.color.color_fab_new_single_chat))
            .setFabImageTintColor(Color.WHITE)
            .setLabelClickable(true)
            .create())
        fabAddChat.setOnActionSelectedListener {
            when (it.id){
                R.id.fab_muc_chat -> {
                    routeToPickContacts()
                    fabAddChat.close()
                    return@setOnActionSelectedListener true
                }
                R.id.fab_single_chat -> {
                    routeToSelectContact()
                    fabAddChat.close()
                    return@setOnActionSelectedListener true
                }
                else -> false
            }
        }

    }

    private fun routeToSelectContact() {

        val i = Intent(activity, ContactSelectActivity::class.java)
        activity!!.startActivity(i)
    }

    private fun routeToPickContacts() {
        val i = Intent(activity, ContactPickActivity::class.java)
        activity!!.startActivity(i)
    }

    private fun openChat(it: BZChat) {
        if (it.isMulti) routeToMucChatActivity(it.jid)
        else routeToSingleChatActivity(it.jid)
    }

    private fun routeToMucChatActivity(id: String) {
        val intent = Intent(this.activity, MucLightChatActivity::class.java)
        intent.putExtra("JID",id)
        startActivity(intent)
    }

    private fun routeToSingleChatActivity(id: String) {
        val intent = Intent(this.activity, SingleChatActivity::class.java)
        intent.putExtra("JID",id)
        startActivity(intent)
    }
}
