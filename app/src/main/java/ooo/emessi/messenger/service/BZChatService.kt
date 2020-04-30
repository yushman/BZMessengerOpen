package ooo.emessi.messenger.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.launch
import ooo.emessi.messenger.IOScope
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.managers.message.MessageReciever
import ooo.emessi.messenger.managers.roster.RosterManager
import ooo.emessi.messenger.utils.helpers.LogHelper
import ooo.emessi.messenger.xmpp.XMPPApi
import ooo.emessi.messenger.xmpp.connection.ConnectionState
import org.jivesoftware.smack.packet.*
import org.koin.core.KoinComponent
import org.koin.core.get
import timber.log.Timber
import java.util.*

class BZChatService : Service(), KoinComponent {
    companion object {

        const val ACTION = "ACTION"
        const val ACTION_LOAD_ROSTER = "ACTION_LOAD_ROSTER"
        const val ACTION_LOAD_LAST_MAM = "ACTION_LOAD_ROSTER"
        const val ACTION_OTHER = "ACTION_OTHER"
        const val ACTION_MESSAGE = "ACTION_MESSAGE"
        const val ACTION_ERROR = "ACTION_ERROR"
        const val ACTION_FOREGROUND = "ACTION_FOREGROUND"
        const val MESSAGE_FROM_JID = "MESSAGE_FROM_JID"
        const val MESSAGE_FROM_NAME = "MESSAGE_FROM_NAME"
        const val MESSAGE_BODY = "MESSAGE_BODY"
        const val STANDART_CONNECTION_DELAY = 1000L

        const val SELF_MESSEGING = true //Accept of receiving messeges, sended by myself
    }

    private val xmppApi = get<XMPPApi>()
    private val ioScope = get<IOScope>()
    private val rosterManager = get<RosterManager>()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Timber.d("servise created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d("servise start command")
        when (intent.action) {
            Constants.ACTION_DO_CONNECT -> doConnect(intent)
            Constants.ACTION_DO_LOGIN -> doLogin(intent)
            Constants.ACTION_DO_CONNECT_LOGIN -> doConnectAndLogin(intent)
            Constants.ACTION_DO_LOGOUT -> doLogout(intent)
        }
        return START_STICKY
    }

    private fun doLogout(intent: Intent) {
        xmppApi.reconnect()
    }

    private fun doConnect(intent: Intent) {
        xmppApi.connect { onConnectionStateChanged(it) }
    }

    private fun doLogin(intent: Intent) {
        intent.extras?.let {
            val user = it.getString("USER")
            val password = it.getString("PASSWORD")
            xmppApi.login(user!!, password!!)
        }
    }

    private fun doConnectAndLogin(intent: Intent) {
        intent.extras?.let {
            val user = it.getString("USER")
            val password = it.getString("PASSWORD")
            xmppApi.connect { onConnectionStateChanged(it) }
            xmppApi.login(user!!, password!!)
        }
    }

    private fun onAuthenticated(state: ConnectionState) {
        if (state.isLoggedin && !state.isResumed) firstStart()
        val i = Intent(Constants.ACTION_LOGIN_STATUS)
        i.putExtra(Constants.EXTRAS_LOGIN_STATUS, state.isLoggedin)
        i.putExtra("USER", state.user)
        i.putExtra("HOST", state.host)
        i.putExtra("PASSWORD", state.password)
        sendBroadcast(i)
    }

    private fun onConnectionStateChanged(state: ConnectionState) {
        if (state.isLoggedin) onAuthenticated(state)
    }

    private fun firstStart() {
        LogHelper.appendLog("SERVICE" + ":" + Date() + "Log Start")
        Timber.i("first start")
        xmppApi.setupStanzaListener { doStanza(it) }
        xmppApi.setupDeliveryListener { fromJid, toJid, receiptId, receipt -> }
        registerFBToken()
        rosterManager.updateChatsFromRoster()
    }

