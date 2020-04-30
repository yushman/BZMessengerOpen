package ooo.emessi.messenger.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_login.et_password_wrapper
import kotlinx.android.synthetic.main.activity_login.et_user
import kotlinx.android.synthetic.main.activity_login.et_user_wrapper
import kotlinx.android.synthetic.main.activity_login_old.*
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.managers.account.AccountManager
import ooo.emessi.messenger.service.BZChatService
import ooo.emessi.messenger.utils.getHost
import ooo.emessi.messenger.utils.jidIsValid
import org.koin.core.KoinComponent
import org.koin.core.get

class LoginOldActivity : AppCompatActivity(), KoinComponent {

    private val TAG = this.javaClass.simpleName

    private lateinit var etUser: TextInputEditText
    private lateinit var etUserWrapper: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var etPasswordWrapper: TextInputLayout
    private lateinit var btnLogin: Button
    private lateinit var pBar: ProgressBar

    private val accountManager: AccountManager = get()

    private var user: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_old)
        setupViews()
        registerBR()
    }

    private fun registerBR() {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                togglePBar()
                intent?.extras?.let {
                    val isAuthenticated = it.getBoolean(Constants.EXTRAS_LOGIN_STATUS)
                    val user = it.getString("USER")!!
                    val host = it.getString("HOST")!!
                    val password = it.getString("PASSWORD")!!
                    if (isAuthenticated) {
                        saveUserData(user, password)
                        startMainActivity()
                    } else showLoginErrors()
                }
            }
        }
        val intenFilter = IntentFilter().apply { addAction(Constants.ACTION_LOGIN_STATUS) }
        registerReceiver(broadcastReceiver, intenFilter)
    }

    private fun setupViews() {
        etUser = et_user
        etUserWrapper = et_user_wrapper
        etPassword = et_password
        etPasswordWrapper = et_password_wrapper
        btnLogin = btn_login2
        pBar = pbar_login

        pBar.visibility = View.GONE
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
        togglePBar()
        tryToLogin(user, password)
//        login(user, password)
    }

    private fun togglePBar() {
        if (pBar.visibility == View.GONE) {
            pBar.visibility = View.VISIBLE
        }
        else pBar.visibility = View.GONE
    }

    private fun tryToLogin(user: String, password: String) {
        val i = Intent(this, BZChatService::class.java)
        i.action = Constants.ACTION_DO_LOGIN
        i.putExtra("USER", user)
        i.putExtra("HOST", user.getHost())
        i.putExtra("PASSWORD", password)
        startService(i)
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

//    private fun connect(): Boolean {
//        val connection = XMPPConnectionApi.getXmppConnection()
//        try {
//            connection.connect()
//        } catch (ex: Exception) {
//            Timber.d( "error%s", ex.toString())
//        } finally {
//            return connection.isConnected
//        }
//    }
//
//    fun login(user: String, password: String) = CoroutineScope(Dispatchers.IO).launch {
//        val connection = XMPPConnectionApi.setupConnection(user.getHost(), user, password) //
//        try {
//            connection.connect()
//            connection.login() //user, password
//        } catch (ex: Exception) {
//            Log.d(TAG, "error" + ex.toString())
//        } finally {
//            if (connection.isAuthenticated) {
//                saveUserData(user, password)
//                startMainActivity()
//            } else {
//                showLoginErrors()
//
//            }
//        }
//    }

    fun showLoginErrors() {
        etUser.setText("")
        etPassword.setText("")
        showUserWrongOrEmpty("Wrong user")
        showPasswordWrongOrEmpty("Wrong password")
    }

    fun saveUserData(user: String, password: String) {
        accountManager.saveAccount(user, password)
    }

    private fun startMainActivity() {
//        var i = Intent(this, BZChatService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i)
//        else
//            startService(i)

        val i = Intent(this, NewMainActivity::class.java)
        startActivity(i)
        finish()
    }
}
