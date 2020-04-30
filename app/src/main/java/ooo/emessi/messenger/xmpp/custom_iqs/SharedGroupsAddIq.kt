package ooo.emessi.messenger.xmpp.custom_iqs

import ooo.emessi.messenger.xmpp.XMPPManagersFactory
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smackx.pubsub.packet.PubSub
import org.jxmpp.jid.impl.JidCreate

class SharedGroupsAddIq (pubSub: PubSub): IQ(pubSub){
    init {
        this.type = IQ.Type.set
        to = JidCreate.domainBareFrom("pubsub.mossales.ru")
        from = XMPPManagersFactory.getMyJid()
    }
    override fun getIQChildElementBuilder(xml: IQChildElementXmlStringBuilder?): IQChildElementXmlStringBuilder {
//        xml!!.append("<create node-id=\'groups/Marketing/Europe\'/>")
        xml!!.rightAngleBracket()
        xml.append("<crate")
//        xml.openElement("create")
        xml.attribute("node-id", "groups/aksjhd")
        xml.closeEmptyElement()
        return xml
    }

//    inner class Provider: IQProvider<SharedGroupsAddIq>(){
//        override fun parse(
//            parser: XmlPullParser?,
//            initialDepth: Int,
//            xmlEnvironment: XmlEnvironment?
//        ): SharedGroupsAddIq {
//
//        }

}

