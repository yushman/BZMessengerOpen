package ooo.emessi.messenger.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.r0adkll.slidr.Slidr
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.ui.viewmodels.ContactActivityViewModel
import ooo.emessi.messenger.utils.helpers.AvatarHelper
import ooo.emessi.messenger.utils.jidIsValid

class ContactAddActivity : AppCompatActivity() {

    companion object{
        const val CONTACT_ID = "JID"
        const val JID = "JID"
        const val NAME = "NAME"
    }

    private val TAG = this.javaClass.simpleName

//    lateinit var tvName: TextView
    lateinit var toolbar: Toolbar
    lateinit var tvName: TextView
    lateinit var etName: TextInputEditText
    lateinit var etNameWrapper: TextInputLayout
    lateinit var etSecondName: TextInputEditText
    lateinit var etSecondNameWrapper: TextInputLayout
    lateinit var etJid: TextInputEditText
    lateinit var etJidWrapper: TextInputLayout
    lateinit var btnSave: Button
    lateinit var ivAvatar: ImageView
    lateinit var contactViewModel: ContactActivityViewModel

    private var contactId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_add)

        initViewModel()
        initViews()
        initFromBundle()
    }

    private fun initFromBundle() {
        try {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                contactId = bundle.getString(Constants.KEY_CONTACT_JID, "")
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
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_contact_add)
        setSupportActionBar(toolbar)
        title = ""
        toolbar.title = ""
        toolbar.setNavigationOnClickListener { finish() }
        tvName = findViewById(R.id.tv_toolbar_contact_name)
        etName = findViewById(R.id.et_fname_contact_add)
        etNameWrapper = findViewById(R.id.wrapper_fname_contact_add)
        etSecondName = findViewById(R.id.et_sname_contact_add)
        etSecondNameWrapper = findViewById(R.id.wrapper_sname_contact_add)
        etJid = findViewById(R.id.et_jid_contact_add)
        etJidWrapper = findViewById(R.id.wrapper_jid_contact_add)
        btnSave = findViewById(R.id.btn_save_contact_add)
        ivAvatar = findViewById(R.id.iv_avatar_contact_add)

        btnSave.setOnClickListener{ saveContactInfo() }

        addAutomatchers()

        Slidr.attach(this)
    }


    private fun addAutomatchers() {

        etJid.addTextChangedListener(object : TextWatcher {
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

        etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 3 || s.isNullOrEmpty()){
                    etNameWrapper.isErrorEnabled = false
                    etNameWrapper.error = ""
                    tvName.text = s.toString()
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
        val fName = etName.text.toString()
        val sName = etSecondName.text.toString()
        var nickName = fName
        if (sName.isNotEmpty()) nickName += " $sName"

        if (jid.jidIsValid() && nickName.length > 3){
            contactViewModel.saveContactData(jid, nickName)
            finish()
        } else {
            Toast.makeText(this, "Fill the fields correctly", Toast.LENGTH_LONG).show()
        }



    }

    private fun initViewModel() {
        contactViewModel = ViewModelProviders.of(this).get(ContactActivityViewModel::class.java)
        contactViewModel.contactDto.observe(this, Observer { updateUI(it) })
    }

    private fun updateUI(contactDto: ContactDto?) {
        if (contactDto != null) {
            tvName.text = contactDto.name
            etName.setText(contactDto.name)
            etJid.setText(contactDto.contactJid)
            AvatarHelper.placeRoundAvatar(
                ivAvatar,
                contactDto.avatar,
                contactDto.name.capitalize().first().toString(),
                contactDto.contactJid
            )
        } else {

        }
    }

    private fun deleteContact(jid: String){
        contactViewModel.removeContact(jid)
        finish()
    }
}
