package ooo.emessi.messenger.xmpp.managers

import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import ooo.emessi.messenger.xmpp.custom_iqs.PushDiscoverIq
import org.jxmpp.jid.impl.JidCreate


class PushManager(private val managersFactory: XMPPManagersFactory) {

    fun sendPushDiscoverIq(token: String) {
        try {
            val con = managersFactory.getConnection()
            val iq = PushDiscoverIq(token)
            con.sendIqRequestAndWaitForResponse<PushDiscoverIq>(iq)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            val pumngr = managersFactory.getPushNotificationsManager()
            val hm = HashMap<String, String>()
            hm.put("service", "fcm")
            hm.put("device_id", token)
            hm.put("silent", "false")
            hm.put("topic", "")
            pumngr.enable(JidCreate.from(""), token, hm)
        }
    }
}