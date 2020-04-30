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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants.KEY_CHAT
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.muc_affiliation.MucAffiliationDto
import ooo.emessi.messenger.data.model.view_item_model.chat.ChatViewItem
import ooo.emessi.messenger.ui.adapters.MucAffiliationsAdapter
import ooo.emessi.messenger.ui.viewmodels.ChatViewModelFactory
import ooo.emessi.messenger.ui.viewmodels.MucLightChatInfoActivityViewModel
import ooo.emessi.messenger.utils.helpers.AvatarHelper

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

    private lateinit var chatDto: ChatDto


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muclight_chat_info)

        initFromBundle()
        initViews()
        initViewModels()
    }

    override fun onResume() {
        chatViewModel.loadAffiliations()
        chatViewModel.loadChatInfo()
        super.onResume()
    }

    private fun initFromBundle() {
        try {
            val bundle = intent.extras
            if (bundle != null){
                chatDto = bundle.getParcelable<ChatDto>(KEY_CHAT)!!
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun initViewModels() {
        chatViewModel = ViewModelProvider(this, ChatViewModelFactory(chatDto)).get(
            MucLightChatInfoActivityViewModel::class.java)
        chatViewModel.chatViewItem.observe(this, Observer { updateUI(it) })
        chatViewModel.affiliations.observe(this, Observer { affiliationsAdapter.updateAffiliations(it) })
        chatViewModel.isMeOwner.observe(this, Observer { affiliationsAdapter.updateOwner(it) })
    }

    private fun updateUI(chatViewItem: ChatViewItem) {
        val nickName = chatViewItem.chatDto.name
        tvChatName.text = nickName
        tvLastOnline.text = chatViewItem.contactDto?.getLastActivityInfo()
        tvJid.text = chatViewItem.chatDto.jid
        AvatarHelper.placeRoundAvatar(
            ivAvatar,
            chatViewItem.contactDto?.avatar,
            chatViewItem.contactDto?.getShortName(),
            chatViewItem.chatDto.name)
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
        fabAdd.setOnClickListener { routeToContactPickActivity(chatDto) }

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

    private fun onRvItemClicked(affiliationDto: MucAffiliationDto, view: View) {
        when (view.id){
            R.id.btn_contact_item_delete -> {
                chatViewModel.deleteAffiliation(affiliationDto)
                chatViewModel.loadAffiliations()
            }
            else -> routeToContactActivity(affiliationDto)
        }
    }

    private fun routeToContactPickActivity(chatDto: ChatDto) {
        val i = Intent(this, ContactPickActivity::class.java)
        i.putExtra(KEY_CHAT, chatDto)
        startActivity(i)
    }

    private fun routeToContactActivity(it: MucAffiliationDto) {
        val i = Intent(this, ContactAddActivity::class.java)
        i.putExtra(KEY_CHAT, it.affiliationJid.asEntityBareJidIfPossible().toString())
        startActivity(i)
    }
}
