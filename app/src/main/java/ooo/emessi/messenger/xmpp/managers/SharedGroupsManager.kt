package ooo.emessi.messenger.xmpp.managers

import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import org.jivesoftware.smackx.sharedgroups.SharedGroupManager

class SharedGroupsManager {
    fun foo() {
        val x = SharedGroupManager.getSharedGroups(XMPPManagersFactory.getXmppConnection())
    }
}