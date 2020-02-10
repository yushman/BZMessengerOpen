package ooo.emessi.messenger.ui.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.*
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.account.BZAccount
import ooo.emessi.messenger.managers.AccountManager
import ooo.emessi.messenger.service.BZChatService
import ooo.emessi.messenger.service.ConnectionService
import ooo.emessi.messenger.service.ConnectionService.Companion.ACTION_CONNECTION
import ooo.emessi.messenger.ui.viewmodels.SplashActivityViewModel
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.koin.android.ext.android.get

class SplashActivity : AppCompatActivity() {
    companion object{
        const val REASON = "REASON"
        private val TAG = this.javaClass.simpleName
        private val SPLASH_DURATION = 100L
        private val DELAY = 10000L
        private val REQUEST_WRITE_STORAGE_REQUEST_CODE = 12345
    }

    private lateinit var splashViewModel: SplashActivityViewModel
//    private lateinit var br: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        if (XMPPConnectionApi.isInitialized) routeToMainActivity()
        super.onCreate(savedInstanceState)
        Log.d("Service", "spl cr")
        setContentView(R.layout.activity_splash)
        if (hasPermissions()) scheduleSplashScreen()
        else requestAppPermissions()
        splashViewModel = ViewModelProviders.of(this).get(SplashActivityViewModel::class.java)
        splashViewModel.account.observe(this, Observer { routeToActivity(it) })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_WRITE_STORAGE_REQUEST_CODE && grantResults.size == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                scheduleSplashScreen()
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStop() {
//        unregisterReceiver(br)
        super.onStop()
    }

//    private fun initReceiver() {
//        br = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                val result = intent!!.extras!!.getString(ConnectionService.RESULT)
//                when(result){
//                    ConnectionService.AUTH_DONE -> routeToMainActivity()
//                    ConnectionService.AUTH_FAILED -> routeToLoginActivity(ConnectionService.AUTH_FAILED)
//                    ConnectionService.EMPTY_ACCOUNT -> routeToLoginActivity(ConnectionService.EMPTY_ACCOUNT)
//                }
//            }
//        }
//        val ifilter = IntentFilter(ACTION_CONNECTION)
//        registerReceiver(br, ifilter)
//    }

    private fun scheduleSplashScreen() = CoroutineScope(Dispatchers.IO).launch{
        delay(SPLASH_DURATION)
        splashViewModel.loadAccount()
    }

    private fun routeToActivity(it: BZAccount?) {
        Log.d("Service", "spl get acc")
        if (it == null) routeToLoginActivity("")
        else
            setupConnection(it)
    }

    private fun setupConnection(it: BZAccount?) = CoroutineScope(Dispatchers.Main).launch{

//        XMPPConnectionApi.setupConnection()
        XMPPConnectionApi.setupConnection(it!!.host, it.userJid, it.password)

        var isConnectOk = withContext(Dispatchers.IO){connect()}
        while (!isConnectOk){
            isConnectOk = askReconnect()
            delay(DELAY)
        }

        val isLoginOk = withContext(Dispatchers.IO){login()}
        if (isLoginOk) {
            routeToMainActivity()

        }
        else {
            Toast.makeText(this@SplashActivity,"Failed to login, please login again",Toast.LENGTH_LONG).show()
            routeToLoginActivity("")
        }
    }

    private fun connect(): Boolean{

        if (XMPPConnectionApi.isConnected) return true
        try {
            XMPPConnectionApi.connect()
        } catch (ex: Exception) {
            Log.d(TAG, "error" + ex.toString())
        } finally {
            return XMPPConnectionApi.isConnected
        }

    }

    private suspend fun askReconnect(): Boolean {
        //need count attempts?
        Toast.makeText(this,"Connection error, trying to reconnect",Toast.LENGTH_LONG).show()
        Log.d(TAG, "try to reconnect")
        return  withContext(Dispatchers.IO){connect()}
    }

    private fun login(): Boolean {

        if (XMPPConnectionApi.isLoggedin) return true
        try {
            XMPPConnectionApi.login()
        } catch (ex: Exception) {
            Log.d(TAG, "error" + ex.toString())
        } finally {
            return XMPPConnectionApi.isLoggedin
        }
    }

    private fun routeToMainActivity() {
        val intent = Intent(this, NewMainActivity::class.java)
        startActivity(intent)
        startMainService()
        finish()
        //routeToChatActivity()
    }

    private fun routeToLoginActivity(reason: String){
        val intent = Intent(this, LoginOldActivity::class.java)
        intent.putExtra(REASON, reason)
        startActivity(intent)
        finish()
    }

    private fun startMainService() {
        val i = Intent(this@SplashActivity, BZChatService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i)
        else
            startService(i)
    }

    private fun hasPermissions() = hasReadPermissions() && hasWritePermissions()

    private fun requestAppPermissions() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_WRITE_STORAGE_REQUEST_CODE
        ) // your request code
    }

    private fun hasReadPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasWritePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


}
