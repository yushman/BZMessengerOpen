package ooo.emessi.messenger.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.managers.account.AccountManager
import ooo.emessi.messenger.managers.account.LoginManager
import ooo.emessi.messenger.ssl.SslApi
import ooo.emessi.messenger.utils.getHost
import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import org.koin.core.KoinComponent
import org.koin.core.get

class LoginActivityViewModel : ViewModel(), KoinComponent{

    private val TAG = this.javaClass.simpleName
    private val accountManager: AccountManager = get()
    private val loginManager =
        LoginManager()
    val code: LiveData<String?> = loginManager.code
    val authOk: MutableLiveData<Boolean> = MutableLiveData()
    val httpOk: LiveData<Boolean> = loginManager.httpOk

    fun getPublicSKey(){
        loginManager.getPublicSKey()
    }

    fun setupConnection(user: String, ssl: SslApi) = CoroutineScope(Dispatchers.Main).launch {
        val domain = user.getHost()
//        XMPPConnectionApi.setupConnection(domain)

        loginManager.getCode(user)
//        connect()
    }

    private fun connect(): Boolean {
        val connection = XMPPManagersFactory.getXmppConnection()
        try {
            connection.connect()
        } catch (ex: Exception) {
            Log.d(TAG, "error" + ex.toString())
        } finally {
            return connection.isConnected
        }
    }

    fun login(user: String, code: String, ssl: SslApi) = CoroutineScope(Dispatchers.IO).launch {
        val (login, password) = loginManager.register(user, code)
        val connection = XMPPManagersFactory.getXmppConnection() //
        try {
            connection.connect()
            connection.login() //user, password
        } catch (ex: Exception) {
            Log.d(TAG, "error" + ex.toString())
        } finally {
            if (connection.isAuthenticated) {
                saveUserData(login, password)
                authOk.postValue(true)
            } else authOk.postValue(false)
        }
    }

    fun saveUserData(user: String, password: String) {
       accountManager.saveAccount(user, password)
    }

    fun getPublicKey(ssl: SslApi) {
        loginManager.getPublicKey(ssl)
    }

}