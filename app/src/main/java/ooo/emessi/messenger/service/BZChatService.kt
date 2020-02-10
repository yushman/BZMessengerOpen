package ooo.emessi.messenger.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.util.Log.d
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.R
import ooo.emessi.messenger.controllers.ChatListController
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.managers.*
import ooo.emessi.messenger.utils.helpers.LogHelper
import ooo.emessi.messenger.utils.helpers.NotifyHelper
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smack.packet.*
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*

class BZChatService : Service(), KoinComponent {
    companion object{

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
    private val TAG = this.javaClass.simpleName
    private lateinit var connection: XMPPTCPConnection


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d("Service", "created")
        if (!XMPPConnectionApi.isInitialized) stopSelf()
        connection = XMPPConnectionApi.getConnection()
        val nh = NotifyHelper(this)
        startForeground(NotifyHelper.NOTIFICATION_ID_FOREGROUND, nh.createForegroundNotification())
        firstStart()
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("Service", "s")
//        initRoster()
        val action = intent.action
//        when (action) {
//            null -> initRoster()
//            ACTION_LOAD_ROSTER -> initRoster()
//        }

//            initChatManager()
//            initMucManager()
//        return START_STICKY
        return super.onStartCommand(intent, flags, startId)
    }

    private fun firstStart() = CoroutineScope(Dispatchers.Main).launch {
                registerFBToken()
        LogHelper.appendLog(TAG + ":" + Date() +  "Log Start")
        connection.addAsyncStanzaListener(
            { packet -> doStanza(packet) },
            { stanza -> stanzaFilter(stanza)})
//        connection.addStanzaSendingListener(
//            {packet: Stanza? -> doSendedStanza(packet) },
//            {stanza: Stanza? -> stanza is Message }
//        )
        XMPPConnectionApi.getDeliveryReceiptManager().addReceiptReceivedListener { fromJid, toJid, receiptId, receipt ->
            MessagesManager(fromJid.asEntityBareJidIfPossible().asEntityBareJidString()).receiveDeliveryReceipt(receiptId)
        }
        initRoster()
    }

    private fun stanzaFilter(stanza: Stanza): Boolean{
        d(TAG, stanza.hasExtension("bzm:media:1").toString())
        return when (stanza){
            is Message -> (!stanza.body.isNullOrEmpty()) || (stanza.hasExtension("bzm:media:1"))
            is Presence -> true
            is IQ -> false
            else -> false
        }
    }

//    private fun doSendedStanza(packet: Stanza?) {
//        if (packet != null) MessagesManager.messageSended(packet)
//    }

    private fun registerFBToken() = CoroutineScope(Dispatchers.IO).launch {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                if (token != null) {
                    val pubSubManager = PubSubManager(token)
                    pubSubManager.sendPushDiscoverIq()
                }


                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
            })
    }

    private fun initRoster() = CoroutineScope(Dispatchers.Main).launch{
//        ContactsManager.loadContactsFromRoaster()
        val chatListManager =
            ChatListController()
        chatListManager.loadChatsFromRoster()
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
//        Toast.makeText(this, error.condition.toString(), Toast.LENGTH_LONG).show()
        val i = Intent(this, NotificationService::class.java)
        i.putExtra(ACTION, ACTION_ERROR)
        i.putExtra(MESSAGE_FROM_JID, error.type.toString())
        i.putExtra(MESSAGE_BODY, "Condition - ${error.condition}")
        startService(i)
    }

    private fun changePresence(it: Presence) {
        val contactsManager = ContactsManager()
        contactsManager.presenceChanged(it)
    }

    private fun receiveMessage(it: Message) {
        d(TAG, it.body)
        val acceptSelfMessages = SELF_MESSEGING //Accept of receiving messeges, sended by myself
        if (!acceptSelfMessages && it.from.asEntityFullJidIfPossible() == XMPPConnectionApi.getMyJid()) return
        if (it.from.resourceOrEmpty.toString() == XMPPConnectionApi.getMyJid().asEntityBareJidIfPossible().toString()) return
        if (it.type == Message.Type.error) {
            showError(it.error)
        } else {
            showMessage(it)
            MessageReciever(it).recieve()
        }
    }

    private fun showMessage(it: Message) = CoroutineScope(Dispatchers.IO).launch {
        val i = Intent(this@BZChatService, NotificationService::class.java)
        val chatRepo: ChatRepo = get()
        val chatJid = it.from.asEntityBareJidIfPossible().toString()
        val chatName = chatRepo.getChatById(it.from.asEntityBareJidIfPossible().toString())?.name
            ?: chatJid

        i.putExtra(ACTION, ACTION_MESSAGE)
        i.putExtra(MESSAGE_FROM_JID, chatJid)
        i.putExtra(MESSAGE_FROM_NAME, chatName)
        i.putExtra(MESSAGE_BODY, it.body)
        startService(i)
    }

    override fun onDestroy() {
        stopForeground(true)
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
