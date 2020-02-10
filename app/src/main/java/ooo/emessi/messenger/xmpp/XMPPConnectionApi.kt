package ooo.emessi.messenger.xmpp

import android.util.Log
import org.jivesoftware.smack.*
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.provider.ProviderManager
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.sasl.SASLMechanism
import org.jivesoftware.smack.sm.StreamManagementException
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.blocking.BlockingCommandManager
import org.jivesoftware.smackx.carbons.CarbonManager
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager
import org.jivesoftware.smackx.iqlast.LastActivityManager
import org.jivesoftware.smackx.iqprivate.PrivateDataManager
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jivesoftware.smackx.mam.MamManager
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muclight.MultiUserChatLightManager
import org.jivesoftware.smackx.pep.PepManager
import org.jivesoftware.smackx.pubsub.PubSubManager
import org.jivesoftware.smackx.push_notifications.PushNotificationsManager
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager
import org.jivesoftware.smackx.reference.ReferenceManager
import org.jivesoftware.smackx.sharedgroups.SharedGroupManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.DomainBareJid
import java.util.regex.Pattern


object XMPPConnectionApi {


    private val TAG = this.javaClass.simpleName

    private const val DEFAULT_PACKET_TIMEOUT = 15000
    private val sPattern = Pattern.compile("^[A-Za-z0-9\\-\\.]+(\\|[A-Za-z0-9\\-\\.]+(:\\d+)?)?$")

    private var connection: XMPPTCPConnection? = null
    private var chatManager: ChatManager? = null
    private var vCardManager: VCardManager? = null
    private var mucManager: MultiUserChatManager? = null
    private var mucLightManager: MultiUserChatLightManager? = null
    private var roster: Roster? = null
    private var pubSubManager: PubSubManager? = null
    private var pubSubService: DomainBareJid? = null
    private var pushNotificationManager: PushNotificationsManager? = null
    private var mamManager: MamManager? = null
    private var carbonManager: CarbonManager? = null
    private var accountManager: AccountManager? = null
    private var reconnectionManager: ReconnectionManager? = null
    private var pepManager: PepManager? = null
    private var blockingCommandManager: BlockingCommandManager? = null
    private var serviceDiscoveryManager: ServiceDiscoveryManager? = null
    private var deliveryReceiptManager: DeliveryReceiptManager? = null
    private var fileUpload: HttpFileUploadManager? = null
    private var privateDataManager: PrivateDataManager? = null
    private var sharedGroupManager: SharedGroupManager? = null
    private var lastActivityManager: LastActivityManager? = null
    private var referenceManager: ReferenceManager? = null

    var isConnected: Boolean = false
    var isLoggedin: Boolean = false
    var isInitialized = false
    var user = ""
    var password = ""
    var host = ""

    fun setupConnection(): XMPPTCPConnection {
        val domain = //""
        this.host = domain
        try {
            val config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(host)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setCompressionEnabled(true)
                .setSendPresence(true)
                .enableDefaultDebugger()
                .setUsernameAndPassword(user, password)
                .setResource("test")
                .setHost("xmpp.$domain")
                .addEnabledSaslMechanism(SASLMechanism.PLAIN)
                .build()
            SmackConfiguration.addSaslMech(SASLMechanism.PLAIN)
            connection = XMPPTCPConnection(config)
            initializeSmack()
        }
        catch (e: Exception){
            e.printStackTrace()
        }


        return connection!!
    }

    fun setupConnection(domain: String, user: String, password: String): XMPPTCPConnection{
        this.host = domain
        this.user = user
        this.password = password
        try {
            val config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(domain)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setCompressionEnabled(true)
                .setSendPresence(true)
                .setUsernameAndPassword(user, password)//user, password
                .enableDefaultDebugger()
                .setResource("test")
                .setHost("xmpp.mossales.ru")//$domain
                .addEnabledSaslMechanism(SASLMechanism.PLAIN)

//                .setSocketFactory(DummySSLSocketFactory())
                .build()
//            SmackConfiguration.addSaslMech(SASLMechanism.PLAIN)
//            SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager")

            connection = XMPPTCPConnection(config)
            initializeSmack()
        }
        catch (e: Exception){
            e.printStackTrace()
        }



        return connection!!
    }

