package ooo.emessi.messenger.xmpp

import org.jivesoftware.smack.packet.ExtensionElement
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider
import org.jivesoftware.smack.util.XmlStringBuilder

class MediaExtension : ExtensionElement {
    companion object{
        val NAMESPACE = "bzm:media"
        val ELEMENT = "media"
        val ITEM = "item"

        class Provider: EmbeddedExtensionProvider<MediaExtension>() {
            override fun createReturnExtension(
                currentElement: String?,
                currentNamespace: String?,
                attributeMap: MutableMap<String, String>?,
                content: MutableList<out ExtensionElement>?
            ): MediaExtension {
                return MediaExtension()
            }

        }
    }


    override fun toXML(xmlEnvironment: XmlEnvironment?): CharSequence {
        val xml = XmlStringBuilder(this).apply {
            attribute(ITEM, "someItem")
            rightAngleBracket()
//            append("somestring")
            element("elemname", "elemstr")
//            closeElement("closeelem")
//            emptyElement("emptyelem")
//            escape("escape")
//
//            optAttribute("optatr", "optatrtext")
            closeElement(ELEMENT)
        }

        return xml
    }

    override fun getNamespace(): String {
        return NAMESPACE
    }

    override fun getElementName(): String {
        return ELEMENT
    }



}