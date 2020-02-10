package ooo.emessi.messenger.managers

import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smackx.sharedgroups.SharedGroupManager

class SharedGroupsManager () {
    fun foo() {
        val x = SharedGroupManager.getSharedGroups(XMPPConnectionApi.getConnection())
    }
}