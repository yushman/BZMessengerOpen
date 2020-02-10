package ooo.emessi.messenger.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.ui.adapters.ChatsAdapter
import ooo.emessi.messenger.ui.viewmodels.ChatsFragmentViewModel
import ooo.emessi.messenger.utils.isMultiChat

class NewMainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private val REQUEST_WRITE_STORAGE_REQUEST_CODE = 12345

    private lateinit var toolbar: Toolbar
    private lateinit var searchView: MaterialSearchView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var fragment: Fragment
    private lateinit var fabAddChat: SpeedDialView
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var chatsViewModel: ChatsFragmentViewModel

    private var isSearchOpened: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_main)

        initChatsFromBundle()
        initViews()
        initViewModels()
        initForwardedFromBundle()



//        fragment = ChatsFragment()
//        routeToFragment(fragment)


    }

    override fun onResume() {
        chatsViewModel.updateChatsFromRoster()
        super.onResume()
    }

    override fun onStop() {
        chatsViewModel.isForwarded = false
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_search, menu)
        val mi = menu.findItem(R.id.action_search_main)
        searchView.setMenuItem(mi)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty())
                chatsAdapter.updateChats(chatsViewModel.chats.value?.filter{it.name.contains(query, true)} ?: listOf())
                else chatsAdapter.updateChats(chatsViewModel.chats.value ?: listOf())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty())
                    chatsAdapter.updateChats(chatsViewModel.chats.value?.filter{it.name.contains(newText, true)} ?: listOf())
                else chatsAdapter.updateChats(chatsViewModel.chats.value ?: listOf())
                return true
            }

        })
        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener{
            override fun onSearchViewClosed() {
                chatsAdapter.updateChats(chatsViewModel.chats.value ?: listOf())
            }

            override fun onSearchViewShown() {
                //what to do
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_search_main -> updateAppBar(isSearchOpened)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen){
            searchView.closeSearch()
        } else
        super.onBackPressed()
    }

    private fun routeToFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        fm.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()

    }

    private fun routeToMainSearchFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateAppBar(searhOpened: Boolean) {
        isSearchOpened = !isSearchOpened
        if (searhOpened) {
            toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_menu_black_24dp)
        } else {
            toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_chevron_left_black_24dp)
        }
    }

    private fun initChatsFromBundle() {
        try {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                val chatId = bundle.getString("JID")
                if (!chatId.isNullOrEmpty())
                    if (chatId.isMultiChat()) routeToMucChatActivity(chatId)
                    else routeToSingleChatActivity(chatId)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun initForwardedFromBundle() {
        try {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                val forwardedMessage = bundle.getString("FORWARDED_MESSAGE_ID")
                if (!forwardedMessage.isNullOrEmpty()){
                    chatsViewModel.isForwarded = true
                    chatsViewModel.loadForwardedMessage(forwardedMessage)

                }


            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun initViewModels() {
        chatsViewModel = ViewModelProviders.of(this).get(ChatsFragmentViewModel::class.java)
        chatsViewModel.chats.observe(this, Observer {
            chatsAdapter.updateChats(it)
            if (chatsAdapter.itemCount !=0) recyclerView.scrollToPosition(0)
        })


//        chatsViewModel.loadMam()
    }

    private fun initViews() {

        drawerLayout = findViewById(R.id.drawer_main)
        toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.main_search)
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        searchView = findViewById(R.id.search_view_main)
//        navigationView = drawerLayout.findViewById(R.id.nav_view_main)
//        headerLayout = navigationView.inflateHeaderView(R.layout.nav_view_header)
//        val btnExit = headerLayout.findViewById<Button>(R.id.btn_nav_view_exit)
//        btnExit.setOnClickListener { exitClick() }



        val lm = LinearLayoutManager(this).apply { reverseLayout }
        val decorator = DividerItemDecoration( this, DividerItemDecoration.VERTICAL)
//        decorator.setDrawable(resources.getDrawable(R.drawable.divider))
//        decorator.
        val newItemAnimator = DefaultItemAnimator()
        newItemAnimator.supportsChangeAnimations = false
        newItemAnimator.changeDuration = 0
        chatsAdapter = ChatsAdapter{chatClick(it)}
        recyclerView = findViewById(R.id.rv_chats)

        recyclerView.apply {
            layoutManager = lm
            adapter = chatsAdapter
            itemAnimator = null
//            addItemDecoration(decorator)
        }

        fabAddChat = findViewById(R.id.fab_add_chat)
        fabAddChat.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_muc_chat, R.drawable.ic_group_black_24dp)
                .setLabel("Group chat")
                .setFabBackgroundColor(resources.getColor(R.color.color_fab_new_muc_chat))
                .setLabelClickable(true)
                .create())
        fabAddChat.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_single_chat, R.drawable.ic_person_black_24dp)
                .setLabel("Single chat")
                .setFabBackgroundColor(resources.getColor(R.color.color_fab_new_single_chat))
                .setLabelClickable(true)
                .create())
        fabAddChat.setOnActionSelectedListener {
            when (it.id){
                R.id.fab_muc_chat -> {
                    routeToPickContacts()
                    fabAddChat.close()
                    return@setOnActionSelectedListener true
                }
                R.id.fab_single_chat -> {
                    routeToSelectContact()
                    fabAddChat.close()
                    return@setOnActionSelectedListener true
                }
                else -> false
            }
        }

    }

    private fun exitClick() {
        chatsViewModel.exitFromAccount()
        stopBZService()
        routeToLoginActivity()
    }

    private fun stopBZService() {

    }

    private fun routeToLoginActivity() {
        val i = Intent(this, ContactSelectActivity::class.java)
        startActivity(i)
    }

    private fun chatClick(it: BZChat) {
        openChat(it)
        if (chatsViewModel.isForwarded)
        chatsViewModel.forwardMessage(it)

    }

    private fun routeToSelectContact() {

        val i = Intent(this, ContactSelectActivity::class.java)
        startActivity(i)
    }

    private fun routeToPickContacts() {
        val i = Intent(this, ContactPickActivity::class.java)
        startActivity(i)
    }

    private fun openChat(it: BZChat) {
        if (it.isMulti) routeToMucChatActivity(it.jid)
        else routeToSingleChatActivity(it.jid)
    }

    private fun routeToMucChatActivity(id: String) {
        val intent = Intent(this, MucLightChatActivity::class.java)
        intent.putExtra("JID",id)
        startActivity(intent)
    }

    private fun routeToSingleChatActivity(id: String) {
        val intent = Intent(this, SingleChatActivity::class.java)
        intent.putExtra("JID",id)
        startActivity(intent)
    }


}
