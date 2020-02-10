package ooo.emessi.messenger.data.model.bz_model.xmpp

import org.jivesoftware.smack.packet.ExtensionElement
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.provider.ExtensionElementProvider
import org.jivesoftware.smack.xml.XmlPullParser

class PushIQ : ExtensionElementProvider<ExtensionElement>(){
    override fun parse(
        parser: XmlPullParser?,
        initialDepth: Int,
        xmlEnvironment: XmlEnvironment?
    ): ExtensionElement {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}