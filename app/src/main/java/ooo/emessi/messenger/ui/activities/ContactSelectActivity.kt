package ooo.emessi.messenger.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.r0adkll.slidr.Slidr
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.ui.adapters.ContactsAdapter
import ooo.emessi.messenger.ui.viewmodels.ContactSelectActivityViewModel

class ContactSelectActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName

    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var searchView: MaterialSearchView
    private lateinit var contactSelectViewModel: ContactSelectActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conatct_select)

        initViews()
        initViewModels()
    }

    override fun onResume() {
        contactSelectViewModel.loadContacts()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contact_select, menu)
        val mi = menu.findItem(R.id.action_search_main)
        searchView.setMenuItem(mi)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty())
                    contactsAdapter.updateContacts(contactSelectViewModel.contacts.value?.filter{it.nickName.contains(query, true)} ?: listOf())
                else contactsAdapter.updateContacts(contactSelectViewModel.contacts.value ?: listOf())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty())
                    contactsAdapter.updateContacts(contactSelectViewModel.contacts.value?.filter{it.nickName.contains(newText, true)} ?: listOf())
                else contactsAdapter.updateContacts(contactSelectViewModel.contacts.value ?: listOf())
                return true
            }

        })
        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener{
            override fun onSearchViewClosed() {
                contactsAdapter.updateContacts(contactSelectViewModel.contacts.value ?: listOf())
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

    private fun initViewModels() {
        contactSelectViewModel = ViewModelProviders.of(this).get(ContactSelectActivityViewModel::class.java)
        contactSelectViewModel.contacts.observe(this, Observer { contactsAdapter.updateContacts(it) })
        contactSelectViewModel.loadContacts()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_contacts)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        title = "Select contact"
        searchView = findViewById(R.id.search_view_contacts)
        val lm = LinearLayoutManager(this)
        contactsAdapter = ContactsAdapter{routeToChatActivity(it)}
        recyclerView = findViewById(R.id.rv_contact_select)
        recyclerView.apply {
            layoutManager = lm
            adapter = contactsAdapter
            itemAnimator = null
        }

        Slidr.attach(this)
    }

    private fun routeToChatActivity(it: BZContact) {
        val i = Intent(this, SingleChatActivity::class.java)
        i.putExtra("JID", it.contactJid)
        i.putExtra("NEW_FLAG", true)
        startActivity(i)
        finish()
    }

    private fun routeToNewContactActivity() {
        val i = Intent(this, ContactAddActivity::class.java)
        startActivity(i)
        finish()
    }
}
