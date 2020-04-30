package ooo.emessi.messenger.xmpp.managers

import kotlinx.coroutines.launch
import ooo.emessi.messenger.XMPPScope
import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import ooo.emessi.messenger.xmpp.connection.ConnectionOptions
import ooo.emessi.messenger.xmpp.connection.ConnectionState
import ooo.emessi.messenger.xmpp.extensions.MediaExtension
import org.jivesoftware.smack.*
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.sasl.SASLMechanism
import org.jivesoftware.smack.sm.StreamManagementException
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager
import org.jxmpp.jid.parts.Resourcepart
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import timber.log.Timber

class ConnectionManager(private val managersFactory: XMPPManagersFactory) : KoinComponent {
    private var isConnected: Boolean = false
    private var isLoggedin: Boolean = false
    private var isResumed: Boolean = false

    private val xmppScope by inject<XMPPScope>()

    private val domain = ""
    private val host = "xmpp.$domain"
    private val resource = "test"

    private lateinit var user: String
    private lateinit var password: String
    private lateinit var connectionListener: (state: ConnectionState) -> Unit

    fun makeConnection(listener: (state: ConnectionState) -> Unit) {
        setConnectionListener(listener)
        setupConnectionConfiguration(get())
        doConnect()// need async??
    }

    fun doConnect() = xmppScope.launch {
        connect()
    }

    fun doLogin() = xmppScope.launch {
        login()
    }

    fun doConnectAndLogin() = xmppScope.launch {
        connect()
        if (isConnected) login()
    }

    fun doLogin(user: String, password: String) = xmppScope.launch {
        setUserAndPassword(user, password)
        login()
    }

    fun doConnectAndLogin(user: String, password: String) = xmppScope.launch {
        setUserAndPassword(user, password)
        connect()
        if (isConnected) login()
    }

    fun doLogout() = xmppScope.launch {
        if (isLoggedin) logout()
    }

    private fun setConnectionListener(listener: (state: ConnectionState) -> Unit) {
        connectionListener = listener
    }

    private fun setUserAndPassword(user: String, password: String) {
        this.user = user
        this.password = password
    }

    private fun connect() {
        Timber.i( "Trying to connect domain - $host")
        try {
            if (!managersFactory.getConnection().isConnected) managersFactory.getConnection()
                .connect()
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun login() {
        Timber.i( "Trying to login domain - $host, user - $user, passsword - $password")
        try {
            if (!managersFactory.getConnection().isAuthenticated) managersFactory.getConnection()
                .login(
                user,
                password,
                Resourcepart.from(ConnectionManager.resource)
            ) // TODO Some with resourcePart
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun logout() = xmppScope.launch {
        try {
            managersFactory.getConnection().disconnect()
            managersFactory.getConnection().connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupConnectionConfiguration(connectionOptions: ConnectionOptions){
        managersFactory.getConnection().apply {
            setUseStreamManagement(connectionOptions.useStreamManagement)
            setUseStreamManagementResumption(connectionOptions.useStreamManagementResumption)
            replyTimeout = connectionOptions.replyTimeout
        }
        ProviderManager.addExtensionProvider(MediaExtension.ELEMENT, MediaExtension.NAMESPACE, MediaExtension.Companion.Provider())
        managersFactory.getReconnectionManager().enableAutomaticReconnection()
        managersFactory.getCarbonManager().disableCarbons()
        val drm = managersFactory.getDeliveryReceiptManager()
        drm.autoReceiptMode = DeliveryReceiptManager.AutoReceiptMode.always
        drm.autoAddDeliveryReceiptRequests()
        addConnectionListener()
    }


    private fun addConnectionListener() {
        managersFactory.getConnection().addConnectionListener(object : ConnectionListener {
            override fun connected(connection: XMPPConnection?) {
                Timber.i("connection connected")
                isConnected = true
                connectionListener.invoke(ConnectionState(isConnected, isLoggedin, isResumed, domain, user, password, host))
            }

            override fun connectionClosed() {
                Timber.i("connection closed")
                isConnected = false
                isLoggedin = false
                connectionListener.invoke(ConnectionState(isConnected, isLoggedin, isResumed, domain, user, password, host))
            }

            override fun connectionClosedOnError(e: Exception?) {
                Timber.i("connection lost")
                isConnected = false
                isLoggedin = false
                e?.printStackTrace()
                connectionListener.invoke(ConnectionState(isConnected, isLoggedin, isResumed, domain, user, password, host))
            }

            override fun authenticated(_connection: XMPPConnection?, resumed: Boolean) {
                isLoggedin = true
                isResumed = resumed
                if (!resumed) {
                    try {
                        managersFactory.getConnection().sendSmAcknowledgement()
                        managersFactory.getConnection().requestSmAcknowledgement()
                    } catch (e: SmackException.NotConnectedException) {
                        e.printStackTrace()
                    } catch (e: StreamManagementException.StreamManagementNotEnabledException) {
                        e.printStackTrace()
                    }
                }
                connectionListener.invoke(ConnectionState(isConnected, isLoggedin, isResumed, domain, user, password, host))
            }
        })
    }

    private fun isFirstLogin() = isLoggedin && !isResumed

    companion object {
        private const val domain = "mossales.ru"
        private const val host = "xmpp.$domain"
        private const val resource = "test"
        fun configureConnectionBuilder(connectionOptions: ConnectionOptions): XMPPTCPConnectionConfiguration.Builder {
            SmackConfiguration.addSaslMech(SASLMechanism.PLAIN)
            val builder = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(domain)
                .setCompressionEnabled(connectionOptions.compressionEnabled)
                .setSendPresence(connectionOptions.sendPresence)
                .setResource(connectionOptions.defaultResource)
                .setHost(host)
                .addEnabledSaslMechanism(SASLMechanism.PLAIN)

            if (connectionOptions.enableDefaultDebugger) builder.enableDefaultDebugger()
            if (connectionOptions.useSecurityMode) builder.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible)
            else builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            return builder
        }
    }


}