package ooo.emessi.messenger.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants.KEY_CHAT
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.view_item_model.chat.ChatViewItem
import ooo.emessi.messenger.ui.viewmodels.ChatViewModelFactory
import ooo.emessi.messenger.ui.viewmodels.SingleChatInfoActivityViewModel
import ooo.emessi.messenger.utils.helpers.AvatarHelper

class SingleChatInfoActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserNickname: TextView
    private lateinit var tvLastOnline: TextView
    private lateinit var tvJid: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var btnLeave: Button
    private lateinit var fabEdit: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var chatViewModel: SingleChatInfoActivityViewModel

    private lateinit var chatDto: ChatDto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_chat_info)

        initFromBundle()
        initViews()
        initViewModels()
    }

    override fun onResume() {
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
        chatViewModel = ViewModelProvider(this, ChatViewModelFactory(chatDto)).get(SingleChatInfoActivityViewModel::class.java)
        chatViewModel.chatViewItem.observe(this, Observer { updateUI(it) })
    }

    private fun updateUI(chatViewItem: ChatViewItem) {
        tvUserName.text = chatViewItem.chatDto.name
        tvLastOnline.text = chatViewItem.contactDto?.getLastActivityInfo()
        tvUserNickname.text = chatViewItem.chatDto.name
        tvJid.text = chatViewItem.chatDto.jid
        AvatarHelper.placeRoundAvatar(
            ivAvatar,
            chatViewItem.contactDto?.avatar,
            chatViewItem.contactDto?.getShortName(),
            chatViewItem.chatDto.name
        )

    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_single_chat_info)
        setSupportActionBar(toolbar)

        toolbar.title = ""
        title = ""
        toolbar.setNavigationOnClickListener { finish() }

        tvUserName = findViewById(R.id.tv_singlechat_info_username)
        tvLastOnline = findViewById(R.id.tv_singlechat_info_lastactivity)
        tvJid = findViewById(R.id.tv_singlechat_info_jid)
        tvUserNickname = findViewById(R.id.tv_singlechat_info_username2)
        ivAvatar = findViewById(R.id.iv_singlechat_info_avatar)
        btnLeave = findViewById(R.id.btn_singlechat_info_leave)
        fabEdit = findViewById(R.id.fab_singlechat_info_edit)

        btnLeave.setOnClickListener { chatViewModel.leaveChat() }
        fabEdit.setOnClickListener { routeToContactActivity() }
    }

    private fun routeToContactActivity() {
        val i = Intent(this, ContactAddActivity::class.java)
        i.putExtra(KEY_CHAT, chatDto)
        startActivity(i)
    }
}
