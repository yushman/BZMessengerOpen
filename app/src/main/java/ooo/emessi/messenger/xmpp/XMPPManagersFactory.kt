package ooo.emessi.messenger.xmpp

import ooo.emessi.messenger.xmpp.connection.ConnectionOptions
import ooo.emessi.messenger.xmpp.managers.*
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
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
import org.jivesoftware.smackx.sharedgroups.SharedGroupManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.DomainBareJid
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.regex.Pattern


class XMPPManagersFactory(private val connection: XMPPTCPConnection) : KoinComponent {
    
    private val sPattern = Pattern.compile("^[A-Za-z0-9\\-\\.]+(\\|[A-Za-z0-9\\-\\.]+(:\\d+)?)?$")

    fun getConnectionManager() = get<ConnectionManager>()

    fun getStanzaManager() = get<StanzaManager>()

    fun getRosterManager() = get<XRosterManager>()

    fun getMessageArchiveManager() = MessageArchiveManager(this)

    fun getPushManager() = PushManager(this)

    fun getMamManager(): MamManager {
        val mamManager = MamManager.getInstanceFor(connection)
        mamManager!!.enableMamForRosterMessages()
        return mamManager
    }

    fun getConnection() = connection

    fun getChatManager(): ChatManager = ChatManager.getInstanceFor(connection)

    fun getMUCManager(): MultiUserChatManager = MultiUserChatManager.getInstanceFor(connection)

    fun getMucLightManager(): MultiUserChatLightManager = MultiUserChatLightManager.getInstanceFor(
        connection
    )

    fun getRoster(): Roster = Roster.getInstanceFor(connection)

    fun getPubSubManager(): PubSubManager = PubSubManager.getInstance(connection)

    fun getPubSubService(): DomainBareJid = PubSubManager.getPubSubService(connection)

    fun getPushNotificationsManager(): PushNotificationsManager =
        PushNotificationsManager.getInstanceFor(connection)

    fun getReconnectionManager(): ReconnectionManager = ReconnectionManager.getInstanceFor(
        connection
    )

    fun getMyJid() = connection.user

    fun getMyJidEntityBare() = getMyJid().asEntityBareJidIfPossible()

    fun getMyJidString() = getMyJidEntityBare().toString()

    fun getCarbonManager(): CarbonManager = CarbonManager.getInstanceFor(connection)

    fun getVCardManager(): VCardManager = VCardManager.getInstanceFor(connection)

    fun getAccountManager(): AccountManager = AccountManager.getInstance(
        connection
    )

    fun getServiceDiscoveryManager(): ServiceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(
        connection
    )

    fun getPepManager(): PepManager = PepManager.getInstanceFor(connection)

    fun getBlockingCommandManager(): BlockingCommandManager = BlockingCommandManager.getInstanceFor(
        connection
    )

    fun getDeliveryReceiptManager(): DeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(
        connection
    )

    fun getFileUpload(): HttpFileUploadManager = HttpFileUploadManager.getInstanceFor(connection)

    fun getPrivateDataManager(): PrivateDataManager = PrivateDataManager.getInstanceFor(
        connection
    )

    fun getSharedGroupManager(): SharedGroupManager = SharedGroupManager()

    fun getLastActivityManager(): LastActivityManager = LastActivityManager.getInstanceFor(
        connection
    )

    fun validate(value: String): Boolean {
        return sPattern.matcher(value).matches()
    }

    fun setupOptions(connectionOptions: ConnectionOptions) {

    }

}