package ooo.emessi.messenger.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ooo.emessi.messenger.R
import ooo.emessi.messenger.service.BZChatService
import ooo.emessi.messenger.ssl.SslApi
import ooo.emessi.messenger.ui.viewmodels.LoginActivityViewModel
import ooo.emessi.messenger.utils.jidIsValid


class LoginActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName

    private lateinit var loginViewModel: LoginActivityViewModel
    private lateinit var etUser: TextInputEditText
    private lateinit var etUserWrapper: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var etPasswordWrapper: TextInputLayout
    private lateinit var btnLogin: Button
    private lateinit var btnGetCode: Button
    private lateinit var pgBar: ProgressBar
    private lateinit var ssl: SslApi

    private var user: String = ""
    private var password: String = ""
    private var code: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ssl = SslApi()
        setupViewModel()
        setupViews()
    }

    private fun setupViews() {
        etUser = et_user
        etUserWrapper = et_user_wrapper
        etPassword = et_password
        etPasswordWrapper = et_password_wrapper
        btnLogin = btn_login
        btnGetCode = btn_get_code
        pgBar = pb_login_activity

        btnLogin.setOnClickListener { loginClick() }
        btnGetCode.setOnClickListener { getCodeClick() }
        etUser.addTextChangedListener { etUserWrapper.error = null }
        etPassword.addTextChangedListener { etPasswordWrapper.error = null }

    }

    private fun getCodeClick() {
        pgBar.visibility = View.VISIBLE
        etUser.isEnabled = false
        user = etUser.text.toString()
        if (user.isEmpty()){
            showUserWrongOrEmpty("Email is empty")
            return
        }
        if (!user.jidIsValid()) {
            showUserWrongOrEmpty("Wrong email")
            return
        }
        btnGetCode.isEnabled = false
        loginViewModel.setupConnection(user, ssl)
    }

    private fun setupViewModel() {

        loginViewModel = ViewModelProviders.of(this).get(LoginActivityViewModel::class.java)
        loginViewModel.code.observe(this, Observer { codeArrived(it) })
        loginViewModel.authOk.observe(this, Observer { authArrived(it) })
        loginViewModel.httpOk.observe(this, Observer { httpArrived(it) })
        loginViewModel.getPublicSKey()
    }

    private fun httpArrived(it: Boolean?) {
        if (it != null && it) {
            btnGetCode.isEnabled = true
        } else {
            btnGetCode.isEnabled = false
            Toast.makeText(this, "No connection to login server", Toast.LENGTH_LONG).show()
            tryToReconnect()
        }
    }

    private fun tryToReconnect() = CoroutineScope(Dispatchers.IO).launch{
        delay(5000)
        loginViewModel.getPublicSKey()
    }

    private fun authArrived(it: Boolean?) {
        if (it != null && it) {

            val i = Intent(this, BZChatService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i)
            else
                startService(i)
            startMainActivity()
        } else {
            showLoginError()
        }
    }

    private fun codeArrived(it: String?) {
        pgBar.visibility = View.GONE
        if (it == null) {
            btnGetCode.isEnabled = true
            etUser.isEnabled = true
            return
        } else {
            code = it
            etPassword.setText(code)
            btnGetCode.visibility = View.GONE
            etPasswordWrapper.visibility = View.VISIBLE
            btnLogin.visibility = View.VISIBLE
        }
    }

    private fun loginClick() {
        val userCode = etPassword.text.toString()
        if (userCode.isEmpty()){
            showPasswordWrongOrEmpty("Code is empty")
            return
        }
        if (userCode != code) {
            showPasswordWrongOrEmpty("Wrong code")
            return
        }
        loginViewModel.login(user, code, ssl)
    }



    private fun checkFeildsAreWrong(): Boolean {
        val a = when {
            //check regex
            else -> false
        }
        val b = when {
            //check regex
            else -> false
        }
        return a&&b
    }

    private fun showPasswordWrongOrEmpty(error: String) {
        etPasswordWrapper.error = error
    }

    private fun showUserWrongOrEmpty(error: String) {
        etUserWrapper.error = error
    }

    private fun showLoginError() {
        pgBar.visibility = View.GONE
        btnGetCode.isEnabled = true
        etUser.isEnabled = true
        btnGetCode.visibility = View.VISIBLE
        etPasswordWrapper.visibility = View.GONE
        btnLogin.visibility = View.GONE
        Toast.makeText(this,"Error to Login", Toast.LENGTH_SHORT)
        Log.d(TAG, "error")
    }

    private fun checkFieldsAreEmpty(): Boolean {
        val a = etUser.text.isNullOrEmpty()
        val b = etPassword.text.isNullOrEmpty()
        if (a) showUserWrongOrEmpty("User is Empty")
        if (b) showPasswordWrongOrEmpty("Password is Empty")
        return a && b
    }

    private fun startMainActivity() {

        val i = Intent(this, NewMainActivity::class.java)
        startActivity(i)
        finish()
    }


}
