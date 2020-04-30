package ooo.emessi.messenger.ui.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.r0adkll.slidr.Slidr
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants.KEY_CHAT
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.view_item_model.contact.ContactPickViewItem
import ooo.emessi.messenger.ui.adapters.ContactsPickAdapter
import ooo.emessi.messenger.ui.viewmodels.ContactPickActivityViewModel

class ContactPickActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName

    private lateinit var contactsAdapter: ContactsPickAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var chipGroup: ChipGroup
    private lateinit var searchView: MaterialSearchView
    private lateinit var toolbar: Toolbar
//    private lateinit var etChatName: EditText
    private lateinit var fabAddToChat: FloatingActionButton
    private lateinit var contactPickViewModel: ContactPickActivityViewModel

    private var chatDto: ChatDto? = null
    private var selectedList = listOf<ContactPickViewItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_pick)

        initViews()
        initViewModels()
        initFromBundle()
    }

    override fun onResume() {
        contactPickViewModel.loadContacts()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contact_select, menu)
        val mi = menu.findItem(R.id.action_search_main)
        searchView.setMenuItem(mi)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty())
                    contactsAdapter.updateContacts(contactPickViewModel.contactPickItems.value?.filter {
                        it.contactViewItem.contactDto.name.contains(
                            query,
                            true
                        )
                    } ?: listOf())
                else contactsAdapter.updateContacts(contactPickViewModel.contactPickItems.value ?: listOf())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty())
                    contactsAdapter.updateContacts(contactPickViewModel.contactPickItems.value?.filter {
                        it.contactViewItem.contactDto.name.contains(
                            newText,
                            true
                        )
                    } ?: listOf())
                else contactsAdapter.updateContacts(contactPickViewModel.contactPickItems.value ?: listOf())
                return true
            }

        })
        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener{
            override fun onSearchViewClosed() {
                contactsAdapter.updateContacts(contactPickViewModel.contactPickItems.value ?: listOf())
            }

            override fun onSearchViewShown() {
                //what to do
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_new_contact -> routeToNewContactActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen){
            searchView.closeSearch()
        } else
            super.onBackPressed()
    }

    private fun initFromBundle() {
        val bundle = intent.extras
        if (bundle != null){
            try {
                chatDto = bundle.getParcelable(KEY_CHAT)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
//        if (chatId.isNotEmpty()) etChatName.visibility = View.GONE
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
        contactsAdapter = ContactsPickAdapter{
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
        fabAddToChat.setOnClickListener { addContactsClick() }
        Slidr.attach(this)
    }

    private fun addContactsClick() {
        Log.d(TAG, "size" + selectedList.size.toString())
        if (selectedList.isNullOrEmpty()) makeDialogNoSelected().show()
        else {
            if (chatDto != null) addContactsToChat(chatDto!!)
            else makeDialogAskChatName().show()
//        finish()
        }
    }

    private fun addContactsToChat(chatDto: ChatDto) {
        contactPickViewModel.addContactToChat(chatDto)
        finish()
    }

    private fun makeDialogNoSelected(): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Select any contacts")
            .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
            .create()

    }

    private fun makeDialogAskChatName(): AlertDialog {

        val v = LayoutInflater.from(this).inflate(R.layout.dialog_ask_chat_name, null)
        val etChatName = v.findViewById<EditText>(R.id.et_dialog_ask_chat_name)
        val etChatNameWrapper =
            v.findViewById<TextInputLayout>(R.id.et_wrapper_dialog_ask_chat_name)
        return AlertDialog.Builder(this)
            .setView(v)
            .setTitle("Enter Groupchat Name")
            .setPositiveButton("OK") { dialog, which ->
                createNewChat(
                    etChatName,
                    etChatNameWrapper,
                    dialog
                )
            }
            .setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
            .setCancelable(false)
            .create()


//        contactPickViewModel.createNewChat(etChatName.text.toString())
    }

    fun createNewChat(
        et: EditText,
        etChatNameWrapper: TextInputLayout,
        dialog: DialogInterface
    ) {
        if (et.text.length < 4) {
            etChatNameWrapper.error = "Chat name must be at least 4 symbols long"
            return
        } else {
            contactPickViewModel.createNewChat(et.text.toString())
            dialog.dismiss()
        }

    }

    private fun handlePickContact(it: ContactPickViewItem) {
        contactPickViewModel.handlePickContact(it.contactViewItem.contactDto.contactJid)
        addChipToGroup(it)
    }

    private fun initViewModels() {
        contactPickViewModel = ViewModelProvider(this).get(ContactPickActivityViewModel::class.java)
        contactPickViewModel.contactPickItems.observe(this, Observer { contactsAdapter.updateContacts(it) })
        contactPickViewModel.selectedContacts.observe(this, Observer { list ->
            selectedList = list
            if (selectedList.isNullOrEmpty()) {
                chipGroup.visibility = View.GONE
                fabAddToChat.visibility = View.GONE
            }
            else {
                chipGroup.visibility = View.VISIBLE
                fabAddToChat.visibility = View.VISIBLE
            }
            Log.d(TAG, selectedList.toString())
        })
        contactPickViewModel.newChat.observe(this, Observer { routeToChatActivity(it) })
    }

    private fun addChipToGroup(contactPick: ContactPickViewItem){
        val views = chipGroup.children.associate { view -> view.tag to view }
        if (contactPick.isSelected) {
            chipGroup.removeView(views[contactPick.contactViewItem.contactDto.contactJid])
            return
        }
        val chip = Chip(this).apply {
            text = contactPick.contactViewItem.contactDto.name
            isCloseIconVisible = true
            tag = contactPick.contactViewItem.contactDto.contactJid
            isClickable = true
            setChipBackgroundColorResource(R.color.color_chip_primary)
        }
        chip.setOnCloseIconClickListener {
            contactPickViewModel.handleUnpickContact(contactPick.contactViewItem.contactDto.contactJid)
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }

    private fun routeToChatActivity(chat: ChatDto) {
        val i = Intent(this, MucLightChatActivity::class.java)
        i.putExtra(KEY_CHAT, chat)
        startActivity(i)
        finish()
    }

    private fun routeToNewContactActivity() {
        val i = Intent(this, ContactAddActivity::class.java)
        startActivity(i)
        finish()
    }
}
