package ooo.emessi.messenger.managers

import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smackx.push_notifications.element.PushNotificationsElements
import java.util.*


object PushManager {
    fun isSupported(): Boolean{
        try {
            val jid = XMPPConnectionApi.getMyJid().asEntityBareJid()
            return XMPPConnectionApi.getServiceDiscoveryManager()
                .supportsFeatures(
                    jid,
                    Collections.singletonList(PushNotificationsElements.NAMESPACE)
                )

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}