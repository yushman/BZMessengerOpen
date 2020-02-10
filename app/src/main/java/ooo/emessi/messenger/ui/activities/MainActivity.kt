package ooo.emessi.messenger.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.R
import ooo.emessi.messenger.managers.PubSubManager
import ooo.emessi.messenger.ui.fragments.ChatsFragment
import ooo.emessi.messenger.ui.fragments.ContactsFragment
import ooo.emessi.messenger.ui.fragments.SettingsFragment


class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private val REQUEST_WRITE_STORAGE_REQUEST_CODE = 12345

    private lateinit var navigation: BottomNavigationView
    private lateinit var fragment: Fragment
    private val chatsFragment = ChatsFragment()
    private val contactsFragment = ContactsFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Chats"

        navigation = findViewById<BottomNavigationView>(R.id.bottom_naivgation)

        with(navigation) {
            selectedItemId = R.id.bottom_navigation_chats
            setOnNavigationItemSelectedListener { selectFragment(it) }
        }
        requestAppPermissions()
        fragment = ChatsFragment()
        routeToFragment(fragment)
    }

    private fun selectFragment(menuItem: MenuItem): Boolean {
        when (menuItem.itemId){
            R.id.bottom_navigation_chats -> {
                if (fragment is ChatsFragment) return true
                fragment = ChatsFragment()
                title = "Chats"
            }
            R.id.bottom_navigation_contacts -> {
                if (fragment is ContactsFragment) return true
                fragment = ContactsFragment()
                title = "Contacts"
            }
            R.id.bottom_navigation_settings -> {
                if (fragment is SettingsFragment) return true
                fragment = SettingsFragment()
                title = "Settings"
            }
            else -> fragment = ChatsFragment()
        }

        routeToFragment(fragment)
        return true
    }

    private fun routeToFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        fm.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()

    }

    private fun requestAppPermissions() {
        if (hasReadPermissions() && hasWritePermissions()) {
            return
        }

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
