package ooo.emessi.messenger.xmpp.extensions

import org.jivesoftware.smack.packet.ExtensionElement
import org.jivesoftware.smack.packet.XmlEnvironment
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider
import org.jivesoftware.smack.util.XmlStringBuilder

class MediaExtension : ExtensionElement {

    override fun toXML(xmlEnvironment: XmlEnvironment?): CharSequence {
        val xml = XmlStringBuilder(this).apply {
            attribute(ITEM, "someItem")
            rightAngleBracket()
            element("elemname", "elemstr")
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



}