package ooo.emessi.messenger.xmpp.custom_iqs

import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smackx.pubsub.packet.PubSub
import org.jxmpp.jid.impl.JidCreate

class PushDiscoverIq (val uniq: String, pubSub: PubSub = PubSub()) : IQ(pubSub) {
    init {
        this.type = IQ.Type.set
        this.to = JidCreate.domainBareFrom("pubsub.mossales.ru")
    }
    override fun getIQChildElementBuilder(xml: IQChildElementXmlStringBuilder?): IQChildElementXmlStringBuilder {
        xml!!.rightAngleBracket()
        xml.append("<create")
        xml.attribute("node", uniq)
        xml.attribute("type", "push")
        xml.closeEmptyElement()
//        xml.openElement("configure")
//        xml.openElement("x")
        xml.append("<configure><x xmlns='jabber:x:data' type='submit'><field var='FORM_TYPE' type='hidden'><value>http://jabber.org/protocol/pubsub#node_config</value></field><field var='pubsub#access_model'><value>whitelist</value></field><field var='pubsub#publish_model'><value>publishers</value></field></x></configure>")
        return xml
    }

}


