package ooo.emessi.messenger.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.ui.adapters.ChatsAdapter
import ooo.emessi.messenger.ui.viewmodels.ChatsListViewModel

class NewMainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var searchView: MaterialSearchView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var fabAddChat: SpeedDialView
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var chatsViewModel: ChatsListViewModel

    private var isSearchOpened: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_main)

//        initChatsFromBundle()
        initViews()
        initViewModels()
//        initForwardedFromBundle()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_search, menu)
        val mi = menu.findItem(R.id.action_search_main)
        searchView.setMenuItem(mi)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty())
                    chatsAdapter.updateChats(chatsViewModel.chats.value?.filter {
                        it.chatDto.name.contains(
                            query,
                            true
                        )
                    } ?: listOf())
                else chatsAdapter.updateChats(chatsViewModel.chats.value ?: listOf())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty())
                    chatsAdapter.updateChats(chatsViewModel.chats.value?.filter {
                        it.chatDto.name.contains(
                            newText,
                            true
                        )
                    } ?: listOf())
                else chatsAdapter.updateChats(chatsViewModel.chats.value ?: listOf())
                return true
            }

        })
        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener{
            override fun onSearchViewClosed() {
                chatsAdapter.updateChats(chatsViewModel.chats.value ?: listOf())
            }

            override fun onSearchViewShown() {
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

    private fun updateAppBar(searhOpened: Boolean) {
        isSearchOpened = !isSearchOpened
        if (searhOpened) {
            toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_menu_black_24dp)
        } else {
            toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_chevron_left_black_24dp)
        }
    }

    private fun initViewModels() {
        chatsViewModel = ViewModelProvider(this).get(ChatsListViewModel::class.java)
        chatsViewModel.chats.observe(this, Observer {
            chatsAdapter.updateChats(it)
            if (chatsAdapter.itemCount !=0) recyclerView.scrollToPosition(0)
        })
    }

    private fun initViews() {

        drawerLayout = findViewById(R.id.drawer_main)
        navigationView = findViewById(R.id.navigation_main)
        toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.main_search)
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        searchView = findViewById(R.id.search_view_main)
        navigationView.setNavigationItemSelectedListener { navItemClicked(it) }
        val lm = LinearLayoutManager(this).apply { reverseLayout }
        val newItemAnimator = DefaultItemAnimator()
        newItemAnimator.supportsChangeAnimations = false
        newItemAnimator.changeDuration = 0
        chatsAdapter = ChatsAdapter{chatClick(it)}
        recyclerView = findViewById(R.id.rv_chats)

        recyclerView.apply {
            layoutManager = lm
            adapter = chatsAdapter
            itemAnimator = null
        }

        fabAddChat = findViewById(R.id.fab_add_chat)
        fabAddChat.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_muc_chat, R.drawable.ic_group_black_24dp)
                .setLabel("Group chat")
                .setFabBackgroundColor(resources.getColor(R.color.color_fab_new_muc_chat, this.theme))
                .setLabelClickable(true)
                .create())
        fabAddChat.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_single_chat, R.drawable.ic_person_black_24dp)
                .setLabel("Single chat")
                .setFabBackgroundColor(resources.getColor(R.color.color_fab_new_single_chat, this.theme))
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

    private fun navItemClicked(menuItem: MenuItem): Boolean {
        drawerLayout.closeDrawers()
        when (menuItem.itemId){
            R.id.nav_menu_contacts -> routeToSelectContact()
            R.id.nav_menu_chats -> {}
            R.id.nav_menu_settings -> routeToSettingsActivity()
            R.id.nav_menu_logout -> doLogOut()
        }
        return true
    }

    private fun doLogOut() {
        chatsViewModel.logout()
        routeToLoginActivity()
    }

    private fun routeToSettingsActivity() {
        val i = Intent(this, SettingsActivity::class.java)
        startActivity(i)
    }

    private fun routeToLoginActivity() {
        val i = Intent(this, LoginOldActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun chatClick(it: ChatDto) {
        openChat(it)
    }

    private fun routeToSelectContact() {

        val i = Intent(this, ContactSelectActivity::class.java)
        startActivity(i)
    }

    private fun routeToPickContacts() {
        val i = Intent(this, ContactPickActivity::class.java)
        startActivity(i)
    }

    private fun openChat(chatDto: ChatDto) {
        if (chatDto.isMulti) routeToMucChatActivity(chatDto)
        else routeToSingleChatActivity(chatDto)
    }

    private fun routeToMucChatActivity(chatDto: ChatDto) {
        val intent = Intent(this, MucLightChatActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT, chatDto)
        startActivity(intent)
    }

    private fun routeToSingleChatActivity(chatDto: ChatDto) {
        val intent = Intent(this, SingleChatActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT, chatDto)
        startActivity(intent)
    }


}
