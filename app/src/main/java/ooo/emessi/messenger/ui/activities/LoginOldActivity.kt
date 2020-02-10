package ooo.emessi.messenger.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_login.et_password_wrapper
import kotlinx.android.synthetic.main.activity_login.et_user
import kotlinx.android.synthetic.main.activity_login.et_user_wrapper
import kotlinx.android.synthetic.main.activity_login_old.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.R
import ooo.emessi.messenger.managers.AccountManager
import ooo.emessi.messenger.service.BZChatService
import ooo.emessi.messenger.ssl.SslApi
import ooo.emessi.messenger.ui.viewmodels.LoginActivityViewModel
import ooo.emessi.messenger.utils.getHost
import ooo.emessi.messenger.utils.jidIsValid
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.koin.core.KoinComponent
import org.koin.core.get

class LoginOldActivity : AppCompatActivity(), KoinComponent {

    private val TAG = this.javaClass.simpleName

    private lateinit var etUser: TextInputEditText
    private lateinit var etUserWrapper: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var etPasswordWrapper: TextInputLayout
    private lateinit var btnLogin: Button

    private val accountManager: AccountManager = get()

    private var user: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_old)
        setupViews()
    }

    private fun setupViews() {
        etUser = et_user
        etUserWrapper = et_user_wrapper
        etPassword = et_password
        etPasswordWrapper = et_password_wrapper
        btnLogin = btn_login2

        btnLogin.isEnabled = false
        btnLogin.setOnClickListener { loginClick() }
        etUser.addTextChangedListener { etUserWrapper.error = null }
        etUser.addTextChangedListener { btnLogin.isEnabled = !checkFieldsAreEmpty()}
        etPassword.addTextChangedListener { etPasswordWrapper.error = null }
        etPassword.addTextChangedListener { btnLogin.isEnabled = !checkFieldsAreEmpty()}

    }

    private fun loginClick() {
        if (checkFieldsAreEmpty()) {
            showPasswordWrongOrEmpty("must not be empty")
            showUserWrongOrEmpty("must not be empty")
        }
        user = etUser.text.toString()
        if (user.isEmpty()){
            showUserWrongOrEmpty("Email is empty")
            return
        }
        if (!user.jidIsValid()) {
            showUserWrongOrEmpty("Wrong email")
            return
        }
        password = etPassword.text.toString()
        login(user, password)
    }

    private fun showPasswordWrongOrEmpty(error: String) {
        etPasswordWrapper.error = error
    }

    private fun showUserWrongOrEmpty(error: String) {
        etUserWrapper.error = error
    }

    private fun checkFieldsAreEmpty(): Boolean {
        val a = etUser.text.isNullOrEmpty()
        val b = etPassword.text.isNullOrEmpty()
//        if (a) showUserWrongOrEmpty("User is Empty")
//        if (b) showPasswordWrongOrEmpty("Password is Empty")
        return a && b
    }

    private fun connect(): Boolean {
        val connection = XMPPConnectionApi.getConnection()
        try {
            connection.connect()
        } catch (ex: Exception) {
            Log.d(TAG, "error" + ex.toString())
        } finally {
            return connection.isConnected
        }
    }

    fun login(user: String, password: String) = CoroutineScope(Dispatchers.IO).launch {
        val connection = XMPPConnectionApi.setupConnection(user.getHost(), user, password) //
        try {
            connection.connect()
            connection.login() //user, password
        } catch (ex: Exception) {
            Log.d(TAG, "error" + ex.toString())
        } finally {
            if (connection.isAuthenticated) {
                saveUserData(user, password)
                startMainActivity()
            } else {
                showLoginErrors()

            }
        }
    }

    fun showLoginErrors() = CoroutineScope(Dispatchers.Main).launch{
        etUser.setText("")
        etPassword.setText("")
        showUserWrongOrEmpty("Wrong user")
        showPasswordWrongOrEmpty("Wrong password")
    }

    fun saveUserData(user: String, password: String) {
        accountManager.saveAccount(user, password)
    }

    private fun startMainActivity() {
        var i = Intent(this, BZChatService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i)
        else
            startService(i)

        i = Intent(this, NewMainActivity::class.java)
        startActivity(i)
        finish()
    }
}
