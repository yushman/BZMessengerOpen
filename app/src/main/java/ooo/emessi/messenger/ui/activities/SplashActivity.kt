package ooo.emessi.messenger.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.data.model.dto_model.account.AccountDto
import ooo.emessi.messenger.service.BZChatService
import ooo.emessi.messenger.ui.viewmodels.SplashActivityViewModel
import timber.log.Timber

class SplashActivity : AppCompatActivity() {
    companion object{
        private const val REASON = "REASON"
        private const val SPLASH_DURATION = 10L
        private const val REQUEST_WRITE_STORAGE_REQUEST_CODE = 12345
    }

    private lateinit var splashViewModel: SplashActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) startMainService(null)
//        if (XMPPConnectionApi.connection != null) routeToMainActivity()
        super.onCreate(savedInstanceState)
        Timber.d("spl cr")
        setContentView(R.layout.activity_splash)
        if (hasPermissions()) scheduleSplashScreen()
        else requestAppPermissions()
        splashViewModel = ViewModelProvider(this).get(SplashActivityViewModel::class.java)
        splashViewModel.accountDto.observe(this, Observer { routeToActivity(it) })

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

    private fun scheduleSplashScreen() = CoroutineScope(Dispatchers.IO).launch{
        delay(SPLASH_DURATION)
        splashViewModel.loadAccount()
    }

    private fun routeToActivity(it: AccountDto?) {
        Timber.d("spl get acc")
        startMainService(it)
        if (it == null) routeToLoginActivity("No account")
        else routeToMainActivity()
    }

    private fun setupConnection(it: AccountDto) {
        startMainService(it)
        routeToMainActivity()
    }

    private fun routeToMainActivity() {
        val i = Intent(this, NewMainActivity::class.java)
        if (intent != null){
            i.putExtras(intent)
        }
        startActivity(i)
        finish()
    }

    private fun routeToLoginActivity(reason: String){
        val intent = Intent(this, LoginOldActivity::class.java)
        intent.putExtra(REASON, reason)
        startActivity(intent)
        finish()
    }

    private fun startMainService(accountDto: AccountDto?) {
        val i = Intent(this, BZChatService::class.java)
        if (accountDto == null) i.action = Constants.ACTION_DO_CONNECT
        else {
            i.action = Constants.ACTION_DO_CONNECT_LOGIN
            i.putExtra("USER", accountDto.userJid)
            i.putExtra("HOST", accountDto.host)
            i.putExtra("PASSWORD", accountDto.password)
        }
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
