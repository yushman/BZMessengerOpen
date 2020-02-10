package ooo.emessi.messenger.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.r0adkll.slidr.Slidr
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.utils.jidIsValid
import ooo.emessi.messenger.ui.viewmodels.ContactActivityViewModel


class ContactActivity : AppCompatActivity() {

    companion object{
        const val CONTACT_ID = "JID"
        const val JID = "JID"
        const val NAME = "NAME"
    }

    private val TAG = this.javaClass.simpleName

    lateinit var tvName: TextView
    lateinit var etName: TextInputEditText
    lateinit var etNameWrapper: TextInputLayout
    lateinit var etJid: TextInputEditText
    lateinit var etJidWrapper: TextInputLayout
    lateinit var ibSave: ImageButton
    lateinit var ibRemove: ImageButton
    lateinit var ivAvatar: ImageView
    lateinit var contactViewModel: ContactActivityViewModel

    private var contactId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        initViewModel()
        initViews()
        initFromBundle()
    }

    override fun onStop() {
        Log.d(TAG, "onstop")
        contactViewModel.contact.removeObservers(this)
        super.onStop()
    }

    override fun onResume() {
        if (contactId.isEmpty()) setEmptyFields()
        Log.d(TAG, "resume" + contactId.isEmpty())
        super.onResume()
    }

    private fun initFromBundle() {
        try {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                contactId = bundle.getString(CONTACT_ID, "")
                Log.d(TAG, contactId)
            }
        } catch (ex: Exception){
            ex.printStackTrace()
        }
        if (contactId.isNotEmpty()) {contactViewModel.loadContact(contactId)
            Log.d(TAG, contactId.isNotEmpty().toString())}
        else {setEmptyFields()
            Log.d(TAG, contactId.isNotEmpty().toString())}
    }

    private fun setEmptyFields() {
        contactId = ""
        etName.setText("")
        etJid.setText("")
        tvName.setText("New Contact")
        AvatarHelper.placeEmptyRoundAvatar(ivAvatar)
    }

    private fun initViews() {
        tvName = findViewById(R.id.tv_contact_name)
        etName = findViewById(R.id.et_contact_name)
        etNameWrapper = findViewById(R.id.et_contact_name_wrapper)
        etJid = findViewById(R.id.et_contact_jid)
        etJidWrapper = findViewById(R.id.et_contact_jid_wrapper)
        ibSave = findViewById(R.id.ib_contact_edit)
        ibRemove = findViewById(R.id.ib_contact_remove)
        ivAvatar = findViewById(R.id.iv_contact_avatar)

        ibSave.setOnClickListener{ saveContactInfo() }
        ibRemove.setOnClickListener { deleteContact(contactId) }

        addAutomatchers()

        Slidr.attach(this)
    }


    private fun addAutomatchers() {

        etJid.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().jidIsValid() || s.isNullOrEmpty()){
                    etJidWrapper.isErrorEnabled = false
                    etJidWrapper.error = ""
                } else {
                    etJidWrapper.isErrorEnabled = true
                    etJidWrapper.error = "WrongJid"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        etName.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 3 || s.isNullOrEmpty()){
                    etNameWrapper.isErrorEnabled = false
                    etNameWrapper.error = ""
                } else {
                    etNameWrapper.isErrorEnabled = true
                    etNameWrapper.error = "Name must contain at least 4 chars"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun jidIsValid(s: String): Boolean{
        return !TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches()
    }

    private fun saveContactInfo() {

        val jid = etJid.text.toString()
        val nickName = etName.text.toString()
        if (jid.jidIsValid() && nickName.length > 3){
            contactViewModel.saveContactData(jid, nickName)
        } else {
            Toast.makeText(this, "Fill right the fields", Toast.LENGTH_LONG).show()
        }

        finish()

    }

    private fun initViewModel() {
        contactViewModel = ViewModelProviders.of(this).get(ContactActivityViewModel::class.java)
        contactViewModel.contact.observe(this, Observer { updateUI(it) })
    }

    private fun updateUI(contact: BZContact?) {
        if (contact != null) {
            tvName.setText(contact.nickName)
            etName.setText(contact.nickName)
            etJid.setText(contact.contactJid)
            AvatarHelper.placeRoundAvatar(ivAvatar, contact.avatar, contact.nickName.capitalize().first().toString(), contact.contactJid)
        } else {
            AvatarHelper.placeEmptyRoundAvatar(ivAvatar)
        }
    }

    private fun deleteContact(jid: String){
        contactViewModel.removeContact(jid)
        finish()
    }
}
