package ooo.emessi.messenger.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.muc_affiliation.BZMucAffiliation
import ooo.emessi.messenger.ui.adapters.MucAffiliationsAdapter
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.ui.viewmodels.ChatViewModelFactory
import ooo.emessi.messenger.ui.viewmodels.MucLightChatInfoActivityViewModel

class MuclightChatInfoActivity : AppCompatActivity() {

    private lateinit var tvChatName: TextView
    private lateinit var tvLastOnline: TextView
    private lateinit var tvJid: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var btnLeave: Button
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var affiliationsAdapter: MucAffiliationsAdapter
    private lateinit var chatViewModel: MucLightChatInfoActivityViewModel

    var chatId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muclight_chat_info)

        initFromBundle()
        initViews()
        initViewModels()
    }

    override fun onResume() {
        chatViewModel.loadAffiliations()
        super.onResume()
    }

    private fun initFromBundle() {
        try {
            val bundle = intent.extras
            if (bundle != null){
                chatId = bundle.getString("JID","")
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun initViewModels() {
        chatViewModel = ViewModelProviders.of(this, ChatViewModelFactory(chatId)).get(
            MucLightChatInfoActivityViewModel::class.java)
        chatViewModel.chat.observe(this, Observer { updateUI(it) })
        chatViewModel.affiliations.observe(this, Observer { affiliationsAdapter.updateAffiliations(it) })
        chatViewModel.isMeOwner.observe(this, Observer { affiliationsAdapter.updateOwner(it) })
    }

    private fun updateUI(it: BZChat) {
        val nickName = it.name
        val lastActivity = it.getLastActivity()
        tvChatName.setText(nickName)
        tvLastOnline.setText(lastActivity)
        tvJid.setText(it.jid)
        AvatarHelper.placeRoundAvatar(ivAvatar, it.contact?.avatar, it.getShortName(), it.jid)

    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_muc_chat_info)
        setSupportActionBar(toolbar)
        toolbar.title = ""
        title = ""
        toolbar.setNavigationOnClickListener { finish() }

        tvChatName = findViewById(R.id.tv_muclightchat_info_chatname)
        tvLastOnline = findViewById(R.id.tv_muclightchat_info_lastactivity)
        tvJid = findViewById(R.id.tv_muclightchat_info_jid)
        ivAvatar = findViewById(R.id.iv_muclightchat_info_avatar)
        btnLeave = findViewById(R.id.btn_muclightchat_info_leave)
        fabAdd = findViewById(R.id.fab_muclightchat_info_add)
        recyclerView = findViewById(R.id.rv_muclightchat_info)

        btnLeave.setOnClickListener { chatViewModel.leaveChat() }
        fabAdd.setOnClickListener { routeToContactPickActivity(chatId) }

        val lm = LinearLayoutManager(this).apply { reverseLayout }
        val decorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val newItemAnimator = DefaultItemAnimator()
        newItemAnimator.supportsChangeAnimations = false
        newItemAnimator.changeDuration = 0
        affiliationsAdapter = MucAffiliationsAdapter {affiliation, view ->  onRvItemClicked(affiliation, view)}

        recyclerView.apply {
            layoutManager = lm
            adapter = affiliationsAdapter
            itemAnimator = null
            addItemDecoration(decorator)
        }
    }

    private fun onRvItemClicked(affiliation: BZMucAffiliation, view: View) {
        when (view.id){
            R.id.btn_contact_item_delete -> {
                chatViewModel.deleteAffiliation(affiliation)
                chatViewModel.loadAffiliations()
            }
                else -> routeToContactActivity(affiliation)
        }
    }

    private fun routeToContactPickActivity(chatId: String) {
        val i = Intent(this, ContactPickActivity::class.java)
        i.putExtra("JID", chatId)
        startActivity(i)
    }

    private fun routeToContactActivity(it: BZMucAffiliation) {
        val i = Intent(this, ContactAddActivity::class.java)
        i.putExtra("JID", it.affiliationJid.asEntityBareJidIfPossible().toString())
        startActivity(i)
    }
}