    private fun registerFBToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.d("getInstanceId failed" + task.exception)
                }
                // Get new Instance ID token
                val token = task.result?.token
                if (token != null) {
                    xmppApi.registerFBToken(token)
                }
                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Timber.d(msg)
            }
    }

    private fun doStanza(it: Stanza?) {
        when (it) {
            is Message -> receiveMessage(it)
            is Presence -> changePresence(it)
            is IQ -> processIQ(it)
        }
    }

    private fun processIQ(it: IQ) {
        if (it.type == IQ.Type.error) {
            showError(it.error)
            return
        }
    }

    private fun showError(error: StanzaError) {
        val i = Intent(this, NotificationService::class.java)
        i.putExtra(ACTION, ACTION_ERROR)
        i.putExtra(MESSAGE_FROM_JID, error.type.toString())
        i.putExtra(MESSAGE_BODY, "Condition - ${error.condition}")
        startService(i)
    }

    private fun changePresence(presence: Presence) {
        rosterManager.presenceChanged(presence)
    }

    private fun receiveMessage(it: Message) {
        Timber.i("receiveMessage")
        Timber.d(it.body)
//        if (it.body.isNullOrEmpty() && it.hasExtension("media", "bzm:media:1")) it.body =
//            "Picture"
        val acceptSelfMessages = SELF_MESSEGING //Accept of receiving messeges, sended by myself
        if (!acceptSelfMessages && it.from.asEntityFullJidIfPossible() == xmppApi.getMyJid()) return
        if (it.from.resourceOrEmpty.toString() == xmppApi.getMyJid()
                .asEntityBareJidIfPossible().toString()
        ) return
        if (it.type == Message.Type.error) {
            showError(it.error)
        } else {
            showMessage(it)
            ioScope.launch { MessageReciever(it, xmppApi.getMyJid().asEntityBareJidString()).recieve() }
        }
    }

    private fun showMessage(it: Message) {
        val i = Intent(this@BZChatService, NotificationService::class.java)
        val chatJid = it.from.asEntityBareJidIfPossible().toString()
        val chatName = chatJid

        i.putExtra(ACTION, ACTION_MESSAGE)
        i.putExtra(MESSAGE_FROM_JID, chatJid)
        i.putExtra(MESSAGE_FROM_NAME, chatName)
        i.putExtra(MESSAGE_BODY, it.body)
        startService(i)
    }

    override fun onDestroy() {
        xmppApi.disconnect()
        super.onDestroy()
    }

//    private fun initMucManager() {
//        mucManager = XMPPConnectionApi.getMucLightManager()
//        val domain = mucManager.localServices.first()
//        val rooms = mucManager.getOccupiedRooms(domain)
//        val chats = rooms.map { mucManager.getMultiUserChatLight(it.asEntityBareJidIfPossible()) }
//            chats.forEach { it.addMessageListener { message -> mucChatMessageReceived(message, it) }
//                Log.d("Service", "listener added" + it.room)
//                Log.d("Service", "occupants" + it.affiliations)
//            }
//    }
//    private fun initChatManager() {
//        chatManager = XMPPConnectionApi.getChatManager()
//        chatManager.addIncomingListener { from, message, chat -> newMessage(from, message, chat) }
//    }

//    private fun newMessage(from: EntityBareJid, message: Message, chat: Chat) {
//        MessagesManager.receiveSingleChatMessage(message, chat)
//
////        val i = Intent(Constants.NEW_MESSAGE_ACTION)
////        i.putExtra(Constants.MESSAGE_KEY, bzMessage)
////        sendBroadcast(i)
//    }

//    private fun mucChatMessageReceived(message: Message, chat: MultiUserChatLight) {
//        val myJid = XMPPConnectionApi.getMyJid().toString()
//        if (message.from.resourceOrEmpty.toString() != myJid) {
//            MessagesManager.receiveGroupChatMessage(message, chat)
//        }
//    }


}
