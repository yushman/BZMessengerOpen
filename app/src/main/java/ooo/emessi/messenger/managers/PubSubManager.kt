package ooo.emessi.messenger.managers

import android.content.Context
import android.content.Intent
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ooo.emessi.messenger.constants.Constants
import ooo.emessi.messenger.service.BZChatService
import ooo.emessi.messenger.service.NotificationService
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import ooo.emessi.messenger.xmpp.custom_iqs.PushDiscoverIq
import org.jivesoftware.smackx.pubsub.*
import org.jivesoftware.smackx.xdata.packet.DataForm
import org.jxmpp.jid.impl.JidCreate


class PubSubManager(val token: String) {

    val pubSubManager = XMPPConnectionApi.getPubSubManager()

    fun sendPushDiscoverIq() = CoroutineScope(Dispatchers.IO).launch{
        try {
            val iqId = Constants.DEVICE_ID + token.removeRange(0, 10)
            val con = XMPPConnectionApi.getConnection()
            val iq = PushDiscoverIq(iqId)
            con.sendStanza(iq)
            delay(1000)
            val pumngr = XMPPConnectionApi.getPushNotificationsManager()
            val hm = HashMap<String, String>()
            hm.put("service", "fcm")
            hm.put("device_id", token)
            pumngr.enable(JidCreate.from("pubsub.mossales.ru"), iqId, hm)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun startNotify(
        context: Context?,
        it: ItemPublishEvent<Item>
    ) {
        val i = Intent(context, NotificationService::class.java)
        i.putExtra(BZChatService.ACTION, BZChatService.ACTION_MESSAGE)
        i.putExtra(BZChatService.MESSAGE_FROM_JID, it.nodeId)
        i.putExtra(BZChatService.MESSAGE_BODY, it.publishedDate.toString())
        context!!.startService(i)
    }
}
