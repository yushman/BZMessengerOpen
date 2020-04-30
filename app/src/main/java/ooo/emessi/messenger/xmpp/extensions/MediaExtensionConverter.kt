package ooo.emessi.messenger.xmpp.extensions

import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.utils.helpers.FileCategoryUtils
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.StandardExtensionElement

class MediaExtensionConverter(val message: Message) {
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

    private var mediaBuilder = StandardExtensionElement.builder(
        MEDIA_ELEMENT,
        MEDIA_NS
    )

    fun attachFilesToMessage(attachment: AttachmentDto): Message {
        mediaBuilder.addAttribute(MEDIA_version, MEDIA_VER.toString())
        when (attachment.type) {
            FileCategoryUtils.image -> attachImage(attachment)
            FileCategoryUtils.audio -> attachAudio(attachment)
            FileCategoryUtils.video -> attachVideo(attachment)
            FileCategoryUtils.document -> attachDocument(attachment)
            FileCategoryUtils.pdf -> attachPdf(attachment)
            FileCategoryUtils.table -> attachTable(attachment)
            FileCategoryUtils.presentation -> attachPresentation(attachment)
            FileCategoryUtils.archive -> attachArchive(attachment)
            FileCategoryUtils.file -> attachFile(attachment)
        }
        message.addExtension(mediaBuilder.build())
        return message
    }

    fun retrievFilesFromMessage(messageId: String): List<AttachmentDto> {
        val mediaExtension = message.getExtension<StandardExtensionElement>(
            MEDIA_ELEMENT,
            MEDIA_NS
        ) ?: return emptyList()
        val attachments = mutableListOf<AttachmentDto>()
        val items = mediaExtension.elements
        items.forEach {
            val category = it.getFirstElement(MEDIA_type).text
            val attachment = when (FileCategoryUtils.getCategoryByName(category)){
                FileCategoryUtils.image -> retrievImage(it)
                FileCategoryUtils.audio -> retrievAudio(it)
                FileCategoryUtils.video -> retrievVideo(it)
                FileCategoryUtils.document -> retrievDocument(it)
                FileCategoryUtils.pdf -> retrievPdf(it)
                FileCategoryUtils.table -> retrievTable(it)
                FileCategoryUtils.presentation -> retrievPresentation(it)
                FileCategoryUtils.archive -> retrievArchive(it)
                FileCategoryUtils.file -> retrievFile(it)
            }
            attachment.messageId = messageId
            attachments.add(attachment)
        }
        return attachments

    }

    private fun retrievFile(it: StandardExtensionElement): AttachmentDto {
        val hashAlgo = it.getFirstElement(MEDIA_ITEM_HASH_ELEMENT).attributes[MEDIA_algo]!!
        val hm = HashMap<String, String?>()
        val contents = it.elements
        contents.forEach { hm[it.elementName] = it.text }
        return AttachmentDto.fromHashMap(hm)
//        hm[MEDIA_algo] = hashAlgo
    }

    private fun retrievArchive(it: StandardExtensionElement): AttachmentDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievPresentation(it: StandardExtensionElement): AttachmentDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievTable(it: StandardExtensionElement): AttachmentDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievPdf(it: StandardExtensionElement): AttachmentDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievDocument(it: StandardExtensionElement): AttachmentDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievVideo(it: StandardExtensionElement): AttachmentDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievAudio(it: StandardExtensionElement): AttachmentDto {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun retrievImage(it: StandardExtensionElement): AttachmentDto {
//        val hashAlgo = it.getFirstElement(MEDIA_ITEM_HASH_ELEMENT).attributes[MEDIA_algo]!!
        val hm = HashMap<String, String?>()
        val contents = it.elements
        contents.forEach { hm[it.elementName] = it.text }
        val attachment =AttachmentDto.fromHashMap(hm)
        attachment.messageId = message.stanzaId
        return attachment
    }

    private fun attachFile(attachment: AttachmentDto) {
        val itemBuilder = getBaseChilds(attachment)
        mediaBuilder.addElement(itemBuilder.build())
    }

    private fun attachArchive(attachment: AttachmentDto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachPresentation(attachment: AttachmentDto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachTable(attachment: AttachmentDto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachPdf(attachment: AttachmentDto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachDocument(attachment: AttachmentDto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachVideo(attachment: AttachmentDto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachAudio(attachment: AttachmentDto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attachImage(attachment: AttachmentDto) {
        val itemBuilder = getBaseChilds(attachment)
        val previewBuilder = StandardExtensionElement.builder(
            MEDIA_ITEM_PREVIEW_ELEMENT,
            MEDIA_ITEM_PREVIEW_NS
        )
        previewBuilder.addAttribute(MEDIA_type, attachment.type.name)
        previewBuilder.addAttribute(MEDIA_width, attachment.height.toString())
        previewBuilder.addAttribute(MEDIA_width, attachment.width.toString())
        previewBuilder.setText(attachment.thumb)
        itemBuilder.addElement(previewBuilder.build())
        mediaBuilder.addElement(itemBuilder.build())

    }

    private fun getBaseChilds(attachment: AttachmentDto): StandardExtensionElement.Builder {
        val itemBuilder = StandardExtensionElement.builder(
            MEDIA_ITEM_ELEMENT,
            MEDIA_ITEM_NS
        )
        val hashBuilder = StandardExtensionElement.builder(
            MEDIA_ITEM_HASH_ELEMENT,
            MEDIA_ITEM_HASH_NS
        )
        itemBuilder.addElement(MEDIA_type, attachment.type.name)
        itemBuilder.addElement(MEDIA_name, attachment.attachmentName)
        itemBuilder.addElement(MEDIA_size, attachment.size.toString())
        itemBuilder.addElement(MEDIA_url, attachment.url)
        hashBuilder.addAttribute(
            MEDIA_algo,
            MEDIA_sha1
        )
        hashBuilder.setText(attachment.hash)
        itemBuilder.addElement(hashBuilder.build())
        return itemBuilder
    }
}