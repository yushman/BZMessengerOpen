package ooo.emessi.messenger.ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.ui.viewmodels.ChatViewModelFactory
import ooo.emessi.messenger.ui.viewmodels.SingleChatInfoActivityViewModel

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

    var chatId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_chat_info)

        initFromBundle()
        initViews()
        initViewModels()
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
        chatViewModel = ViewModelProviders.of(this, ChatViewModelFactory(chatId)).get(SingleChatInfoActivityViewModel::class.java)
        chatViewModel.chat.observe(this, Observer { updateUI(it) })
    }

    private fun updateUI(it: BZChat) {
        val nickName = it.contact?.nickName ?: it.name
        val lastActivity = it.getLastActivity()
        tvUserName.setText(nickName)
        tvLastOnline.setText(lastActivity)
        tvUserNickname.setText(nickName)
        tvJid.setText(it.jid)
        AvatarHelper.placeRoundAvatar(ivAvatar, it.contact?.avatar, it.getShortName(), it.jid)

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
        i.putExtra("JID", chatId)
        startActivity(i)
    }
}
