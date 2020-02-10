package ooo.emessi.messenger.xmpp

import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.FileAttachment
import ooo.emessi.messenger.data.model.bz_model.attachment.ImageAttachment
import ooo.emessi.messenger.utils.helpers.FileCategory
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.StandardExtensionElement

class MessageMediaConverter(val message: Message) {
    companion object{
        private const val MEDIA_VER = 1
        private const val MEDIA_NS = "bzm:media:1"
        private const val MEDIA_ITEM_NS = "bzm:file"
        private const val MEDIA_ITEM_HASH_NS = "urn:xmpp:hashes:2"
        private const val MEDIA_ITEM_PREVIEW_NS = "bzm:preview"
        private const val MEDIA_ITEM_PREVIEW_ELEMENT = "preview"
        private const val MEDIA_ELEMENT = "media"
        private const val MEDIA_ITEM_ELEMENT = "file"
        private const val MEDIA_ITEM_HASH_ELEMENT = "hash"
        private const val MEDIA_type = "media-type"
        private const val MEDIA_name = "name"
        private const val MEDIA_size = "size"
        private const val MEDIA_height = "height"
        private const val MEDIA_width = "width"
        private const val MEDIA_algo = "algo"
        private const val MEDIA_sha1 = "sha-1"
        private const val MEDIA_url = "url"
        private const val MEDIA_thumb = "thumb"
        private const val MEDIA_version = "ver"
    }

    var mediaBuilder= StandardExtensionElement.builder(MEDIA_ELEMENT, MEDIA_NS)

    fun attachFilesToMessage(files: List<ABZAttachment>): Message{
        mediaBuilder.addAttribute(MEDIA_version, MEDIA_VER.toString())
        files.forEach {
            val attachment = it
            val category = attachment.type
            when (category) {
                FileCategory.image -> attachImage(attachment)
                FileCategory.audio -> attachAudio(attachment)
                FileCategory.video -> attachVideo(attachment)
                FileCategory.document -> attachDocument(attachment)
                FileCategory.pdf -> attachPdf(attachment)
                FileCategory.table -> attachTable(attachment)
                FileCategory.presentation -> attachPresentation(attachment)
                FileCategory.archive -> attachArchive(attachment)
                FileCategory.file -> attachFile(attachment)
            }
        }
        message.addExtension(mediaBuilder.build())
        return message
    }

    fun retrievFilesFromMessage(messageId: String): List<ABZAttachment>{
        val mediaExtension = message.getExtension<StandardExtensionElement>(MEDIA_ELEMENT, MEDIA_NS) ?: null ?: return emptyList()
        val attachments = mutableListOf<ABZAttachment>()
        val items = mediaExtension.elements
        items.forEach {
            val category = it.getFirstElement(MEDIA_type).text
            val attachment = when (FileCategory.getCategoryByName(category)){
                FileCategory.image -> retrievImage(it)
                FileCategory.audio -> retrievAudio(it)
                FileCategory.video -> retrievVideo(it)
                FileCategory.document -> retrievDocument(it)
                FileCategory.pdf -> retrievPdf(it)
                FileCategory.table -> retrievTable(it)
                FileCategory.presentation -> retrievPresentation(it)
                FileCategory.archive -> retrievArchive(it)
                FileCategory.file -> retrievFile(it)
            }
            attachment.messageId = messageId
            attachments.add(attachment)
        }
        return attachments

    }

    private fun retrievFile(it: StandardExtensionElement) : ABZAttachment {
        val hashAlgo = it.getFirstElement(MEDIA_ITEM_HASH_ELEMENT).attributes[MEDIA_algo]!!
        val hm = HashMap<String, String?>()
        val contents = it.elements
        contents.forEach { hm[it.elementName] = it.text }
        return FileAttachment.fromHashMap(hm)
//        hm[MEDIA_algo] = hashAlgo
    }

    private fun retrievArchive(it: StandardExtensionElement) : ABZAttachment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievPresentation(it: StandardExtensionElement) : ABZAttachment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievTable(it: StandardExtensionElement) : ABZAttachment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievPdf(it: StandardExtensionElement) : ABZAttachment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievDocument(it: StandardExtensionElement) : ABZAttachment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievVideo(it: StandardExtensionElement) : ABZAttachment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievAudio(it: StandardExtensionElement) : ABZAttachment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievImage(it: StandardExtensionElement) : ABZAttachment {
//        val hashAlgo = it.getFirstElement(MEDIA_ITEM_HASH_ELEMENT).attributes[MEDIA_algo]!!
        val hm = HashMap<String, String?>()
        val contents = it.elements
        contents.forEach { hm[it.elementName] = it.text }
        val attachment = ImageAttachment.fromHashMap(hm)
        attachment.messageId = message.stanzaId
        return attachment
    }

    private fun attachFile(attachment: ABZAttachment) {
        val itemBuilder = getBaseChilds(attachment)
        mediaBuilder.addElement(itemBuilder.build())
    }

    private fun attachArchive(attachment: ABZAttachment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachPresentation(attachment: ABZAttachment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachTable(attachment: ABZAttachment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachPdf(attachment: ABZAttachment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachDocument(attachment: ABZAttachment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachVideo(attachment: ABZAttachment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachAudio(attachment: ABZAttachment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachImage(attachment: ABZAttachment) {
        if (attachment is ImageAttachment) {
            val itemBuilder = getBaseChilds(attachment)
            val previewBuilder = StandardExtensionElement.builder(MEDIA_ITEM_PREVIEW_ELEMENT, MEDIA_ITEM_PREVIEW_NS)
            previewBuilder.addAttribute(MEDIA_type, attachment.type.name)
            previewBuilder.addAttribute(MEDIA_width, attachment.height)
            previewBuilder.addAttribute(MEDIA_width, attachment.width)
            previewBuilder.setText(attachment.thumb)
            itemBuilder.addElement(previewBuilder.build())
            mediaBuilder.addElement(itemBuilder.build())
        }

    }

    private fun getBaseChilds(attachment: ABZAttachment): StandardExtensionElement.Builder {
        val itemBuilder = StandardExtensionElement.builder(MEDIA_ITEM_ELEMENT, MEDIA_ITEM_NS)
        val hashBuilder = StandardExtensionElement.builder(MEDIA_ITEM_HASH_ELEMENT, MEDIA_ITEM_HASH_NS)
        itemBuilder.addElement(MEDIA_type, attachment.type.name)
        itemBuilder.addElement(MEDIA_name, attachment.attachmentName)
        itemBuilder.addElement(MEDIA_size, attachment.size)
        itemBuilder.addElement(MEDIA_url, attachment.url)
        hashBuilder.addAttribute(MEDIA_algo, MEDIA_sha1)
        hashBuilder.setText(attachment.hash)
        itemBuilder.addElement(hashBuilder.build())
        return itemBuilder
    }
}