    private fun addConnectionListener(connection: XMPPTCPConnection) {
        connection.addConnectionListener(object : ConnectionListener {
            override fun connected(connection: XMPPConnection?) {
                Log.d("SMACK", "connection connected")
                isConnected = true
            }

            override fun connectionClosed() {
                Log.d("SMACK", "connection closed")
                isConnected = false
                isLoggedin = false
            }

            override fun connectionClosedOnError(e: Exception?) {
                Log.d("SMACK", "connection lost")
                isConnected = false
                isLoggedin = false
                e?.printStackTrace()
            }

            override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
                isLoggedin = true
                if (!resumed) {
                    try {
                        this@XMPPConnectionApi.connection!!.sendSmAcknowledgement()
                        this@XMPPConnectionApi.connection!!.requestSmAcknowledgement()
                    } catch (e: SmackException.NotConnectedException) {
                        e.printStackTrace()
                    } catch (e: StreamManagementException.StreamManagementNotEnabledException) {
                        e.printStackTrace()
                    }
                }
                    //send all unsent messages

            }
        })
    }

    private fun initializeSmack(){
        if (isInitialized) return
//        ProviderManager.addIQProvider(VCard.ELEMENT, VCard.NAMESPACE, VCard())
//        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual)
        getConnection().apply {
            setUseStreamManagement(true)
            setUseStreamManagementResumption(true)
            replyTimeout = DEFAULT_PACKET_TIMEOUT.toLong()
        }
        ProviderManager.addExtensionProvider(MediaExtension.ELEMENT, MediaExtension.NAMESPACE, MediaExtension.Companion.Provider())
        getReconnectionManager().enableAutomaticReconnection()

        CarbonManager.getInstanceFor(connection).disableCarbons()
        addConnectionListener(connection!!)
//        initializeSmack()
        val drm = getDeliveryReceiptManager()
        drm.autoReceiptMode = DeliveryReceiptManager.AutoReceiptMode.always
        drm.autoAddDeliveryReceiptRequests()
        isInitialized = true
    }

    fun connect() {
        Log.i(TAG, "Trying to connect domain - $host, user - $user, passsword - $password")
        if (!getConnection().isConnected) getConnection().connect()
    }

    fun login() {
//        val sasl = SASLMechanism.PLAIN
        Log.i(TAG, "Trying to login domain - $host, user - $user, passsword - $password")
        if (!getConnection().isAuthenticated) getConnection().login()
    }

    fun getConnection(): XMPPTCPConnection{
//        if (connection == null) setupConnection()
        return connection!!
    }

    fun getMamManager(): MamManager {
        if (mamManager == null) {
            mamManager = MamManager.getInstanceFor(getConnection())
            mamManager!!.enableMamForRosterMessages()
        }
        return mamManager!!
    }

    fun getChatManager(): ChatManager {
        if (chatManager == null) chatManager = ChatManager.getInstanceFor(getConnection())
        return chatManager!!

    }

    fun getMUCManager(): MultiUserChatManager {
        if (mucManager == null) mucManager = MultiUserChatManager.getInstanceFor(getConnection())
        return mucManager!!
    }

    fun getMucLightManager(): MultiUserChatLightManager {
        if (mucLightManager == null) mucLightManager = MultiUserChatLightManager.getInstanceFor(
            connection)
        return mucLightManager!!
    }

    fun getRoster(): Roster {
        if (roster == null) roster = Roster.getInstanceFor(getConnection())
        return roster!!
    }

    fun getPubSubManager(): PubSubManager{
        if (pubSubManager == null) pubSubManager = PubSubManager.getInstance(getConnection())
        return pubSubManager!!
    }

    fun getPubSubService(): DomainBareJid{
        if (pubSubService == null) pubSubService = PubSubManager.getPubSubService(getConnection())
        return pubSubService!!
    }

    fun getPushNotificationsManager(): PushNotificationsManager{
        if (pushNotificationManager == null) pushNotificationManager = PushNotificationsManager.getInstanceFor(getConnection())
        return pushNotificationManager!!
    }

    fun getReconnectionManager(): ReconnectionManager{
        if (reconnectionManager == null) reconnectionManager = ReconnectionManager.getInstanceFor(
            getConnection())
        return reconnectionManager!!
    }

    fun getMyJid() = getConnection().user

    fun getMyJidEntityBare() = getMyJid().asEntityBareJidIfPossible()

    fun getMyJidString() = getMyJidEntityBare().toString()

    fun getCarbonManager(): CarbonManager{
        if (carbonManager == null) carbonManager = CarbonManager.getInstanceFor(getConnection())
        return carbonManager!!
    }

    fun getVCardManager(): VCardManager{
        if (vCardManager == null) vCardManager = VCardManager.getInstanceFor(getConnection())

        return vCardManager!!
    }

    fun getAccountManager(): AccountManager{
        if (accountManager == null) accountManager = AccountManager.getInstance(
            getConnection())
        return accountManager!!
    }

    fun getServiceDiscoveryManager(): ServiceDiscoveryManager{
        if (serviceDiscoveryManager == null) serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(
            getConnection())
        return serviceDiscoveryManager!!
    }

    fun getPepManager(): PepManager{
        if (pepManager == null) pepManager = PepManager.getInstanceFor(getConnection())
        return pepManager!!
    }

    fun getBlockingCommandManager(): BlockingCommandManager{
        if (blockingCommandManager == null) blockingCommandManager = BlockingCommandManager.getInstanceFor(
            getConnection())
        return blockingCommandManager!!
    }

    fun getDeliveryReceiptManager(): DeliveryReceiptManager{
        if (deliveryReceiptManager == null) deliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(
            getConnection())
        return deliveryReceiptManager!!
    }

    fun getFileUpload(): HttpFileUploadManager {
        if (fileUpload == null) fileUpload = HttpFileUploadManager.getInstanceFor(getConnection())
        return fileUpload!!
    }

    fun getPrivateDataManager(): PrivateDataManager {
        if (privateDataManager == null) privateDataManager = PrivateDataManager.getInstanceFor(
            getConnection())
        return privateDataManager!!
    }

    fun getSharedGroupManager(): SharedGroupManager {
        if (sharedGroupManager == null) sharedGroupManager = SharedGroupManager()
        return sharedGroupManager!!
    }

    fun getLastActivityManager(): LastActivityManager {
        if (lastActivityManager == null) lastActivityManager = LastActivityManager.getInstanceFor(
            connection)
        return lastActivityManager!!
    }



    fun validate(value: String): Boolean {
        return sPattern.matcher(value).matches()
    }



}
