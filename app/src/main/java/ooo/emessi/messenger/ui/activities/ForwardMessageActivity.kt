package ooo.emessi.messenger.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.r0adkll.slidr.Slidr
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.data.model.view_item_model.contact.ContactPickViewItem
import ooo.emessi.messenger.ui.adapters.ContactsPickAdapter
import ooo.emessi.messenger.ui.viewmodels.ForwardMessageViewModel
import timber.log.Timber

class ForwardMessageActivity : AppCompatActivity() {

    private lateinit var contactsAdapter: ContactsPickAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var chipGroup: ChipGroup
    private lateinit var searchView: MaterialSearchView
    private lateinit var toolbar: Toolbar

    //    private lateinit var etChatName: EditText
    private lateinit var fabAddToChat: FloatingActionButton
    private lateinit var forwardMessageViewModel: ForwardMessageViewModel

    private var isShared = false
    private var selectedList = listOf<ContactPickViewItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forward_message)

        initViews()
        initViewModels()
        initFromBundle()
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_contact_select, menu)
//        val mi = menu.findItem(R.id.action_search_main)
//        searchView.setMenuItem(mi)
//        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (!query.isNullOrEmpty())
//                    contactsAdapter.updateContacts(forwardMessageViewModel.contacts.value.filter {
//                        it.contact.name.contains(
//                            query,
//                            true
//                        )
//                    }
//                        ?: listOf())
//                else contactsAdapter.updateContacts(
//                    forwardMessageViewModel.contacts.value ?: listOf()
//                )
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (!newText.isNullOrEmpty())
//                    contactsAdapter.updateContacts(forwardMessageViewModel.contacts.value.filter {
//                        it.contact.name.contains(
//                            newText,
//                            true
//                        )
//                    }
//                        ?: listOf())
//                else contactsAdapter.updateContacts(
//                    forwardMessageViewModel.contacts.value ?: listOf()
//                )
//                return true
//            }
//
//        })
//        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
//            override fun onSearchViewClosed() {
//                contactsAdapter.updateContacts(forwardMessageViewModel.contacts.value ?: listOf())
//            }
//
//            override fun onSearchViewShown() {
//                //what to do
//            }
//        })
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_new_contact -> routeToNewContactActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
        } else
            super.onBackPressed()
    }

    private fun initFromBundle() {
        val bundle = intent.extras
        if (bundle != null) {
            val messageId = bundle.getString(Constants.FORWARDED_MESSAGE_ID, "")
            if (messageId.isEmpty()) handleShared()
            else forwardMessageViewModel.createForwardMessage(messageId)
        }
//        if (chatId.isNotEmpty()) etChatName.visibility = View.GONE
    }

    private fun handleShared() {
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                } else if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent) // Handle single image being sent
                }

            }
            intent?.action == Intent.ACTION_SEND_MULTIPLE
                    && intent.type?.startsWith("image/") == true -> {
//                handleSendMultipleImages(intent) // Handle multiple images being sent
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }

        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            forwardMessageViewModel.createTextMessage(it)
        }
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            forwardMessageViewModel.createImageMessage(it)
        }
    }


    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_contacts)
        setSupportActionBar(toolbar)
        toolbar.title = ""
        toolbar.setNavigationOnClickListener { finish() }
        title = "Pick contact"
        searchView = findViewById(R.id.search_view_contacts)
        val lm = LinearLayoutManager(this)
        val decorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        contactsAdapter = ContactsPickAdapter {
            handlePickContact(it)
        }
        recyclerView = findViewById(R.id.rv_contact_pick)
        recyclerView.apply {
            layoutManager = lm
            adapter = contactsAdapter
//            addItemDecoration(decorator)
            itemAnimator = null
        }

        chipGroup = findViewById(R.id.chip_group_contact_pick)

//        etChatName = findViewById(R.id.et_chat_name)

        fabAddToChat = findViewById(R.id.fab_add_contacts_to_chat)
        fabAddToChat.setOnClickListener { forwardMessageClick() }
        Slidr.attach(this)
    }

    private fun forwardMessageClick() {
        Timber.d("size" + selectedList.size.toString())
        if (selectedList.isNullOrEmpty()) showDialogNoSelected().show()
        else {
//
//        finish()
        }
    }

    private fun showDialogNoSelected(): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Select any contacts")
            .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
            .create()

    }

    private fun handlePickContact(it: ContactPickViewItem) {
//        forwardMessageViewModel.handlePickChat(it.contactDto.contactJid)
        addChipToGroup(it)
    }

    private fun initViewModels() {
        forwardMessageViewModel =
            ViewModelProviders.of(this).get(ForwardMessageViewModel::class.java)
//        forwardMessageViewModel.contacts.observe(
//            this,
//            Observer { contactsAdapter.updateContacts(it) })
//        forwardMessageViewModel.loadChats()
//        forwardMessageViewModel.selectedContacts.observe(this, Observer { list ->
//            selectedList = list
//            if (selectedList.isNullOrEmpty()) {
//                chipGroup.visibility = View.GONE
//                fabAddToChat.visibility = View.GONE
//            } else {
//                chipGroup.visibility = View.VISIBLE
//                fabAddToChat.visibility = View.VISIBLE
//            }
//            Timber.d(selectedList.toString())
//        })
    }

    private fun addChipToGroup(contactPick: ContactPickViewItem) {
//        val views = chipGroup.children.associate { view -> view.tag to view }
//        if (contactPick.isSelected) {
//            chipGroup.removeView(views[contactPick.contactDto.contactJid])
//            return
//        }
//        val chip = Chip(this).apply {
//            text = contactPick.contactDto.name
//            isCloseIconVisible = true
//            tag = contactPick.contactDto.contactJid
//            isClickable = true
//            setChipBackgroundColorResource(R.color.color_chip_primary)
//        }
//        chip.setOnCloseIconClickListener {
//            forwardMessageViewModel.handleUnpickChat(contactPick.contactDto.contactJid)
//            chipGroup.removeView(chip)
//        }
//        chipGroup.addView(chip)
    }

//    private fun updateChips(contacts: List<ContactItem>){
//        chipGroup.visibility = if (contacts.isEmpty()) View.GONE else View.VISIBLE
//        val contactsList = contacts
//            .associateBy { item -> item.contact.contactJid }
//            .toMutableMap()
//        val views = chipGroup.children.associate { view -> view.tag to view }
//        for ((k,v) in views){
//            if (!contactsList.containsKey(k))
//        }
//
//    }

    private fun routeToNewContactActivity() {
        val i = Intent(this, ContactAddActivity::class.java)
        startActivity(i)
        finish()
    }
}
