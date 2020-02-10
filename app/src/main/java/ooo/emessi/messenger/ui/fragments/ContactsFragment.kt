package ooo.emessi.messenger.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.ui.activities.ContactActivity
import ooo.emessi.messenger.ui.adapters.ContactsAdapter
import ooo.emessi.messenger.ui.viewmodels.ContactsFragmentViewModel

class ContactsFragment : Fragment() {

    companion object{
        const val SELECT_MODE = "SELECT_MODE"
        const val CONTACT_ID = "JID"
    }

    private val TAG = this.javaClass.simpleName

    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddContact: FloatingActionButton
    private lateinit var contactsViewModel: ContactsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_contacts, container, false)

        initViews(v)
        initViewModels()

//        if (savedInstanceState == null) contactsViewModel.loadContactsFromRoster()

        return v
    }

    private fun initViews(v: View) {
        val lm = LinearLayoutManager(v.context)
        val decorator = DividerItemDecoration(this.context, VERTICAL)
        val newItemAnimator = DefaultItemAnimator()
        newItemAnimator.supportsChangeAnimations = false
        newItemAnimator.changeDuration = 0
        contactsAdapter = ContactsAdapter{routeToContactEditActivity(it)}
        recyclerView = v.findViewById(R.id.rv_contacts)
        recyclerView.apply {
            layoutManager = lm
            adapter = contactsAdapter
            itemAnimator = null
            addItemDecoration(decorator)
        }
        fabAddContact = v.findViewById(R.id.fab_add_contact)
        fabAddContact.setOnClickListener { addContactClick() }


    }

    private fun routeToContactEditActivity(it: BZContact?) {

        val i = Intent(this.activity, ContactActivity::class.java)
        if (it != null) i.putExtra(CONTACT_ID, it.contactJid)
        else i.putExtra(CONTACT_ID,"")
        startActivity(i)
    }

    private fun addContactClick() {
        routeToContactEditActivity(null)
    }

    private fun initViewModels() {
        contactsViewModel = ViewModelProviders.of(this).get(ContactsFragmentViewModel::class.java)
        contactsViewModel.contacts.observe(this, Observer { contactsAdapter.updateContacts(it) })
    }

}
